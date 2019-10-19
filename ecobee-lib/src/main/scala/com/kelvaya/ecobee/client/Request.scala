package com.kelvaya.ecobee.client

import com.kelvaya.ecobee.client.storage.Tokens
import com.kelvaya.ecobee.client.storage.TokenStorage
import com.kelvaya.ecobee.client.storage.TokenStorageError
import com.kelvaya.ecobee.config.Settings

import scala.concurrent.Future
import scala.language.higherKinds

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling._
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.ContentType
import akka.http.scaladsl.model.HttpCharsets
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.HttpMethods
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.MediaType
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.model.headers.Authorization
import akka.http.scaladsl.model.headers.OAuth2BearerToken

import monix.eval.Coeval
import monix.eval.Task

import cats.Monad
import scala.concurrent.ExecutionContext


/** Factory methods for [[Request]] */
object Request {
  private val JsonSubType = "json"

  private[client] val ContentTypeJson = ContentType(MediaType.applicationWithOpenCharset(JsonSubType), HttpCharsets.`UTF-8`)

  /** Create a new [[AuthorizedRequest]] at the given URI.
    *
    * @param account The ID of the account for which the token request will be made
    * @param tokenStore The store of all API tokens
    * @param reqUri The URI of the HTTP request
    * @param querystring A list of querystrings to add to the request.  Defaults to an empty list.
    * @param reqEntity The body of the HTTP request.
    * @param exec (implicit) The `RequestExecutor` responsible for executing the HTTP request
    * @param settings (implicit) The application settings
    *
    * @tparam T Request entity type, which must be an `ApiObject`
    * @tparam F The monad container type that will hold results
    */
  def apply[T <: ApiObject : ToEntityMarshaller,F[_] : Monad](account: AccountID, tokenStore: Coeval[TokenStorage[F]], reqUri: Uri.Path,  querystring: List[Querystrings.Entry], reqEntity: T)
  (implicit settings: Settings) : Request[F,T] = apply(account, tokenStore, reqUri, querystring, Some(reqEntity))

  /** Create a new [[AuthorizedRequest]] at the given URI with an empty request body.
    *
    * @param account The ID of the account for which the token request will be made
    * @param tokenStore The store of all API tokens
    * @param reqUri The URI of the HTTP request
    * @param querystring A list of querystrings to add to the request.  Defaults to an empty list.
    * @param exec (implicit) The `RequestExecutor` responsible for executing the HTTP request
    * @param settings (implicit) The application settings
    *
    * @tparam F The monad type to contain operation results
    */
  def apply[F[_] : Monad](account: AccountID, tokenStore : Coeval[TokenStorage[F]], reqUri: Uri.Path, querystring: List[Querystrings.Entry] = List.empty)
  (implicit settings: Settings) : Request[F,ParameterlessApi] = apply(account, tokenStore, reqUri, querystring, None)


  private def apply[T <: ApiObject : ToEntityMarshaller,F[_] : Monad](account: AccountID, tokenStore : Coeval[TokenStorage[F]], reqUri: Uri.Path, querystring: List[Querystrings.Entry], reqEntity : Option[T])
  (implicit s: Settings) =
    new Request[F,T](account, tokenStore) with AuthorizedRequest[F,T] {
      val uri = reqUri
      val query = Coeval.pure(this.tokenIO.pure(querystring))
      val entity = reqEntity
    }

}

/** Ecobee API HTTP GET Request
  *
  * Used within an [[com.kelvaya.ecobee.client.service.EcobeeService]]
  *
  * @note All subclasses must implement [[#uri]], [[#query]], and [[#entity]].
  * This base class does not add authorization headers.  For that, you must mix-in [[AuthorizedRequest]].
  * For HTTP POST requests, mix-in [[PostRequest]].
  *
  * @param account The ID of the account for which the token request will be made
  * @param tokenStore The store of all API tokens
  * @param exec (implicit) The `RequestExecutor` responsible for sending the HTTP request
  * @param settings (implicit) Application settings
  * @param containerClass Instance of class that brings type `M` into the Monad Typeclass, used to contain the results of Request queries and operations.
  *
  * @tparam F The monad type containing the request
  * @tparam T Request entity type, which must be an `ApiObject`
  */
abstract class Request[F[_] : Monad,T <: ApiObject : ToEntityMarshaller](protected val account : AccountID, protected val tokenStore : Coeval[TokenStorage[F]])
(implicit settings : Settings) {
  import Request._

  private lazy val _serverRoot = settings.EcobeeServerRoot
  protected val tokenIO : Monad[F] = implicitly[Monad[F]]

  /** The service endpoint */
  val uri: Uri.Path

  /** The querystring parameters to be included in the request */
  val query: Coeval[F[List[Querystrings.Entry]]]

  /** The request body (if any) */
  val entity : Option[T]

  /** Creates a new Akka HTTP request to encapsulate the request to the Ecobee API
    *
    * Will return a [[RequestError]] if the request is malformed or unrecognized.
    */
  def createRequest : Task[F[Either[RequestError, HttpRequest]]] = {

    this.query.to[Task].flatMap { QS => 

      Task.deferFutureAction { implicit s =>
        
        val computedEntity = marshallAsJsonEntity(this.entity)

        computedEntity.map { entity =>
          tokenIO.map(QS) { qry =>
          
            val computedQuery = Uri.Query((Querystrings.JsonFormat :: qry).toSeq :_*)

            val req = HttpRequest(
              uri = _serverRoot.withPath(_serverRoot.path ++ uri).withQuery(computedQuery)
            ).withEntity(entity)

            // There are no expected errors at this time, so always return a "Right"
            Right(req) : Either[RequestError,HttpRequest]
          }
        }
      }
    }
  }

  private def marshallAsJsonEntity(entity : Option[T])(implicit ec : ExecutionContext) : Future[RequestEntity] = 
    entity
      .map(Marshal(_).to[MessageEntity].map(_.withContentType(ContentTypeJson)))
      .getOrElse(Future.successful(HttpEntity.empty(ContentTypeJson)))


  /** Returns the authorization code querystring parameter used during initial authorization */
  protected def getAuthCodeQS : Coeval[F[Option[Querystrings.Entry]]] = {
    import TokenStorageError._
    tokenStore.map { ts => 
      tokenIO.map(ts.getTokens(account)) { _ match {
        case Right(Tokens(Some(code),_,_)) => Some(("code", code))
        case Right(_)                      => None
        case Left(MissingTokenError)       => None
        case Left(e)                       => throw e // TODO: log error!
      }}
    }
  } 
  

  /** Returns the refresh token querystring parameter used during token refreshes */
  def getRefreshTokenQS : Coeval[F[Querystrings.Entry]] = {
    tokenStore.map { ts =>
      tokenIO.map(ts.getTokens(account)) { _ match { 
        case Right(Tokens(_,_,Some(token))) => (("refresh_token", token))
        case _                              => (("refresh_token", "")) // TODO: log error!
      }}
    }
  }

  /** Return a new Akka `Authorization` OAuth Bearer Token HTTP header */
  protected def generateAuthorizationHeader(): Coeval[F[Either[RequestError,Authorization]]] = {
    
    def getAccessToken(tokens : Tokens) = tokens.accessToken match {
      case None    => Left(RequestError.TokenAccessError(TokenStorageError.MissingTokenError) : RequestError)
      case Some(t) => Right(Authorization(OAuth2BearerToken(t)))
    }
    
    import cats.Monad.ops._
    tokenStore.map { store =>
      store.getTokens(account).map { tokenQuery =>
        val rTokenQuery = tokenQuery.left.map(RequestError.TokenAccessError(_) : RequestError)
        for {
          tokens      <- rTokenQuery
          accessToken <- getAccessToken(tokens)
        } yield (accessToken)
      } 
    }
  }
}

// ---------------------


/** [[Request]] with no entity */
abstract class RequestNoEntity[F[_] : Monad](account : AccountID, tokenStore : Coeval[TokenStorage[F]])
(implicit s : Settings) extends Request[F,ParameterlessApi](account, tokenStore) {
  val entity : Option[ParameterlessApi] = None
}


/** A mix-in trait which includes the authorization header in a [[Request]] */
trait AuthorizedRequest[F[_],T <: ApiObject] extends Request[F,T] { 
  abstract override def createRequest = { 
    
    // Task[F[Either[RequestError, HttpRequest]]]


    this.generateAuthorizationHeader.to[Task].flatMap { authF =>      
      super.createRequest.map { reqF =>
        this.tokenIO.flatMap(authF) { authE =>
          this.tokenIO.map(reqF) { reqE =>
            authE.flatMap { auth =>
              reqE.map { _.addHeader(auth) }
            }
          }
        }
      }
    }
  }
}


// ---------------------


/** A mix-in trait to make the [[Request]] an HTTP POST
  *
  * @tparam T Request entity type, which must be an `WriteableApiObject`
  */
trait PostRequest[F[_],T <: WriteableApiObject] extends Request[F,T] {
  abstract override def createRequest = super.createRequest.map { reqF =>
    this.tokenIO.map(reqF) { _.map { _.withMethod(HttpMethods.POST) }}
  }
}
