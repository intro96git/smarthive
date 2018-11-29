package com.kelvaya.ecobee.client

import com.kelvaya.ecobee.test.BaseTestSpec
import com.kelvaya.ecobee.test.TestDependencies

import java.nio.charset.StandardCharsets

import akka.actor.ActorSystem
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.model.headers

class EcobeeClientSpec extends BaseTestSpec {

  lazy val client = Client()

  "Requests" must "include a JSON HTTP header" in {
    val req = client.getRequest(Uri.Path("/test-uri"))

    req.header[headers.`Content-Type`].value.toString shouldBe "Content-Type: application/json; charset=UTF-8"
  }

  they must "include an authorization header" in {
    val req = client.getRequest(Uri.Path("/test-uri"))

    req.header[headers.`Authorization`].value.toString shouldBe "Authorization: Bearer " + MockAuthToken
  }

  they must "include the format querystring parameter set to 'json'" in {
    val req = client.getRequest(Uri.Path("/test-uri"))
    req.uri.query().get("format").value shouldBe "json"
  }

}