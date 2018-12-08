package com.kelvaya.ecobee.client

import com.kelvaya.ecobee.config.Settings

import akka.http.scaladsl.model.ContentType
import akka.http.scaladsl.model.HttpCharsets
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.MediaType
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.model.HttpEntity

object Request {
  private val JsonSubType = "json"

  private[client] val ContentTypeJson = ContentType(MediaType.applicationWithOpenCharset(JsonSubType), HttpCharsets.`UTF-8`)

  def apply(reqUri: Uri.Path, querystring: Seq[Querystrings.Querystring] = Seq.empty, reqEntity: String = "")(implicit authorizer: RequestExecutor, settings: Settings) =
    new Request with AuthorizedRequest {
      val uri = reqUri
      val query = querystring
      val entity = Some(reqEntity)
    }
}


abstract class Request(implicit val exec: RequestExecutor, val settings: Settings) {
  import Request._

  private lazy val _serverRoot = settings.EcobeeServerRoot

  val uri: Uri.Path
  val query: Seq[Querystrings.Querystring]
  val entity: Option[String]

  def createRequest = {
    val computedQuery = if (this.query.size == 0) Uri.Query(None) else Uri.Query(this.query: _*)
    val computedEntity = this.entity.map(HttpEntity.apply(ContentTypeJson, _)).getOrElse(HttpEntity.Empty)

    HttpRequest(
      uri = _serverRoot.withPath(_serverRoot.path ++ uri).withQuery(Querystrings.JsonFormat +: computedQuery)
    ).withEntity(computedEntity)
  }

}


trait AuthorizedRequest extends Request {
  abstract override def createRequest = super.createRequest.addHeader(exec.generateAuthorizationHeader)
}