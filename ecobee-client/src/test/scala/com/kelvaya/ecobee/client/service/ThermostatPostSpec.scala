package com.kelvaya.ecobee.client.service


import com.kelvaya.ecobee.client.Electricity
import com.kelvaya.ecobee.client.ThermostatModification
import com.kelvaya.ecobee.test.client.BaseTestSpec

import spray.json._

import com.typesafe.scalalogging.Logger

import com.twitter.finagle.http.Method
import com.twitter.finagle.http.{Uri => HttpUri}
import com.twitter.finagle.http.Fields

class ThermostatPostSpec extends BaseTestSpec {


  import deps.Implicits._
  implicit val log = Logger[ThermostatPostSpec]

  lazy val TestThermostat = ThermostatModification("identifier", name = Some("Hello World"))
  lazy val TestFunction = ThermostatFunction("test", Electricity(Seq.empty))

  lazy val store = this.createStorage()

  "The request" must "serialize correctly for HTTP" in {
    val thermReq = ThermostatPostRequest(account, Select(SelectType.Registered), Some(TestThermostat), Some(Seq(TestFunction)))

    val expectedPayload = """
      {
        "functions" : [ { "type" : "test", "params": { "devices" : [] } }],
        "selection" : { "selectionType" : "registered" },
        "thermostat" : { "identifier" : "identifier", "name" : "Hello World" }
      }
      """.parseJson

    val req = this.runtime.unsafeRun(thermReq.createRequest.provide(store))
    req.method shouldBe Method.Post
    req.uri should startWith("/1/thermostat?")
    HttpUri.fromRequest(req).params.size shouldBe 1
    req.headerMap.get(Fields.Authorization) shouldBe 'defined
    req.headerMap(Fields.Authorization) shouldBe "Bearer " + AccessToken

    req.headerMap.get(Fields.ContentType).value shouldBe "application/json;charset=utf-8"
    req.contentString.parseJson shouldBe expectedPayload
  }
}
