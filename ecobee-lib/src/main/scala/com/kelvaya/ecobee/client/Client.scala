package com.kelvaya.ecobee.client

import com.kelvaya.ecobee.config.Settings

import com.google.inject.Inject
import com.google.inject.Injector

import akka.actor.ActorSystem
import akka.http.scaladsl.model.ContentType
import akka.http.scaladsl.model.HttpCharsets
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.MediaType
import akka.http.scaladsl.model.Uri
import akka.stream.ActorMaterializer
import com.kelvaya.ecobee.config.CanInject

/** Companion to [[Client]].
  *
  * @define injectedType Client
  */
object Client extends CanInject[Client] {
  private val JsonSubType = "json"
  private val ContentTypeJson = ContentType(MediaType.applicationWithOpenCharset(JsonSubType), HttpCharsets.`UTF-8`)
  private val JsonFormatQs = (("format", "json"))
}

final class Client @Inject() (authorizer: AuthorizationFactory, settings: Settings, system: ActorSystem) {
  import Client._

  private implicit val _sys = system
  private implicit val _materializer = ActorMaterializer()
  private implicit val _ec = system.dispatcher

  private lazy val _serverRoot = settings.EcobeeServerRoot

  // -------------------------------------------------------------------

  def getRequest(uri: Uri.Path, query: Uri.Query = Uri.Query(None), entity: String = "") = {
    HttpRequest(
      uri = _serverRoot.withPath(_serverRoot.path ++ uri).withQuery(JsonFormatQs +: query),
      headers = List(authorizer.generateAuthorizationHeader)).withEntity(ContentTypeJson, entity)
  }
}