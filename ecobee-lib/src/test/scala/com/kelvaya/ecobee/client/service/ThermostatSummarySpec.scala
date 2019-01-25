package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.client.Client
import com.kelvaya.ecobee.client.Status
import com.kelvaya.ecobee.config.Settings
import com.kelvaya.ecobee.test.BaseTestSpec

import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.HttpMethods
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.Uri
import spray.json._

class ThermostatSummarySpec extends BaseTestSpec {

  implicit lazy val settings = this.injector.instance[Settings]
  implicit lazy val exec = this.createTestExecutor(Map.empty)
  implicit lazy val client = new Client

  "The thermostat summary service" must "serialize requests correctly" in {

    val selection = Selection()
    ThermostatSummaryService.execute(selection)

    val thermReq = new ThermostatSummaryRequest(selection)
    ThermostatSummaryService.execute(thermReq)

    val req: HttpRequest = thermReq.createRequest
    req.method shouldBe HttpMethods.GET
    req.entity shouldBe HttpEntity.Empty
    req.uri.path shouldBe Uri.Path("/authorize")
    req.uri.query() should contain theSameElementsAs(Seq(("response_type","ecobeePin"),("client_id",this.ClientId),("scope","smartWrite")))
  }


  it must "deserialize revision list responses correctly" in {
    val TestResponse = ThermostatSummaryResponse(??? : Seq[CSV], thermostatCount=1, ??? : Seq[CSV], ??? : Status)
    TestResponse.toJson shouldBe s"""{
      "ecobeePin": "${Pin}",
      "code": "${AuthCode}",
      "scope": "smartWrite",
      "expires_in": ${PinExpiration},
      "interval": ${PinInterval}
      }""".parseJson
  }

  it must "deserialize equipment status list responses correctly" in (pending)
}