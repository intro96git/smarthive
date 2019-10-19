package com.kelvaya.ecobee.client

import com.kelvaya.ecobee.client.tokens._
import com.kelvaya.ecobee.client.service.ServiceError
import com.kelvaya.ecobee.config.Settings

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpRequest
import akka.stream.ActorMaterializer


import spray.json.JsonFormat
import zio.IO
import zio.ZIO


/** Ecobee REST API client
  *
  * @param settings (implicit) The application global settings  (from dependency injection, `DI`)
  * @param system (implicit) Akka Actor system for the HTTP service  (from dependency injection, `DI`)
  *
  * @see [[com.kelvaya.ecobee.client client]]
  */
final class Client(implicit settings: Settings, system: ActorSystem) extends RequestExecutor {
  private implicit val _materializer = ActorMaterializer()
  private implicit val _ec = system.dispatcher



  private lazy val _serverRoot = settings.EcobeeServerRoot

  def executeRequest[J : JsonFormat](req: ZIO[TokenStorage,RequestError, HttpRequest]): IO[ServiceError, J] = ???
}
