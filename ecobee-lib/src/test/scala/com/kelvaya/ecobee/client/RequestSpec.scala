package com.kelvaya.ecobee.client

import com.kelvaya.ecobee.config.Settings
import com.kelvaya.ecobee.test.BaseTestSpec

import scala.concurrent.duration.Duration
import scala.reflect.ManifestFactory.classType

import org.scalactic.source.Position.apply

import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.model.headers
import monix.execution.Scheduler.Implicits.global

class RequestSpec extends BaseTestSpec {

  implicit lazy val settings = this.injector.instance[Settings]
  implicit lazy val executor = this.createTestExecutor(Map.empty)

  "All requests" must "include a JSON HTTP header" in {
    val req = Request(Uri.Path("/test-uri")).createRequest.runSyncUnsafe(Duration("1 second"))

    req.header[headers.`Content-Type`].value.toString shouldBe "Content-Type: application/json; charset=UTF-8"
  }

  they must "include an authorization header" in {
    val req = Request(Uri.Path("/test-uri")).createRequest.runSyncUnsafe(Duration("1 second"))

    req.header[headers.`Authorization`].value.toString shouldBe "Authorization: Bearer " + MockAuthToken
  }

  they must "include the format querystring parameter set to 'json'" in {
    val req = Request(Uri.Path("/test-uri")).createRequest.runSyncUnsafe(Duration("1 second"))
    req.uri.query().get("format").value shouldBe "json"
  }
}