package com.kelvaya.ecobee.client

import com.kelvaya.ecobee.test.BaseTestSpec

import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.model.headers

class RequestSpec extends BaseTestSpec {

  import deps.Implicits._

  val storage = this.createStorage()

  "All requests" must "include a JSON HTTP header" in {
    val req = this.runtime.unsafeRun(Request(account, Uri.Path("/test-uri")).createRequest.provide(storage))
    req.header[headers.`Content-Type`].value.toString shouldBe "Content-Type: application/json; charset=UTF-8"
  }

  they must "include an authorization header" in {
    val req = this.runtime.unsafeRun(Request(account, Uri.Path("/test-uri")).createRequest.provide(storage))

    req.header[headers.`Authorization`].value.toString shouldBe "Authorization: Bearer " + AccessToken
  }

  they must "include the format querystring parameter set to 'json'" in {
    val req = this.runtime.unsafeRun(Request(account, Uri.Path("/test-uri")).createRequest.provide(storage))
    req.uri.query().get("format").value shouldBe "json"
  }
}
