package com.kelvaya.ecobee.client

import com.kelvaya.ecobee.client.Event.FanMode
import com.kelvaya.util.SprayImplicits
import com.kelvaya.util.enum.JsonStringEnum

import spray.json._
import spray.json.DefaultJsonProtocol._


/** A climate used by a [[Thermostat]] in a [[Program]].
  *
  * @param name The unique climate name
  * @param climateRef The unique climate identifier.
  * @param isOccupied A flag indicating whether the property is occupied by persons during this climate
  * @param isOptimized A flag indicating whether ecobee optimized climate settings are used by this climate.
  * @param coolFan The cooling fan mode.
  * @param heatFan The heating fan mode.
  * @param vent The ventilator mode.
  * @param ventilatorMinOnTime The minimum time, in minutes, to run the ventilator each hour.
  * @param owner The climate owner.
  * @param type The type of climate.
  * @param colour The integer conversion of the HEX color value used to display this climate on the thermostat and on the web portal.
  * @param coolTemp The cool temperature for this climate.
  * @param heatTemp The heat temperature for this climate.
  * @param sensors The list of sensors in use for the specific climate.
  */
case class Climate(
    name :                String,
    climateRef :          Climate.Ref,
    isOccupied :          Boolean,
    isOptimized :         Boolean,
    coolFan :             FanMode.Entry,
    heatFan :             FanMode.Entry,
    vent :                VentilatorMode.Entry,
    ventilatorMinOnTime : Int,
    owner :               Climate.Owner = Climate.Owner.System,
    `type` :              Climate.Type = Climate.Type.Program,
    colour :              Int,
    coolTemp :            Int,
    heatTemp :            Int,
    sensors :             Array[RemoteSensor]
)


object Climate extends SprayImplicits {
  implicit val ClimateRefFormat = DefaultJsonProtocol.jsonFormat1(Ref)
  implicit val ClimateFormat = DefaultJsonProtocol.jsonFormat14(Climate.apply)


  /** Unique climate reference ID
    *
    * @note These are generated by the Ecobee API and cannot be modified
    */
  case class Ref(name : String)

  type Owner = Owner.Entry

  /** The climate owner */
  object Owner extends JsonStringEnum {
    val AdHoc = Val("adHoc")
    val DemandResponse = Val("demandResponse")
    val QuickSave = Val("quickSave")
    val SensorAction = Val("sensorAction")
    val SwitchOccupancy = Val("switchOccupancy")
    val System = Val("system")
    val Template = Val("template")
    val User = Val("user")
  }


  type Type = Type.Entry

  /** The type of climate used */
  object Type extends JsonStringEnum {
    val CalendarEvent = Val("calendarEvent")
    val Program = Val("program")
  }
}
