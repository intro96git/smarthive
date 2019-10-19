package com.kelvaya.ecobee.client

import com.kelvaya.ecobee.client.service.ServiceError
import com.kelvaya.ecobee.config.Settings
import com.kelvaya.ecobee.test.TestConstants


import akka.http.scaladsl.model.HttpRequest

import spray.json.JsObject
import spray.json.JsonFormat
import zio.ZIO
import zio.IO
import com.kelvaya.ecobee.client.tokens.TokenStorage


/** May be overkill?  At least, the executeRequest method may be overkill.  Do we ever really care to have actual
 *  responses in the tests?
 *
 */
class TestClient(storage : TokenStorage, responses : Map[HttpRequest, JsObject])(implicit settings : Settings)
extends RequestExecutor with TestConstants {

  def executeRequest[S : JsonFormat](taskReq : ZIO[TokenStorage,RequestError,HttpRequest]) : IO[ServiceError,S] = {
    taskReq
      .provide(storage)
      .catchAll(_ => IO.fail(ServiceError("invalid_request", "Unit Test Failure - Missing Request", "")))
      .flatMap { req =>
        val fixedReq = req.withUri(settings.EcobeeServerRoot)
        val response : Either[ServiceError,S] = responses.get(fixedReq)
          .map { json ⇒ Right(implicitly[JsonFormat[S]].read(json)) }
          .getOrElse(Left(ServiceError("invalid_request", s"Unit Test Failure - Missing Response for given request: $req", fixedReq.uri.toString)))
        ZIO.fromEither(response)
      }
  }
}
