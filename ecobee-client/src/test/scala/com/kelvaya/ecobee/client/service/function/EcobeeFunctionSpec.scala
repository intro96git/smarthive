package com.kelvaya.ecobee.client.service.function

import com.kelvaya.ecobee.client.service.Select
import com.kelvaya.ecobee.client.service.SelectType
import com.kelvaya.ecobee.client.service.ThermostatPostRequest
import com.kelvaya.ecobee.client.Temperature
import com.kelvaya.ecobee.client.Event.FanMode
import com.kelvaya.ecobee.test.client.BaseTestSpec

import com.typesafe.scalalogging.Logger

import spray.json._
import spray.json.DefaultJsonProtocol._

import com.twitter.finagle.http.{Request => HttpRequest}

import org.joda.time.DateTime

class EcobeeFunctionSpec extends BaseTestSpec {

  import deps.Implicits._
  implicit val log = Logger[EcobeeFunctionSpec]

  val now = DateTime.parse("2014-03-22T10:23:44")

  val store = this.createStorage()

  "An Ecobee function" must "implicitly convert to a ThermostatFunction" in {
    val tf = TestFunction("world")
    val req = ThermostatPostRequest(account, Select(SelectType.Registered), None, Some(Seq(tf)))

    val expectedPayload = """
      {
        "functions" : [ { "type" : "test", "params": { "hello" : "world" } }],
        "selection" : { "selectionType" : "registered" }
      }
      """.parseJson

    val httpReq: HttpRequest = this.runtime.unsafeRun(req.createRequest.provide(store))
    httpReq.contentString.parseJson shouldBe expectedPayload
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

    val req = ThermostatPostRequest(account, Select(SelectType.Registered), None, Some(Seq(ack, plug, vaca, dv, reset, resume, sm, sh, uv, us)))

    val expectedPayload = """
      {
        "selection" : { "selectionType" : "registered" },
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

    val httpReq: HttpRequest = this.runtime.unsafeRun(req.createRequest.provide(store))
    httpReq.contentString.parseJson shouldBe expectedPayload
  }
}


case class TestFunction(hello : String) extends EcobeeFunction[TestFunction] {
  val name: String = "test"
  val params = this
  protected val writer = DefaultJsonProtocol.jsonFormat(TestFunction.apply _, "hello")
}
