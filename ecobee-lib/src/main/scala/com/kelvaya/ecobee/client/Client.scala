package com.kelvaya.ecobee.client

import com.kelvaya.ecobee.client.storage._
import com.kelvaya.ecobee.client.service.ServiceError
import com.kelvaya.ecobee.config.Settings

import scala.language.higherKinds

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpRequest
import akka.stream.ActorMaterializer

import cats.Monad

import monix.eval.Coeval
import monix.eval.Task

import spray.json.JsonFormat


/** Ecobee REST API client
  *
  * @param storage The backend which stores all authorization tokens
  * @param settings (implicit) The application global settings  (from dependency injection, `DI`)
  * @param system (implicit) Akka Actor system for the HTTP service  (from dependency injection, `DI`)
  *
  * @tparam F The monad holding the request and the token storage
  * @tparam M The container type that will hold results  (from dependency injection, `DI`)
  * 
  * @see [[com.kelvaya.ecobee.client client]]
  */
final class Client[F[_] : Monad,M[_]](storage : Coeval[TokenStorage[F]])(implicit settings: Settings, system: ActorSystem) extends RequestExecutor[F,M] {
  private implicit val _materializer = ActorMaterializer()
  private implicit val _ec = system.dispatcher



  private lazy val _serverRoot = settings.EcobeeServerRoot

  def executeRequest[J : JsonFormat](req: Task[F[Either[RequestError, HttpRequest]]]): M[Either[ServiceError, J]] = ???
}
