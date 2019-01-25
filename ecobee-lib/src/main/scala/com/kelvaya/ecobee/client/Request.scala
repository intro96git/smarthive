package com.kelvaya.ecobee.client

import com.kelvaya.ecobee.config.Settings

import akka.http.scaladsl.model.ContentType
import akka.http.scaladsl.model.HttpCharsets
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.MediaType
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.HttpMethods

object Request {
  private val JsonSubType = "json"

  private[client] val ContentTypeJson = ContentType(MediaType.applicationWithOpenCharset(JsonSubType), HttpCharsets.`UTF-8`)

  /** Create a new [[AuthorizedRequest]] at the given URI.
    *
    * @param reqUri The URI of the HTTP request
    * @param querystring A list of querystrings to add to the request.  Defaults to an empty list.
    * @param reqEntity The body of the HTTP request.  Defaults to an empty string.
    * @param authorizer (implicit) The `RequestExecutor` responsible for executing the HTTP request
    * @param settings (implicit) The application settings
    */
  def apply(reqUri: Uri.Path, querystring: List[Querystrings.Entry] = List.empty, reqEntity: String = "")
  (implicit authorizer: RequestExecutor, settings: Settings) =
    new Request with AuthorizedRequest {
      val uri = reqUri
      val query = querystring
      val entity = Some(reqEntity)
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
  */
abstract class Request(implicit val exec : RequestExecutor, val settings : Settings) {
  import Request._

  private lazy val _serverRoot = settings.EcobeeServerRoot


  /** The service endpoint */
  val uri: Uri.Path

  /** The querystring parameters to be included in the request */
  val query: List[Querystrings.Entry]

  /** The request body (if any) */
  val entity: Option[String]


  /** Creates a new Akka HTTP request to encapsulate the request to the Ecobee API */
  def createRequest = {
    val computedQuery = {
      val q1 = if (this.entity.isDefined) Querystrings.JsonFormat :: Nil else Nil
      val q2 = (if (this.query.size == 0) Nil else this.query) ++ q1
      if (q2.isEmpty) Uri.Query(None) else Uri.Query(q2.toSeq: _*)
    }
    val computedEntity = this.entity.map(HttpEntity.apply(ContentTypeJson, _)).getOrElse(HttpEntity.Empty)

    HttpRequest(
      uri = _serverRoot.withPath(_serverRoot.path ++ uri).withQuery(computedQuery)
    ).withEntity(computedEntity)
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


/** A mix-in trait which includes the authorization header in a [[Request]] */
trait AuthorizedRequest extends Request {
  abstract override def createRequest = super.createRequest.addHeader(exec.generateAuthorizationHeader)
}


// ---------------------


/** A mix-in trait to make the [[Request]] an HTTP POST */
trait PostRequest extends Request {
  abstract override def createRequest = super.createRequest.withMethod(HttpMethods.POST)
}