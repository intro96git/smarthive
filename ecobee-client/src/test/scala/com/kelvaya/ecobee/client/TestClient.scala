package com.kelvaya.ecobee.client

import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.HttpResponse

import spray.json.JsonFormat
import spray.json.JsObject

import zio.IO

/** Empty RequestExecutor used for mocking tests.  Should never be called. */
class TestClient extends RequestExecutor {

  def executeRequest[S:JsonFormat,E<:ServiceError](request: HttpRequest, err: JsObject => E, fail: (Throwable,Option[HttpResponse]) => E) : IO[E,S] = {    
    ???
  }
}
