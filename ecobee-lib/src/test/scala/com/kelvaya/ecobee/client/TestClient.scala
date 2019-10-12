package com.kelvaya.ecobee.client

import com.kelvaya.ecobee.client.storage._
import com.kelvaya.ecobee.client.service.ServiceError
import com.kelvaya.ecobee.config.Settings
import com.kelvaya.ecobee.test.TestConstants


import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpRequest
import monix.eval.Task
import spray.json.JsObject
import spray.json.JsonFormat

import cats.Id


/** May be overkill?  At least, the executeRequest method may be overkill.  Do we ever really care to have actual
 *  responses in the tests?
 *
 */
class TestClient(storage : TokenStorage[Id], responses : Map[HttpRequest, JsObject])(implicit settings : Settings, sys : ActorSystem)
extends BaseClient(storage : TokenStorage[Id]) with TestConstants {

  def executeRequest[S : JsonFormat](taskReq: Id[Either[RequestError, Task[HttpRequest]]]): Id[Either[ServiceError, S]] = {
    import monix.execution.Scheduler.Implicits.global
    taskReq
      .left.map(_ => ServiceError("invalid_request", "Unit Test Failure - Missing Request", ""))
      .flatMap(r => {
          val req = r.runSyncUnsafe(scala.concurrent.duration.Duration("1 second"))
          val fixedReq = req.withUri(settings.EcobeeServerRoot)
          val response : Either[ServiceError,S] = responses.get(fixedReq)
            .map { json ⇒ Right(implicitly[JsonFormat[S]].read(json)) }
            .getOrElse(Left(ServiceError("invalid_request", s"Unit Test Failure - Missing Response for given request: $req", fixedReq.uri.toString)))
          response
      })
  }
}
