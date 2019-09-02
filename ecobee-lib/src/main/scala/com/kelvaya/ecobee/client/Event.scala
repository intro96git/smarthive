package com.kelvaya.ecobee.client

import spray.json._
import spray.json.DefaultJsonProtocol._
import com.kelvaya.util.jsonenum.JsonStringEnum
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
    coolHoldTemp :           Option[Int],
    heatHoldTemp :           Option[Int],
    fan :                    Event.FanMode.Entry,
    vent :                   VentilatorMode.Entry,
    ventilatorMinOnTime :    Int,
    isOptional :             Boolean,
    coolRelativeTemp :       Option[Int],
    heatRelativeTemp :       Option[Int],
    dutyCyclePercentage :    Int,
    fanMinOnTime :           Int,
    occupiedSensorActive :   Boolean,
    unoccupiedSensorActive : Boolean,
//    drRampUpTemp :           Integer,
//    drRampUpTime :           Integer,
    linkRef :                String,
    holdClimateRef :         String
) extends ReadonlyApiObject


object Event extends SprayImplicits {
  implicit object EventFormat extends RootJsonFormat[Event] {
    def read(json: JsValue): Event = json match {
      case o : JsObject => {
        Event(
          `type`                 = find[EventType.Entry](o, "type"),
          name                   = find[String](o, "name"),
          running                = find[Boolean](o, "running"),
          startDate              = find[DateOnly](o, "startDate"),
          startTime              = find[TimeOnly](o, "startTime"),
          endDate                = find[DateOnly](o, "endDate"),
          endTime                = find[TimeOnly](o, "endTime"),
          isOccupied             = find[Boolean](o, "isOccupied"),
          isCoolOff              = find[Boolean](o, "isCoolOff"),
          isHeatOff              = find[Boolean](o, "isHeatOff"),
          coolHoldTemp           = if (findOptional[Boolean](o, "isTemperatureAbsolute").getOrElse(false)) findOptional[Int](o, "coolHoldTemp") else None,
          heatHoldTemp           = if (findOptional[Boolean](o, "isTemperatureAbsolute").getOrElse(false)) findOptional[Int](o, "heatHoldTemp") else None,
          fan                    = find[FanMode.Entry](o, "fan"),
          vent                   = find[VentilatorMode.Entry](o, "vent"),
          ventilatorMinOnTime    = find[Int](o, "ventilatorMinOnTime"),
          isOptional             = find[Boolean](o, "isOptional"),
          coolRelativeTemp       = if (findOptional[Boolean](o, "isTemperatureRelative").getOrElse(false)) findOptional[Int](o, "coolRelativeTemp") else None,
          heatRelativeTemp       = if (findOptional[Boolean](o, "isTemperatureRelative").getOrElse(false)) findOptional[Int](o, "heatRelativeTemp") else None,
          dutyCyclePercentage    = find[Int](o, "dutyCyclePercentage"),
          fanMinOnTime           = find[Int](o, "fanMinOnTime"),
          occupiedSensorActive   = find[Boolean](o, "occupiedSensorActive"),
          unoccupiedSensorActive = find[Boolean](o, "unoccupiedSensorActive"),
          linkRef                = find[String](o, "linkRef"),
          holdClimateRef         = find[String](o, "holdClimateRef")
        )
      }
      case _ ⇒ deserializationError(s"${json} is not an Event")
    }



    def write(obj: Event): JsValue = {

      val jsObj = scala.collection.mutable.Map.empty[String,JsValue]
      jsObj += (("type", obj.`type`.toJson))
      jsObj += (("name", obj.name.toJson))
      jsObj += (("running", obj.running.toJson))
      jsObj += (("startDate", obj.startDate.toJson))
      jsObj += (("startTime", obj.startTime.toJson))
      jsObj += (("endDate", obj.endDate.toJson))
      jsObj += (("endTime", obj.endTime.toJson))
      jsObj += (("isOccupied", obj.isOccupied.toJson))
      jsObj += (("isCoolOff", obj.isCoolOff.toJson))
      jsObj += (("isHeatOff", obj.isHeatOff.toJson))
      jsObj += (("fan", obj.fan.toJson))
      jsObj += (("vent", obj.vent.toJson))
      jsObj += (("ventilatorMinOnTime", obj.ventilatorMinOnTime.toJson))
      jsObj += (("isOptional", obj.isOptional.toJson))
      jsObj += (("dutyCyclePercentage", obj.dutyCyclePercentage.toJson))
      jsObj += (("fanMinOnTime", obj.fanMinOnTime.toJson))
      jsObj += (("occupiedSensorActive", obj.occupiedSensorActive.toJson))
      jsObj += (("unoccupiedSensorActive", obj.unoccupiedSensorActive.toJson))
      jsObj += (("linkRef", obj.linkRef.toJson))
      jsObj += (("isOptional", obj.isOptional.toJson))
      jsObj += (("holdClimateRef", obj.holdClimateRef.toJson))

      if (obj.coolHoldTemp.isDefined && obj.heatHoldTemp.isDefined) {
        jsObj += (("coolHoldTemp", obj.coolHoldTemp.toJson))
        jsObj += (("heatHoldTemp", obj.heatHoldTemp.toJson))
        jsObj += (("isTemperatureAbsolute", true.toJson))
      }
      else jsObj += (("isTemperatureAbsolute", false.toJson))

      if (obj.coolRelativeTemp.isDefined && obj.heatRelativeTemp.isDefined) {
        jsObj += (("coolRelativeTemp", obj.coolRelativeTemp.toJson))
        jsObj += (("heatRelativeTemp", obj.heatRelativeTemp.toJson))
        jsObj += (("isTemperatureRelative", true.toJson))
      }
      else jsObj += (("isTemperatureRelative", false.toJson))

      JsObject(jsObj.toMap)
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
