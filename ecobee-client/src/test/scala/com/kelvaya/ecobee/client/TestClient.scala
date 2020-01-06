package com.kelvaya.ecobee.client

import spray.json.JsonFormat
import spray.json.JsObject

import com.twitter.finagle.http.{Request => HttpRequest}
import com.twitter.finagle.http.{Response => HttpResponse}

import zio.IO

/** Empty RequestExecutor used for mocking tests.  Should never be called. */
class TestClient extends RequestExecutor {

  val requestExecutor = new RequestExecutor.Service[Any] {
    def executeRequest[E<:ServiceError,S:JsonFormat](request: HttpRequest, err: JsObject => E, fail: (Throwable,Option[HttpResponse]) => E) : IO[E,S] = {    
      ???
    }
  }
}
