package com.kelvaya.ecobee.client.service.function

import com.kelvaya.ecobee.client.Client
import com.kelvaya.ecobee.client.service.Select
import com.kelvaya.ecobee.client.service.SelectType
import com.kelvaya.ecobee.client.service.ThermostatPostRequest
import com.kelvaya.ecobee.config.Settings
import com.kelvaya.ecobee.test.BaseTestSpec

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import java.nio.charset.StandardCharsets

import akka.http.scaladsl.model.HttpRequest
import akka.stream.ActorMaterializer
import spray.json._
import spray.json.DefaultJsonProtocol._
import com.kelvaya.util.Time
import org.joda.time.DateTime
import com.kelvaya.ecobee.client.Temperature
import com.kelvaya.ecobee.client.Event.FanMode

class EcobeeFunctionSpec extends BaseTestSpec {

  implicit lazy val settings = this.injector.instance[Settings]
  implicit lazy val exec = this.createTestExecutor(Map.empty)
  implicit lazy val client = new Client
  implicit val materializer = ActorMaterializer()

  val now = DateTime.parse("2014-03-22T10:23:44")

  "An Ecobee function" must "implicitly convert to a ThermostatFunction" in {
    val tf = TestFunction("world")
    val req = ThermostatPostRequest(Select(SelectType.Thermostats), None, Some(Seq(tf)))

    val expectedPayload = """
      {
        "functions" : [ { "type" : "test", "params": { "hello" : "world" } }],
        "selection" : { "selectionType" : "thermostats", "selectionMatch" : "" }
      }
      """.parseJson

    import monix.execution.Scheduler.Implicits.global

    val httpReq: HttpRequest = req.createRequest.runSyncUnsafe(scala.concurrent.duration.Duration("5 seconds"))
    val entity = Await.result(httpReq.entity.toStrict(Duration(1, "second")), Duration(1, "second"))
      .data
      .decodeString(StandardCharsets.UTF_8)

    entity.parseJson shouldBe expectedPayload
  }


  "All built-in functions" must "serialize correctly in the /thermostat POST request" in {

    val ack = Acknowledge("id", "ackRef", Acknowledge.Type.Accept, false)
    val plug = ControlPlug("name", ControlPlug.PlugState.Off, now, now, ControlPlug.HoldType.DateTime, 1)
    val vaca = CreateVacation("vaca", Temperature.fromFahrenheit(80), Temperature.fromFahrenheit(65), now, now, FanMode.Auto, 5)
    val dv = DeleteVacation("vaca")
    val reset = ResetPreferences()
    val resume = ResumeProgram(true)
    val sm = SendMessage("Hello world!")
    val sh = SetHold(Temperature.fromFahrenheit(80), Temperature.fromFahrenheit(60), None, Some(now), None, SetHold.HoldType.HoldHours, 4)
    val uv = UnlinkVoice("voice")
    val us = UpdateSensor("sense", "rs:100", "1")

    val req = ThermostatPostRequest(Select(SelectType.Thermostats), None, Some(Seq(ack, plug, vaca, dv, reset, resume, sm, sh, uv, us)))

    val expectedPayload = """
      {
        "selection" : { "selectionType" : "thermostats", "selectionMatch" : "" },
        "functions" : [
          { "type" : "acknowledge", "params" : { "thermostatIdentifier" : "id", "ackRef": "ackRef", "ackType": "accept", "remindMeLater" : false }},
          { "type" : "controlPlug", "params" : { "plugName": "name", "plugState": "off", "startDate": "2014-03-22","startTime": "10:23:44",
            "endDate": "2014-03-22","endTime": "10:23:44", "holdType": "dateTime", "holdHours": 1 }},
          { "type" : "createVacation", "params" : { "name" : "vaca", "coolHoldTemp" : 800, "heatHoldTemp" : 650, "startDate": "2014-03-22","startTime": "10:23:44",
            "endDate": "2014-03-22","endTime": "10:23:44", "fan": "auto", "fanMinOnTime": 5 }},
          { "type" : "deleteVacation", "params" : { "name" : "vaca" }},
          { "type" : "resetPreferences", "params" : {}},
          { "type" : "resumeProgram", "params" : { "resumeAll" : true }},
          { "type" : "sendMessage", "params" : { "text" : "Hello world!" }},
          { "type" : "setHold", "params" : { "coolHoldTemp":800, "heatHoldTemp":600, "startDate":"2014-03-22", "startTime": "10:23:44",
            "holdType":"holdHours", "holdHours":4 }},
          { "type" : "unlinkVoiceEngine", "params" : { "engineName" : "voice" }},
          { "type" : "updateSensor", "params" : { "name" : "sense", "deviceId" : "rs:100", "sensorId" : "1" }}
        ]
      }
      """.parseJson

    import monix.execution.Scheduler.Implicits.global

    val httpReq: HttpRequest = req.createRequest.runSyncUnsafe(scala.concurrent.duration.Duration("5 seconds"))
    val entity = Await.result(httpReq.entity.toStrict(Duration(1, "second")), Duration(1, "second"))
      .data
      .decodeString(StandardCharsets.UTF_8)

    entity.parseJson shouldBe expectedPayload
  }
}


case class TestFunction(hello : String) extends EcobeeFunction[TestFunction] {
  val name: String = "test"
  val params = this
  protected val writer = DefaultJsonProtocol.jsonFormat(TestFunction.apply _, "hello")
}
