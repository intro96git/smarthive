package com.kelvaya.ecobee.client

import com.kelvaya.ecobee.client.tokens.Tokens
import com.kelvaya.ecobee.client.tokens.TokenStorage
import com.kelvaya.ecobee.client.tokens.TokenStorageError

import com.twitter.finagle.http.Method
import com.twitter.finagle.http.{Request => HttpRequest}
import com.twitter.finagle.http.RequestBuilder
import com.twitter.io.Buf

import zio.IO
import zio.Task
import zio.UIO
import zio.ZIO

import spray.json.JsValue
import spray.json.RootJsonFormat
import spray.json.SerializationException

/** Factory methods for [[Request]] */
object Request {

  /** Create a new [[AuthorizedRequest]] at the given URI.
    *
    * @param account The ID of the account for which the token request will be made
    * @param reqUri The URI of the HTTP request
    * @param querystring A list of querystrings to add to the request.
    * @param queryBodyParam The "body" parameter used on the request querystring.
    * @param reqEntity The body of the HTTP request.
    * @param settings (implicit) The application settings
    *
    * @tparam T Request entity type, which must be an `ApiObject`
    */
  def apply[T <: ApiObject: RootJsonFormat](
      account: AccountID,
      reqUri: Uri,
      querystring: List[Querystrings.Entry],
      queryBodyParam : Option[String],
      reqEntity: T
  )(implicit settings: ClientSettings.Service[Any]): Request[T] =
    apply(account, reqUri, querystring, queryBodyParam, Some(reqEntity))

  /** Create a new [[AuthorizedRequest]] at the given URI with an empty request body.
    *
    * @param account The ID of the account for which the token request will be made
    * @param reqUri The URI of the HTTP request
    * @param querystring A list of querystrings to add to the request.  Defaults to an empty list.
    * @param queryBodyParam The "body" parameter used on the request querystring.  Defaults to None.
    * @param settings (implicit) The application settings
    */
  def apply(
      account: AccountID,
      reqUri: Uri,
      querystring: List[Querystrings.Entry] = List.empty,
      queryBodyParam : Option[String] = None
  )(implicit settings: ClientSettings.Service[Any]): Request[ParameterlessApi] =
    apply(account, reqUri, querystring, queryBodyParam, None)

  private def apply[T <: ApiObject: RootJsonFormat](
      accountId: AccountID,
      reqUri: Uri,
      querystring: List[Querystrings.Entry],
      queryBodyParam : Option[String],
      reqEntity: Option[T]
  )(implicit s: ClientSettings.Service[Any]) =
    new Request[T] with AuthorizedRequest[T] {
      val account = accountId
      val uri = reqUri
      val query = UIO(querystring)
      val queryBody = UIO(queryBodyParam)
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
abstract class Request[T <: ApiObject: RootJsonFormat](implicit protected val s: ClientSettings.Service[Any]) {

  /** Server root URL */
  val serverRoot = s.EcobeeServerRoot

  /** The service endpoint */
  val uri: Uri

  /** The querystring parameters to be included in the request */
  val query: TokenStorage.IO[List[Querystrings.Entry]]

  /** The "body" parameter of the querystring */
  val queryBody : TokenStorage.IO[Option[String]]

  /** The request body (if any) */
  val entity: Option[T]

  /** Creates a new Akka HTTP request to encapsulate the request to the Ecobee API
    *
    * Will return a [[RequestError]] if the request is malformed or unrecognized.
    */
  def createRequest: ZIO[TokenStorage, RequestError, HttpRequest] = {
    val req = for {
      qry     <- this.query
      qryBody <- this.queryBody
      ent     <- Task(this.entity.map(e => implicitly[RootJsonFormat[T]].write(e)))
      r       <- buildRequest(qry, qryBody, ent)
    } yield r
    
    req.catchAll {
        case e: RequestError           => ZIO.fail(e)
        case e: TokenStorageError      => ZIO.fail(RequestError.TokenAccessError(e))
        case e: SerializationException => ZIO.fail(RequestError.SerializationError(e))
        case e                         => ZIO.die(e)
      }
  }

  private def buildRequest(qs : List[Querystrings.Entry], qsBody : Option[String], entity : Option[JsValue]) = Task {
    val bodyReq = List(qsBody.map("body" -> _)).flatten
    val baseReq = HttpRequest(serverRoot + this.uri.uri, (Querystrings.JsonFormat :: bodyReq ::: qs).toSeq: _*)
    val url = new java.net.URL(baseReq.uri)
    val content = entity.map(j => Buf.Utf8(j.compactPrint))
    val req = RequestBuilder().url(url).build(Method.Get, content)
    req.setContentTypeJson()
    req
  }
}

// ---------------------

/** [[Request]] with no entity */
abstract class RequestNoEntity(implicit s: ClientSettings.Service[Any]) extends Request[ParameterlessApi] {
  val entity: Option[ParameterlessApi] = None
}

/** A mix-in trait which includes the authorization header in a [[Request]] */
trait AuthorizedRequest[T <: ApiObject] extends Request[T] {
  val account : AccountID

  override val serverRoot = new java.net.URL(s.EcobeeServerRoot.toString + "/" + s.EcobeeApiVersion)
  
  abstract override def createRequest = {
    for {
      hdr <- this.generateAuthorizationTokensRequestHeader  
      req <- super.createRequest
    } yield {
      req.authorization = hdr.auth
      req
    }
  }
  
  /** Return a new Akka `AuthorizationTokensRequest` OAuth Bearer Token HTTP header */
  private def generateAuthorizationTokensRequestHeader(): ZIO[TokenStorage, RequestError, OAuthHeader] = {

    def getAccessToken(tokens: Tokens) = tokens.accessToken match {
      case None    => ZIO.fail(TokenStorageError.MissingTokenError)
      case Some(t) => ZIO.succeed(OAuthHeader(t))
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
  }}

// ---------------------

/** A mix-in trait to make the [[Request]] an HTTP POST
  *
  * @tparam T Request entity type, which must be an `WriteableApiObject`
  */
trait PostRequest[T <: WriteableApiObject] extends Request[T] {
  abstract override def createRequest = super.createRequest.map { r =>
    r.method = Method.Post
    r
  }
}
