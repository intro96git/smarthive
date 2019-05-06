package com.kelvaya.ecobee.client

import com.kelvaya.ecobee.config.Settings

import scala.language.higherKinds

import com.google.inject.Inject

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpRequest
import akka.stream.ActorMaterializer
import spray.json.JsonFormat
import com.kelvaya.ecobee.client.service.ServiceError
import com.kelvaya.util.Realizer
import scala.concurrent.Future

final class Client @Inject() (implicit exec: RequestExecutor, settings: Settings, system: ActorSystem) {
  private implicit val _materializer = ActorMaterializer()
  private implicit val _ec = system.dispatcher

  private lazy val _serverRoot = settings.EcobeeServerRoot


  def executeRequest[R[_] : Realizer,S : JsonFormat](req : Future[HttpRequest]) = exec.executeRequest[R,S](req)
}