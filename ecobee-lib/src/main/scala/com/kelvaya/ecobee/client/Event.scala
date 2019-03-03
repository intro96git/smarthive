package com.kelvaya.ecobee.client

import spray.json._
import com.kelvaya.util.enum.JsonStringEnum
import com.kelvaya.util.SprayImplicits
import com.kelvaya.util.Time.DateOnly
import com.kelvaya.util.Time.TimeOnly

/** Scheduled thermostat change
  *
  * @param type The type of event
  * @param name The unique event name.
  * @param running Whether the event is currently active or not.
  * @param startDate The event start date in thermostat local time.
  * @param startTime The event start time in thermostat local time.
  * @param endDate The event end date in thermostat local time.
  * @param endTime The event end time in thermostat local time.
  * @param isOccupied Whether there are persons occupying the property during the event.
  * @param isCoolOff Whether cooling will be turned off during the event.
  * @param isHeatOff Whether heating will be turned off during the event.
  * @param coolHoldTemp The cooling absolute temperature to set.
  * @param heatHoldTemp The heating absolute temperature to set.
  * @param fan The fan mode during the event.
  * @param vent The ventilator mode the event.
  * @param ventilatorMinOnTime The minimum amount of time the ventilator equipment must stay on on each duty cycle.
  * @param isOptional Whether this event is mandatory or the end user can cancel it.
  * @param coolRelativeTemp The relative cool temperature adjustment.
  * @param heatRelativeTemp The relative heat temperature adjustment.
  * @param dutyCyclePercentage Indicates the % scheduled runtime during a Demand Response event.
  * @param fanMinOnTime The minimum number of minutes to run the fan each hour.
  * @param occupiedSensorActive True if this calendar event was created because of the occupied sensor.
  * @param unoccupiedSensorActive True if this calendar event was created because of the unoccupied sensor
  * @param linkRef Unique identifier to link one or more events and alerts together.
  * @param holdClimateRef Used for display purposes to indicate what climate (if any) is being used for the hold.
  */
case class Event(
    `type` :                 Event.EventType.Entry,
    name :                   String,
    running :                Boolean,
    startDate :              DateOnly,
    startTime :              TimeOnly,
    endDate :                DateOnly,
    endTime :                TimeOnly,
    isOccupied :             Boolean,
    isCoolOff :              Boolean,
    isHeatOff :              Boolean,
    coolHoldTemp :           Option[Int], // NB: wire up w/ "isTemperatureAbsolute" in JSON
    heatHoldTemp :           Option[Int], // NB: wire up w/ "isTemperatureAbsolute" in JSON
    fan :                    Event.FanMode.Entry,
    vent :                   VentilatorMode.Entry,
    ventilatorMinOnTime :    Int,
    isOptional :             Boolean,
    coolRelativeTemp :       Option[Int], // NB: wire up w/ "isTemperatureRelative" in JSON
    heatRelativeTemp :       Option[Int], // NB: wire up w/ "isTemperatureRelative" in JSON
    dutyCyclePercentage :    Int,
    fanMinOnTime :           Int,
    occupiedSensorActive :   Boolean,
    unoccupiedSensorActive : Boolean,
//    drRampUpTemp :           Integer,
//    drRampUpTime :           Integer,
    linkRef :                String,
    holdClimateRef :         String
)


object Event extends SprayImplicits {
  implicit object EventFormat extends RootJsonFormat[Event] {
    def read(json: JsValue): Event = json match {
      case o : JsObject => Event(
        `type` = o.fields("type").convertTo[EventType.Entry],
        name = o.fields("name").convertTo[String],
        running = o.fields("running").convertTo[Boolean],
        startDate = o.fields("startDate").convertTo[DateOnly],
        startTime = o.fields("startTime").convertTo[TimeOnly],
        endDate =  o.fields("endDate").convertTo[DateOnly],
        endTime = o.fields("endTime").convertTo[TimeOnly],
        isOccupied = o.fields("isOccupied").convertTo[Boolean],
        isCoolOff = o.fields("isCoolOff").convertTo[Boolean],
        isHeatOff = o.fields("isHeatOff").convertTo[Boolean],
        coolHoldTemp = if (o.fields("isTemperatureAbsolute").convertTo[Boolean]) Some(o.fields("coolHoldTemp").convertTo[Int]) else None,
        heatHoldTemp = if (o.fields("isTemperatureAbsolute").convertTo[Boolean]) Some(o.fields("heatHoldTemp").convertTo[Int]) else None,
        fan = o.fields("fan").convertTo[FanMode.Entry],
        vent = o.fields("vent").convertTo[VentilatorMode.Entry],
        ventilatorMinOnTime = o.fields("ventilatorMinOnTime").convertTo[Int],
        isOptional = o.fields("isOptional").convertTo[Boolean],
        coolRelativeTemp = if (o.fields("isTemperatureRelative").convertTo[Boolean]) Some(o.fields("coolRelativeTemp").convertTo[Int]) else None,
        heatRelativeTemp = if (o.fields("isTemperatureRelative").convertTo[Boolean]) Some(o.fields("heatRelativeTemp").convertTo[Int]) else None,
        dutyCyclePercentage = o.fields("dutyCyclePercentage").convertTo[Int],
        fanMinOnTime = o.fields("fanMinOnTime").convertTo[Int],
        occupiedSensorActive = o.fields("occupiedSensorActive").convertTo[Boolean],
        unoccupiedSensorActive = o.fields("unoccupiedSensorActive").convertTo[Boolean],
        linkRef = o.fields("linkRef").convertTo[String],
        holdClimateRef = o.fields("holdClimateRef").convertTo[String]
      )
      case _ => deserializationError(s"${json} is not an Event")
    }



    def write(obj: Event): JsValue = {

      val (holdTemps,isTemperatureAbsolute) =
        if (obj.coolHoldTemp.isDefined && obj.heatHoldTemp.isDefined) {
          ((s""""coolHoldTemp" : ${obj.coolHoldTemp.get.toJson},
          "heatHoldTemp" : ${obj.heatHoldTemp.get.toJson},""",
          true))
        }
        else (("",false))

      val (relTemps,isTemperatureRelative) =
        if (obj.coolRelativeTemp.isDefined && obj.heatRelativeTemp.isDefined) {
          ((s""""coolRelativeTemp" : ${obj.coolRelativeTemp.get.toJson},
          "heatRelativeTemp" : ${obj.heatRelativeTemp.get.toJson},""",
          true))
        }
        else (("",false))

      s"""{
        "type" : ${obj.`type`.toJson},
        "name" : ${obj.name.toJson},
        "running" : ${obj.running.toJson},
        "startDate" : ${obj.startDate.toJson},
        "startTime" : ${obj.startTime.toJson},
        "endDate" :  ${obj.endDate.toJson},
        "endTime" : ${obj.endTime.toJson},
        "isOccupied" : ${obj.isOccupied.toJson},
        "isCoolOff" : ${obj.isCoolOff.toJson},
        "isHeatOff" : ${obj.isHeatOff.toJson},
        ${holdTemps}
        "isTemperatureAbsolute" : ${isTemperatureAbsolute},
        "fan" : ${obj.fan.toJson},
        "vent" : ${obj.vent.toJson},
        "ventilatorMinOnTime" : ${obj.ventilatorMinOnTime.toJson},
        "isOptional" : ${obj.isOptional.toJson},
        ${relTemps}
        "isTemperatureRelative" : ${isTemperatureRelative},
        "dutyCyclePercentage" : ${obj.dutyCyclePercentage.toJson},
        "fanMinOnTime" : ${obj.fanMinOnTime.toJson},
        "occupiedSensorActive" : ${obj.occupiedSensorActive.toJson},
        "unoccupiedSensorActive" : ${obj.unoccupiedSensorActive.toJson},
        "linkRef" : ${obj.linkRef.toJson},
        "holdClimateRef" : ${obj.holdClimateRef.toJson}
        }""".parseJson
    }
  }


  /** The type of event. */
  object EventType extends JsonStringEnum {
    val Hold = Val("hold")
    val DemandResponse = Val("demandResponse")
    val Sensor = Val("sensor")
    val SwitchOccupancy = Val("switchOccupancy")
    val Vacation = Val("vacation")
    val QuickSave = Val("quickSave")
    val Today = Val("today")
    val AutoAway = Val("autoAway")
    val AutoHome = Val("autoHome")
  }


  /** Whether the fan is set to automatic */
  object FanMode extends JsonStringEnum {
    val Auto = Val("auto")
    val On = Val("on")
  }
}
