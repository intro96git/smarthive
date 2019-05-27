package com.kelvaya.ecobee.client

import com.kelvaya.ecobee.test.BaseTestSpec
import com.kelvaya.util.Time.DateOnly
import com.kelvaya.util.Time.TimeOnly
import org.joda.time.DateTime

import spray.json._
import spray.json.DefaultJsonProtocol._
import com.kelvaya.util.SprayImplicits

class EventSpec extends BaseTestSpec with SprayImplicits {

    val Now = DateTime.parse("2013-03-04T18:19:20")

    val BaseEvent = Event(
      `type`                 = Some(Event.EventType.Sensor),
      name                   = Some("my_event"),
      running                = Some(true),
      startDate              = Some(new DateOnly(Now)),
      startTime              = Some(new TimeOnly(Now)),
      endDate                = Some(new DateOnly(Now.plusHours(1))),
      endTime                = Some(new TimeOnly(Now.plusHours(1))),
      isOccupied             = Some(true),
      isCoolOff              = Some(true),
      isHeatOff              = Some(false),
      coolHoldTemp           = Some(68),
      heatHoldTemp           = Some(75),
      fan                    = Some(Event.FanMode.Auto),
      vent                   = Some(VentilatorMode.Auto),
      ventilatorMinOnTime    = Some(30),
      isOptional             = Some(false),
      coolRelativeTemp       = Some(6),
      heatRelativeTemp       = None,
      dutyCyclePercentage    = Some(100),
      fanMinOnTime           = Some(5),
      occupiedSensorActive   = Some(true),
      unoccupiedSensorActive = Some(false),
      linkRef                = Some("linkRef"),
      holdClimateRef         = Some("holdRef")
    )

    "An event" must "be serializable to JSON" in {

      val expected = s"""{
        "type" : "sensor",
        "name" : "my_event",
        "running" : true,
        "startDate" : "2013-03-04",
        "startTime" : "18:19:20",
        "endDate" :  "2013-03-04",
        "endTime" : "19:19:20",
        "isOccupied" : true,
        "isCoolOff" : true,
        "isHeatOff" : false,
        "coolHoldTemp" : 68,
        "heatHoldTemp" : 75,
        "isTemperatureAbsolute" : true,
        "fan" : "auto",
        "vent" : "auto",
        "ventilatorMinOnTime" : 30,
        "isOptional" : false,
        "isTemperatureRelative" : false,
        "dutyCyclePercentage" : 100,
        "fanMinOnTime" : 5,
        "occupiedSensorActive" : true,
        "unoccupiedSensorActive" : false,
        "linkRef" : "linkRef",
        "holdClimateRef" : "holdRef"
        }""".parseJson

      val actual = BaseEvent.toJson

      actual shouldBe expected
      actual.convertTo[Event] shouldBe BaseEvent.copy(coolRelativeTemp = None)


      val noAbs = BaseEvent.copy(coolHoldTemp = None)
      val noAbsExpected = s"""{
        "type" : "sensor",
        "name" : "my_event",
        "running" : true,
        "startDate" : "2013-03-04",
        "startTime" : "18:19:20",
        "endDate" :  "2013-03-04",
        "endTime" : "19:19:20",
        "isOccupied" : true,
        "isCoolOff" : true,
        "isHeatOff" : false,
        "isTemperatureAbsolute" : false,
        "fan" : "auto",
        "vent" : "auto",
        "ventilatorMinOnTime" : 30,
        "isOptional" : false,
        "isTemperatureRelative" : false,
        "dutyCyclePercentage" : 100,
        "fanMinOnTime" : 5,
        "occupiedSensorActive" : true,
        "unoccupiedSensorActive" : false,
        "linkRef" : "linkRef",
        "holdClimateRef" : "holdRef"
        }""".parseJson

     noAbs.toJson shouldBe noAbsExpected
     noAbs.toJson.convertTo[Event] shouldBe noAbs.copy(coolRelativeTemp = None, heatHoldTemp = None)

      val rel = BaseEvent.copy(heatRelativeTemp = Some(4))
      val relExpected = s"""{
        "type" : "sensor",
        "name" : "my_event",
        "running" : true,
        "startDate" : "2013-03-04",
        "startTime" : "18:19:20",
        "endDate" :  "2013-03-04",
        "endTime" : "19:19:20",
        "isOccupied" : true,
        "isCoolOff" : true,
        "isHeatOff" : false,
        "coolHoldTemp" : 68,
        "heatHoldTemp" : 75,
        "isTemperatureAbsolute" : true,
        "fan" : "auto",
        "vent" : "auto",
        "ventilatorMinOnTime" : 30,
        "isOptional" : false,
        "heatRelativeTemp" : 4,
        "coolRelativeTemp" : 6,
        "isTemperatureRelative" : true,
        "dutyCyclePercentage" : 100,
        "fanMinOnTime" : 5,
        "occupiedSensorActive" : true,
        "unoccupiedSensorActive" : false,
        "linkRef" : "linkRef",
        "holdClimateRef" : "holdRef"
        }""".parseJson

      rel.toJson shouldBe relExpected
      rel.toJson.convertTo[Event] shouldBe rel
    }
}