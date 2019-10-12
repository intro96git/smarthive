package com.kelvaya.ecobee.client

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
import monix.eval.Task
import cats.Monad


object Request {
  private val JsonSubType = "json"

  private[client] val ContentTypeJson = ContentType(MediaType.applicationWithOpenCharset(JsonSubType), HttpCharsets.`UTF-8`)

  /** Create a new [[AuthorizedRequest]] at the given URI.
    *
    * @param reqUri The URI of the HTTP request
    * @param querystring A list of querystrings to add to the request.  Defaults to an empty list.
    * @param reqEntity The body of the HTTP request.
    * @param exec (implicit) The `RequestExecutor` responsible for executing the HTTP request
    * @param settings (implicit) The application settings
    *
    * @tparam T Request entity type, which must be an `ApiObject`
    * @tparam M The monad container type that will hold results
    */
  def apply[T <: ApiObject : ToEntityMarshaller,M[_] : Monad](reqUri: Uri.Path,  querystring: List[Querystrings.Entry], reqEntity: T)
  (implicit exec: RequestExecutor[M], settings: Settings) : Request[M,T] = apply(reqUri, querystring, Some(reqEntity))

  /** Create a new [[AuthorizedRequest]] at the given URI with an empty request body.
    *
    * @param reqUri The URI of the HTTP request
    * @param querystring A list of querystrings to add to the request.  Defaults to an empty list.
    * @param exec (implicit) The `RequestExecutor` responsible for executing the HTTP request
    * @param settings (implicit) The application settings
    *
    * @tparam M The monad type to contain operation results
    */
  def apply[M[_] : Monad](reqUri: Uri.Path, querystring: List[Querystrings.Entry] = List.empty)
  (implicit exec: RequestExecutor[M], settings: Settings) : Request[M,ParameterlessApi] = apply(reqUri, querystring, None)



  private def apply[T <: ApiObject : ToEntityMarshaller,M[_] : Monad](reqUri: Uri.Path, querystring: List[Querystrings.Entry], reqEntity : Option[T])
  (implicit e: RequestExecutor[M], s: Settings) =
    new Request[M,T] with AuthorizedRequest[M,T] {
      val uri = reqUri
      val query = async.pure(querystring)
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
  * @param exec (implicit) The `RequestExecutor` responsible for sending the HTTP request
  * @param settings (implicit) Application settings
  * @param containerClass Instance of class that brings type `M` into the Monad Typeclass, used to contain the results of Request queries and operations.
  *
  * @tparam T Request entity type, which must be an `ApiObject`
  * @tparam M The monad container type that will hold results
  */
abstract class Request[M[_],T <: ApiObject : ToEntityMarshaller](implicit val exec : RequestExecutor[M], val settings : Settings, protected val async : Monad[M]) {
  import Request._

  private lazy val _serverRoot = settings.EcobeeServerRoot


  /** The service endpoint */
  val uri: Uri.Path

  /** The querystring parameters to be included in the request */
  val query: M[List[Querystrings.Entry]]

  /** The request body (if any) */
  val entity : Option[T]

  /** Creates a new Akka HTTP request to encapsulate the request to the Ecobee API
    *
    * Will return a [[RequestError]] if the request is malformed or unrecognized.
    */
  def createRequest : M[Either[RequestError, Task[HttpRequest]]] = {
    async.map(this.query) { qry ⇒

      val tsk = Task.deferFutureAction { implicit s ⇒
        val computedQuery = {
          val q1 = (if (qry.size == 0) Nil else qry) ++ (Querystrings.JsonFormat :: Nil)
          if (q1.isEmpty) Uri.Query(None) else Uri.Query(q1.toSeq : _*)
        }

        val computedEntity = this.entity.map(Marshal(_).to[MessageEntity].map(_.withContentType(ContentTypeJson)))
          .getOrElse(Future.successful(HttpEntity.empty(ContentTypeJson)))

        computedEntity.map { e ⇒
          HttpRequest(
            uri = _serverRoot.withPath(_serverRoot.path ++ uri).withQuery(computedQuery)
          ).withEntity(e)
        }
      }

      (Right[RequestError,Task[HttpRequest]](tsk) : Either[RequestError,Task[HttpRequest]])
    }
  }

  /** Returns the authorization code querystring parameter used during initial authorization */
  def getAuthCodeQs : M[Option[Querystrings.Entry]] = {
    import RequestError._
    import TokenStorageError._
    async.map(exec.getAuthCode) { 
      case Left(TokenAccessError(MissingTokenError)) => None
      case Right(code)                               => Some(("code", code))
      case Left(e)                                   => throw e // TODO: log error!
    }
  } 
  
  // map {  }


  /** Returns the refresh token querystring parameter used during token refreshes */
  def getRefreshTokenQs : M[Querystrings.Entry] = {
    async.map(exec.getRefreshToken) { 
      case Left(_)      => (("refresh_token", "")) // TODO: log error!
      case Right(token) => (("refresh_token", token))
    }
  }
}


// ---------------------


/** [[Request]] with no entity */
abstract class RequestNoEntity[M[_] : Monad](implicit exec : RequestExecutor[M], settings : Settings) extends Request[M,ParameterlessApi] {
  val entity : Option[ParameterlessApi] = None
}


/** A mix-in trait which includes the authorization header in a [[Request]] */
trait AuthorizedRequest[M[_],T <: ApiObject] extends Request[M,T] { 

  abstract override def createRequest = async.flatMap(exec.generateAuthorizationHeader) {
    case Left(e)    => async.pure(Left(e))
    case Right(hdr) => async.map(super.createRequest) { _.map { req => req.map(_.addHeader(hdr)) }}
  }
}


// ---------------------


/** A mix-in trait to make the [[Request]] an HTTP POST
  *
  * @tparam T Request entity type, which must be an `WriteableApiObject`
  */
trait PostRequest[M[_],T <: WriteableApiObject] extends Request[M,T] {
  abstract override def createRequest = async.map(super.createRequest) { _.map { tsk => tsk.map(_.withMethod(HttpMethods.POST)) }}
}
