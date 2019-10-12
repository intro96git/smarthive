package com.kelvaya.ecobee.client

import com.kelvaya.ecobee.test.BaseTestSpec

import scala.concurrent.duration.Duration

import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.model.headers
import monix.execution.Scheduler.Implicits.global

class RequestSpec extends BaseTestSpec {

  import deps.Implicits._


  "All requests" must "include a JSON HTTP header" in {
    val req = Request(account, Uri.Path("/test-uri")).createRequest.runSyncUnsafe(Duration("1 second"))
    req.header[headers.`Content-Type`].value.toString shouldBe "Content-Type: application/json; charset=UTF-8"
  }

  they must "include an authorization header" in {
    val req = Request(account, Uri.Path("/test-uri")).createRequest.runSyncUnsafe(Duration("1 second"))

    req.header[headers.`Authorization`].value.toString shouldBe "Authorization: Bearer " + AccessToken
  }

  they must "include the format querystring parameter set to 'json'" in {
    val req = Request(account, Uri.Path("/test-uri")).createRequest.runSyncUnsafe(Duration("1 second"))
    req.uri.query().get("format").value shouldBe "json"
  }
}
