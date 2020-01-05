package com.kelvaya.ecobee.client.service

import akka.http.scaladsl.model.HttpMethods
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.model.headers.Authorization
import akka.http.scaladsl.model.headers.OAuth2BearerToken
import akka.stream.ActorMaterializer

import com.kelvaya.ecobee.client.Electricity
import com.kelvaya.ecobee.client.Request
import com.kelvaya.ecobee.client.ThermostatModification
import com.kelvaya.ecobee.test.client.BaseTestSpec

import spray.json._

import java.nio.charset.StandardCharsets

import scala.concurrent.duration.Duration
import scala.concurrent.Await
import com.typesafe.scalalogging.Logger

class ThermostatPostSpec extends BaseTestSpec {


  import deps.Implicits._
  implicit val materializer = ActorMaterializer()
  implicit val log = Logger[ThermostatPostSpec]

  lazy val TestThermostat = ThermostatModification("identifier", name = Some("Hello World"))
  lazy val TestFunction = ThermostatFunction("test", Electricity(Seq.empty))

  lazy val store = this.createStorage()

  "The request" must "serialize correctly for HTTP" in {
    val thermReq = ThermostatPostRequest(account, Select(SelectType.Thermostats), Some(TestThermostat), Some(Seq(TestFunction)))

    val expectedPayload = """
      {
        "functions" : [ { "type" : "test", "params": { "devices" : [] } }],
        "selection" : { "selectionType" : "thermostats", "selectionMatch" : "" },
        "thermostat" : { "identifier" : "identifier", "name" : "Hello World" }
      }
      """.parseJson

    val req: HttpRequest = this.runtime.unsafeRun(thermReq.createRequest.provide(store))
    req.method shouldBe HttpMethods.POST
    req.uri.path shouldBe Uri.Path("/thermostat")
    req.uri.query().size shouldBe 1
    req.header[Authorization] shouldBe 'defined
    req.header[Authorization].get shouldBe Authorization(OAuth2BearerToken(this.AccessToken))

    req.entity.contentType shouldBe Request.ContentTypeJson

    val entity = Await.result(req.entity.toStrict(Duration(1, "second")), Duration(1, "second"))
      .data
      .decodeString(StandardCharsets.UTF_8)

    entity.parseJson shouldBe expectedPayload
  }
}
