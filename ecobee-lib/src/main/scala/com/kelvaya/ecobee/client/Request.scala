package com.kelvaya.ecobee.client

import com.kelvaya.ecobee.client.tokens.Tokens
import com.kelvaya.ecobee.client.tokens.TokenStorage
import com.kelvaya.ecobee.client.tokens.TokenStorageError
import com.kelvaya.ecobee.config.Settings

import scala.concurrent.Future

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling._
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.ContentType
import akka.http.scaladsl.model.HttpCharsets
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.MediaType
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.model.headers.Authorization
import akka.http.scaladsl.model.headers.OAuth2BearerToken

import scala.concurrent.ExecutionContext

import zio.IO
import zio.Task
import zio.UIO
import zio.ZIO

import spray.json.SerializationException

/** Factory methods for [[Request]] */
object Request {
  private val JsonSubType = "json"

  private[client] val ContentTypeJson = ContentType(
    MediaType.applicationWithOpenCharset(JsonSubType),
    HttpCharsets.`UTF-8`
  )

  /** Create a new [[AuthorizedRequest]] at the given URI.
    *
    * @param account The ID of the account for which the token request will be made
    * @param reqUri The URI of the HTTP request
    * @param querystring A list of querystrings to add to the request.  Defaults to an empty list.
    * @param reqEntity The body of the HTTP request.
    * @param settings (implicit) The application settings
    *
    * @tparam T Request entity type, which must be an `ApiObject`
    */
  def apply[T <: ApiObject: ToEntityMarshaller](
      account: AccountID,
      reqUri: Uri.Path,
      querystring: List[Querystrings.Entry],
      reqEntity: T
  )(implicit settings: Settings): Request[T] =
    apply(account, reqUri, querystring, Some(reqEntity))

  /** Create a new [[AuthorizedRequest]] at the given URI with an empty request body.
    *
    * @param account The ID of the account for which the token request will be made
    * @param reqUri The URI of the HTTP request
    * @param querystring A list of querystrings to add to the request.  Defaults to an empty list.
    * @param settings (implicit) The application settings
    */
  def apply(
      account: AccountID,
      reqUri: Uri.Path,
      querystring: List[Querystrings.Entry] = List.empty
  )(implicit settings: Settings): Request[ParameterlessApi] =
    apply(account, reqUri, querystring, None)

  private def apply[T <: ApiObject: ToEntityMarshaller](
      account: AccountID,
      reqUri: Uri.Path,
      querystring: List[Querystrings.Entry],
      reqEntity: Option[T]
  )(implicit s: Settings) =
    new Request[T](account) with AuthorizedRequest[T] {
      val uri = reqUri
      val query = UIO(querystring)
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
  * @param settings (implicit) Application settings
  *
  * @tparam T Request entity type, which must be an `ApiObject`
  */
abstract class Request[T <: ApiObject: ToEntityMarshaller](protected val account: AccountID)(implicit settings: Settings) {
  import Request._

  private lazy val _serverRoot = settings.EcobeeServerRoot

  /** The service endpoint */
  val uri: Uri.Path

  /** The querystring parameters to be included in the request */
  val query: TokenStorage.IO[List[Querystrings.Entry]]

  /** The request body (if any) */
  val entity: Option[T]

  /** Creates a new Akka HTTP request to encapsulate the request to the Ecobee API
    *
    * Will return a [[RequestError]] if the request is malformed or unrecognized.
    */
  def createRequest: ZIO[TokenStorage, RequestError, HttpRequest] = {
    this.query
      .flatMap { qry =>
        Task.fromFuture { implicit s =>
          val computedEntity = marshallAsJsonEntity(this.entity)

          computedEntity.map { entity =>
            val computedQuery =
              Uri.Query((Querystrings.JsonFormat :: qry).toSeq: _*)

            HttpRequest(
              uri = _serverRoot
                .withPath(_serverRoot.path ++ uri)
                .withQuery(computedQuery)
            ).withEntity(entity)
          }
        }
      }
      .catchAll {
        case e: RequestError      => ZIO.fail(e)
        case e: TokenStorageError => ZIO.fail(RequestError.TokenAccessError(e))
        case e: SerializationException =>
          ZIO.fail(RequestError.SerializationError(e))
      }
  }

  private def marshallAsJsonEntity(
      entity: Option[T]
  )(implicit ec: ExecutionContext): Future[RequestEntity] =
    entity
      .map(Marshal(_).to[MessageEntity].map(_.withContentType(ContentTypeJson)))
      .getOrElse(Future.successful(HttpEntity.empty(ContentTypeJson)))

  /** Returns the authorization code querystring parameter used during initial authorization */
  protected def getAuthCodeQS: TokenStorage.IO[Option[Querystrings.Entry]] = {
    for {
      ts <- ZIO.environment[TokenStorage]
      tok <- ts.tokenStorage.getTokens(account)
    } yield tok match {
      case Tokens(Some(code), _, _) => Some(("code", code))
      case _                        => None
    }
  }

  /** Returns the refresh token querystring parameter used during token refreshes */
  def getRefreshTokenQS: TokenStorage.IO[Querystrings.Entry] = {
    for {
      ts <- ZIO.environment[TokenStorage]
      tok <- ts.tokenStorage.getTokens(account)
    } yield tok match {
      case Tokens(_, _, Some(token)) => (("refresh_token", token))
      case _                         => (("refresh_token", "")) // TODO: log error!
    }
  }

  /** Return a new Akka `AuthoriTokensRequestzation` OAuth Bearer Token HTTP header */
  protected def generateAuthorizationTokensRequestHeader()
      : ZIO[TokenStorage, RequestError, Authorization] = {

    def getAccessToken(tokens: Tokens) = tokens.accessToken match {
      case None    => ZIO.fail(TokenStorageError.MissingTokenError)
      case Some(t) => ZIO.succeed(Authorization(OAuth2BearerToken(t)))
    }

    val accessToken =
      for {
        ts  <- ZIO.environment[TokenStorage]
        tok <- ts.tokenStorage.getTokens(account)
        at  <- getAccessToken(tok)
      } yield at

    accessToken.catchAll {
      case e: TokenStorageError => IO.fail(RequestError.TokenAccessError(e))
    }
  }
}

// ---------------------

/** [[Request]] with no entity */
abstract class RequestNoEntity(account: AccountID)(implicit s: Settings)
    extends Request[ParameterlessApi](account) {
  val entity: Option[ParameterlessApi] = None
}

/** A mix-in trait which includes the authorization header in a [[Request]] */
trait AuthorizedRequest[T <: ApiObject] extends Request[T] {
  abstract override def createRequest = {
    for {
      hdr <- this.generateAuthorizationTokensRequestHeader  
      req <- super.createRequest
    } 
    yield req.addHeader(hdr)
  }
}

// ---------------------

/** A mix-in trait to make the [[Request]] an HTTP POST
  *
  * @tparam T Request entity type, which must be an `WriteableApiObject`
  */
trait PostRequest[T <: WriteableApiObject] extends Request[T] {
  abstract override def createRequest = super.createRequest.map(_.withMethod(HttpMethods.POST))
}
