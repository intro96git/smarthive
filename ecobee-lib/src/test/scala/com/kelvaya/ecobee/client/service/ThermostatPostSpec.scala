package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.test.BaseTestSpec
import com.kelvaya.ecobee.client.Client
import com.kelvaya.ecobee.client.Thermostat
import com.kelvaya.ecobee.config.Settings
import com.kelvaya.ecobee.client.Electricity
import com.kelvaya.ecobee.client.ThermostatModification
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.model.HttpMethods
import com.kelvaya.ecobee.client.Request
import akka.http.scaladsl.model.HttpEntity
import spray.json._
import scala.concurrent.duration.Duration
import akka.stream.ActorMaterializer
import scala.concurrent.Await
import java.nio.charset.StandardCharsets
import akka.stream.scaladsl.Sink

class ThermostatPostSpec extends BaseTestSpec {

  implicit lazy val settings = this.injector.instance[Settings]
  implicit lazy val exec = this.createTestExecutor(Map.empty)
  implicit lazy val client = new Client
  implicit val materializer = ActorMaterializer()

  lazy val TestThermostat = ThermostatModification("identifier", name = Some("Hello World"))
  lazy val TestFunction = ThermostatFunction("test", Electricity(Seq.empty))

  "The request" must "serialize correctly for HTTP" in {
    val thermReq = ThermostatPostRequest(Select(SelectType.Thermostats), Some(TestThermostat), Some(Seq(TestFunction)))

    val expectedPayload = """
      {
        "functions" : [ { "type" : "test", "params": { "devices" : [] } }],
        "selection" : { "selectionType" : "thermostats", "selectionMatch" : "" },
        "thermostat" : { "identifier" : "identifier", "name" : "Hello World" }
      }
      """.parseJson

    import monix.execution.Scheduler.Implicits.global

    val req: HttpRequest = thermReq.createRequest.runSyncUnsafe(scala.concurrent.duration.Duration("5 seconds"))
    req.method shouldBe HttpMethods.POST
    req.uri.path shouldBe Uri.Path("/thermostat")
    req.uri.query().size shouldBe 1

    req.entity.contentType shouldBe Request.ContentTypeJson

    val entity = Await.result(req.entity.toStrict(Duration(1, "second")), Duration(1, "second"))
      .data
      .decodeString(StandardCharsets.UTF_8)

    entity.parseJson shouldBe expectedPayload
  }
}