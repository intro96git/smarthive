package com.kelvaya.ecobee.client

import com.kelvaya.ecobee.client.service.ServiceError
import com.kelvaya.ecobee.config.Settings
import com.kelvaya.ecobee.test.TestConstants


import akka.http.scaladsl.model.HttpRequest
import monix.eval.Task
import spray.json.JsObject
import spray.json.JsonFormat

import cats.Id


/** May be overkill?  At least, the executeRequest method may be overkill.  Do we ever really care to have actual
 *  responses in the tests?
 *
 */
class TestClient(responses : Map[HttpRequest, JsObject])(implicit settings : Settings)
extends RequestExecutor[Id,Id] with TestConstants {

  def executeRequest[S : JsonFormat](taskReq: Task[Id[Either[RequestError, HttpRequest]]]): Id[Either[ServiceError, S]] = {
    import monix.execution.Scheduler.Implicits.global
    taskReq
      .map { reqE =>
        reqE
          .left.map( _ => ServiceError("invalid_request", "Unit Test Failure - Missing Request", ""))
          .flatMap { req =>
            val fixedReq = req.withUri(settings.EcobeeServerRoot)
            val response : Either[ServiceError,S] = responses.get(fixedReq)
              .map { json ⇒ Right(implicitly[JsonFormat[S]].read(json)) }
              .getOrElse(Left(ServiceError("invalid_request", s"Unit Test Failure - Missing Response for given request: $req", fixedReq.uri.toString)))
            response
          }
      }
      .runSyncUnsafe()
  }
}
