package com.kelvaya.ecobee.client

import spray.json._
import spray.json.DefaultJsonProtocol._
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
    `type` :                 Option[Event.EventType.Entry] = None,
    name :                   Option[String] = None,
    running :                Option[Boolean] = None,
    startDate :              Option[DateOnly] = None,
    startTime :              Option[TimeOnly] = None,
    endDate :                Option[DateOnly] = None,
    endTime :                Option[TimeOnly] = None,
    isOccupied :             Option[Boolean] = None,
    isCoolOff :              Option[Boolean] = None,
    isHeatOff :              Option[Boolean] = None,
    coolHoldTemp :           Option[Int] = None,
    heatHoldTemp :           Option[Int] = None,
    fan :                    Option[Event.FanMode.Entry] = None,
    vent :                   Option[VentilatorMode.Entry] = None,
    ventilatorMinOnTime :    Option[Int] = None,
    isOptional :             Option[Boolean] = None,
    coolRelativeTemp :       Option[Int] = None,
    heatRelativeTemp :       Option[Int] = None,
    dutyCyclePercentage :    Option[Int] = None,
    fanMinOnTime :           Option[Int] = None,
    occupiedSensorActive :   Option[Boolean] = None,
    unoccupiedSensorActive : Option[Boolean] = None,
//    drRampUpTemp :           Option[Integer] = None,
//    drRampUpTime :           Option[Integer] = None,
    linkRef :                Option[String] = None,
    holdClimateRef :         Option[String] = None
) extends ReadonlyApiObject


object Event extends SprayImplicits {
  implicit object EventFormat extends RootJsonFormat[Event] {
    def read(json: JsValue): Event = json match {
      case o : JsObject => {
        Event(
          `type`                 = findOptional[EventType.Entry](o, "type"),
          name                   = findOptional[String](o, "name"),
          running                = findOptional[Boolean](o, "running"),
          startDate              = findOptional[DateOnly](o, "startDate"),
          startTime              = findOptional[TimeOnly](o, "startTime"),
          endDate                = findOptional[DateOnly](o, "endDate"),
          endTime                = findOptional[TimeOnly](o, "endTime"),
          isOccupied             = findOptional[Boolean](o, "isOccupied"),
          isCoolOff              = findOptional[Boolean](o, "isCoolOff"),
          isHeatOff              = findOptional[Boolean](o, "isHeatOff"),
          coolHoldTemp           = if (findOptional[Boolean](o, "isTemperatureAbsolute").getOrElse(false)) findOptional[Int](o, "coolHoldTemp") else None,
          heatHoldTemp           = if (findOptional[Boolean](o, "isTemperatureAbsolute").getOrElse(false)) findOptional[Int](o, "heatHoldTemp") else None,
          fan                    = findOptional[FanMode.Entry](o, "fan"),
          vent                   = findOptional[VentilatorMode.Entry](o, "vent"),
          ventilatorMinOnTime    = findOptional[Int](o, "ventilatorMinOnTime"),
          isOptional             = findOptional[Boolean](o, "isOptional"),
          coolRelativeTemp       = if (findOptional[Boolean](o, "isTemperatureRelative").getOrElse(false)) findOptional[Int](o, "coolRelativeTemp") else None,
          heatRelativeTemp       = if (findOptional[Boolean](o, "isTemperatureRelative").getOrElse(false)) findOptional[Int](o, "heatRelativeTemp") else None,
          dutyCyclePercentage    = findOptional[Int](o, "dutyCyclePercentage"),
          fanMinOnTime           = findOptional[Int](o, "fanMinOnTime"),
          occupiedSensorActive   = findOptional[Boolean](o, "occupiedSensorActive"),
          unoccupiedSensorActive = findOptional[Boolean](o, "unoccupiedSensorActive"),
          linkRef                = findOptional[String](o, "linkRef"),
          holdClimateRef         = findOptional[String](o, "holdClimateRef")
        )
      }
      case _ ⇒ deserializationError(s"${json} is not an Event")
    }



    def write(obj: Event): JsValue = {

      val jsObj = scala.collection.mutable.Map.empty[String,JsValue]
      obj.`type` foreach { v => jsObj += (("type", v.toJson)) }
      obj.name foreach { v => jsObj += (("name", v.toJson)) }
      obj.running foreach { v => jsObj += (("running", v.toJson)) }
      obj.startDate foreach { v => jsObj += (("startDate", v.toJson)) }
      obj.startTime foreach { v => jsObj += (("startTime", v.toJson)) }
      obj.endDate foreach { v => jsObj += (("endDate", v.toJson)) }
      obj.endTime foreach { v => jsObj += (("endTime", v.toJson)) }
      obj.isOccupied foreach { v => jsObj += (("isOccupied", v.toJson)) }
      obj.isCoolOff foreach { v => jsObj += (("isCoolOff", v.toJson)) }
      obj.isHeatOff foreach { v => jsObj += (("isHeatOff", v.toJson)) }
      obj.fan foreach { v => jsObj += (("fan", v.toJson)) }
      obj.vent foreach { v => jsObj += (("vent", v.toJson)) }
      obj.ventilatorMinOnTime foreach { v => jsObj += (("ventilatorMinOnTime", v.toJson)) }
      obj.isOptional foreach { v => jsObj += (("isOptional", v.toJson)) }
      obj.dutyCyclePercentage foreach { v => jsObj += (("dutyCyclePercentage", v.toJson)) }
      obj.fanMinOnTime foreach { v => jsObj += (("fanMinOnTime", v.toJson)) }
      obj.occupiedSensorActive foreach { v => jsObj += (("occupiedSensorActive", v.toJson)) }
      obj.unoccupiedSensorActive foreach { v => jsObj += (("unoccupiedSensorActive", v.toJson)) }
      obj.linkRef foreach { v => jsObj += (("linkRef", v.toJson)) }
      obj.isOptional foreach { v => jsObj += (("isOptional", v.toJson)) }
      obj.holdClimateRef foreach { v => jsObj += (("holdClimateRef", v.toJson)) }

      if (obj.coolHoldTemp.isDefined && obj.heatHoldTemp.isDefined) {
        jsObj += (("coolHoldTemp", obj.coolHoldTemp.get.toJson))
        jsObj += (("heatHoldTemp", obj.heatHoldTemp.get.toJson))
        jsObj += (("isTemperatureAbsolute", true.toJson))
      }
      else jsObj += (("isTemperatureAbsolute", false.toJson))

      if (obj.coolRelativeTemp.isDefined && obj.heatRelativeTemp.isDefined) {
        jsObj += (("coolRelativeTemp", obj.coolRelativeTemp.get.toJson))
        jsObj += (("heatRelativeTemp", obj.heatRelativeTemp.get.toJson))
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
