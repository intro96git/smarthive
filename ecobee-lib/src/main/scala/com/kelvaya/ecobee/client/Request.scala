package com.kelvaya.ecobee.client

import com.kelvaya.ecobee.config.Settings

import akka.http.scaladsl.model.ContentType
import akka.http.scaladsl.model.HttpCharsets
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.MediaType
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.HttpMethods
import spray.json.JsObject
import scala.util.Right
import spray.json.JsonWriter
import spray.json.RootJsonWriter
import akka.http.scaladsl.model._
import akka.http.scaladsl.marshalling._
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

object Request {
  private val JsonSubType = "json"

  private[client] val ContentTypeJson = ContentType(MediaType.applicationWithOpenCharset(JsonSubType), HttpCharsets.`UTF-8`)

  /** Create a new [[AuthorizedRequest]] at the given URI.
    *
    * @param reqUri The URI of the HTTP request
    * @param querystring A list of querystrings to add to the request.  Defaults to an empty list.
    * @param reqEntity The body of the HTTP request.
    * @param authorizer (implicit) The `RequestExecutor` responsible for executing the HTTP request
    * @param settings (implicit) The application settings
    */
  def apply[T : ToEntityMarshaller](reqUri: Uri.Path, querystring: List[Querystrings.Entry] = List.empty, reqEntity: T)
  (implicit authorizer: RequestExecutor, settings: Settings, ec : ExecutionContext) : Request[T] =
    new Request[T] with AuthorizedRequest[T] {
      val uri = reqUri
      val query = querystring
      val entity = Some(reqEntity)
    }

  /** Create a new [[AuthorizedRequest]] at the given URI with an empty request body.
    *
    * @param reqUri The URI of the HTTP request
    * @param querystring A list of querystrings to add to the request.  Defaults to an empty list.
    * @param reqEntity The body of the HTTP request.
    * @param authorizer (implicit) The `RequestExecutor` responsible for executing the HTTP request
    * @param settings (implicit) The application settings
    */
  def apply(reqUri: Uri.Path, querystring: List[Querystrings.Entry] = List.empty)
  (implicit authorizer: RequestExecutor, settings: Settings, ec : ExecutionContext) : Request[String] = apply(reqUri, querystring, "")
}

/** Ecobee API HTTP GET Request
  *
  * Used within an [[com.kelvaya.ecobee.client.service.EcobeeService]]
  *
  * @note All subclasses must implement [[#uri]], [[#query]], [[#entity]], and [[#jsonBody]].
  * This base class does not add authorization headers.  For that, you must mix-in [[AuthorizedRequest]].
  * For HTTP POST requests, mix-in [[PostRequest]].
  *
  * @param exec (implicit) The `RequestExecutor` responsible for sending the HTTP request
  * @param settings (implicit) Application settings
  */
abstract class Request[T : ToEntityMarshaller](implicit val exec : RequestExecutor, val settings : Settings) {
  import Request._

  private lazy val _serverRoot = settings.EcobeeServerRoot
  protected implicit val ec = exec.ec


  /** The service endpoint */
  val uri: Uri.Path

  /** The querystring parameters to be included in the request */
  val query: List[Querystrings.Entry]

  /** The request body (if any) */
  val entity : Option[T]


  /** Creates a new Akka HTTP request to encapsulate the request to the Ecobee API */
  def createRequest = {
    val computedQuery = {
      val q1 = if (this.entity.isDefined) Querystrings.JsonFormat :: Nil else Nil
      val q2 = (if (this.query.size == 0) Nil else this.query) ++ q1
      if (q2.isEmpty) Uri.Query(None) else Uri.Query(q2.toSeq: _*)
    }
    val computedEntity = this.entity.map(Marshal(_).to[MessageEntity]).getOrElse(Future.successful(HttpEntity.Empty))

    computedEntity.map { e =>
      HttpRequest(
        uri = _serverRoot.withPath(_serverRoot.path ++ uri).withQuery(computedQuery)
      ).withEntity(e)
    }
  }

  /** Returns the authorization code querystring parameter used during initial authorization */
  def getAuthCodeQs : Option[Querystrings.Entry] = exec.getAuthCode map { (("code", _)) }


  /** Returns the refresh token querystring parameter used during token refreshes */
  def getRefreshTokenQs : Querystrings.Entry = {
    val token = exec.getRefreshToken.getOrElse("")
    (("refresh_token", token))
  }
}


// ---------------------


abstract class RequestNoEntity(implicit exec : RequestExecutor, settings : Settings) extends Request[String] {
  val entity : Option[String] = None
}


/** A mix-in trait which includes the authorization header in a [[Request]] */
trait AuthorizedRequest[T] extends Request[T] {
  abstract override def createRequest = super.createRequest.map(_.addHeader(exec.generateAuthorizationHeader))
}


// ---------------------


/** A mix-in trait to make the [[Request]] an HTTP POST */
trait PostRequest[T] extends Request[T] {
  abstract override def createRequest = {
    val req = super.createRequest
    req.map(_.withMethod(HttpMethods.POST))
  }
}