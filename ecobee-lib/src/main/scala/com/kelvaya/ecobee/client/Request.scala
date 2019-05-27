package com.kelvaya.ecobee.client

import com.kelvaya.ecobee.config.Settings

import scala.concurrent.Future

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
    *
    * @tparam T Request entity type, which must be an `ApiObject`
    */
  def apply[T <: ApiObject : ToEntityMarshaller](reqUri: Uri.Path,  querystring: List[Querystrings.Entry], reqEntity: T)
  (implicit authorizer: RequestExecutor, settings: Settings) : Request[T] = apply(reqUri, querystring, Some(reqEntity))

  /** Create a new [[AuthorizedRequest]] at the given URI with an empty request body.
    *
    * @param reqUri The URI of the HTTP request
    * @param querystring A list of querystrings to add to the request.  Defaults to an empty list.
    * @param authorizer (implicit) The `RequestExecutor` responsible for executing the HTTP request
    * @param settings (implicit) The application settings
    */
  def apply(reqUri: Uri.Path, querystring: List[Querystrings.Entry] = List.empty)
  (implicit authorizer: RequestExecutor, settings: Settings) : Request[ParameterlessApi] = apply(reqUri, querystring, None)



  private def apply[T <: ApiObject : ToEntityMarshaller](reqUri: Uri.Path, querystring: List[Querystrings.Entry], reqEntity : Option[T])
  (implicit authorizer: RequestExecutor, settings: Settings) =
    new Request[T] with AuthorizedRequest[T] {
      val uri = reqUri
      val query = querystring
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
  *
  * @tparam T Request entity type, which must be an `ApiObject`
  */
abstract class Request[T <: ApiObject : ToEntityMarshaller](implicit val exec : RequestExecutor, val settings : Settings) {
  import Request._

  private lazy val _serverRoot = settings.EcobeeServerRoot


  /** The service endpoint */
  val uri: Uri.Path

  /** The querystring parameters to be included in the request */
  val query: List[Querystrings.Entry]

  /** The request body (if any) */
  val entity : Option[T]


  /** Creates a new Akka HTTP request to encapsulate the request to the Ecobee API */
  def createRequest = {
    Task.deferFutureAction { implicit s ⇒
      val computedQuery = {
        val q1 = (if (this.query.size == 0) Nil else this.query) ++ (Querystrings.JsonFormat :: Nil)
        if (q1.isEmpty) Uri.Query(None) else Uri.Query(q1.toSeq : _*)
      }

      val computedEntity = this.entity.map(Marshal(_).to[MessageEntity]).getOrElse(Future.successful(HttpEntity.empty(ContentTypeJson)))

      computedEntity.map { e ⇒
        HttpRequest(
          uri = _serverRoot.withPath(_serverRoot.path ++ uri).withQuery(computedQuery)
        ).withEntity(e)
      }
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


/** [[Request]] with no entity
  *
  * @inheritdoc
  */
abstract class RequestNoEntity(implicit exec : RequestExecutor, settings : Settings) extends Request[ParameterlessApi] {
  val entity : Option[ParameterlessApi] = None
}


/** A mix-in trait which includes the authorization header in a [[Request]] */
trait AuthorizedRequest[T <: ApiObject] extends Request[T] {
  abstract override def createRequest = super.createRequest.map(_.addHeader(exec.generateAuthorizationHeader))
}


// ---------------------


/** A mix-in trait to make the [[Request]] an HTTP POST
  *
  * @tparam T Request entity type, which must be an `WriteableApiObject`
  */
trait PostRequest[T <: WriteableApiObject] extends Request[T] {
  abstract override def createRequest = {
    val req = super.createRequest
    req.map(_.withMethod(HttpMethods.POST))
  }
}