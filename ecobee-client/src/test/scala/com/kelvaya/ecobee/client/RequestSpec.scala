package com.kelvaya.ecobee.client

import com.kelvaya.ecobee.test.client.BaseTestSpec

import com.twitter.finagle.http.Fields


class RequestSpec extends BaseTestSpec {

  import deps.Implicits._

  val storage = this.createStorage()

  "All requests" must "include a JSON HTTP header" in {
    val req = this.runtime.unsafeRun(Request(account, Uri("/test-uri")).createRequest.provide(storage))
    req.headerMap.get(Fields.ContentType).value shouldBe "application/json;charset=utf-8"
  }

  they must "include an authorization header" in {
    val req = this.runtime.unsafeRun(Request(account, Uri("/test-uri")).createRequest.provide(storage))

    req.headerMap.get(Fields.Authorization).value shouldBe "Bearer " + AccessToken
  }

  they must "include the format querystring parameter set to 'json'" in {
    val req = this.runtime.unsafeRun(Request(account, Uri("/test-uri")).createRequest.provide(storage))
    req.params.get("format").value shouldBe "json"
  }
}
