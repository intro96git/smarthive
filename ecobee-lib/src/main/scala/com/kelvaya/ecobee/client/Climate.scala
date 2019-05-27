package com.kelvaya.ecobee.client

import com.kelvaya.ecobee.client.Event.FanMode
import com.kelvaya.util.SprayImplicits
import com.kelvaya.util.enum.JsonStringEnum

import spray.json._
import spray.json.DefaultJsonProtocol._


/** A climate used by a [[Thermostat]] in a [[Program]].
  *
  * @note This can be used in GET requests only.  Use the [[#asWritable]] method to grab an instance valid for writing in POST operations.
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
    climateRef :          Option[Climate.Ref],
    isOccupied :          Option[Boolean],
    isOptimized :         Option[Boolean],
    coolFan :             Option[FanMode.Entry],
    heatFan :             Option[FanMode.Entry],
    vent :                Option[VentilatorMode.Entry],
    ventilatorMinOnTime : Option[Int],
    owner :               Option[Climate.Owner] = Some(Climate.Owner.System),
    `type` :              Option[Climate.Type] = Some(Climate.Type.Program),
    colour :              Option[Int],
    coolTemp :            Option[Int],
    heatTemp :            Option[Int],
    sensors :             Option[Array[Climate.RemoteSensor]]
) extends ApiObject {
  def asWriteable = ClimateModification(name, isOccupied, isOptimized, coolFan, heatFan, vent, ventilatorMinOnTime,
      owner, `type`, colour, coolTemp, heatTemp, sensors)
}


object Climate extends SprayImplicits {
  implicit val ClimateRefFormat = DefaultJsonProtocol.jsonFormat1(Ref)
  implicit val ClimateRemoteSensorFormat = DefaultJsonProtocol.jsonFormat2(RemoteSensor)
  implicit val ClimateFormat = DefaultJsonProtocol.jsonFormat14(Climate.apply)


  /** Unique climate reference ID
    *
    * @note These are generated by the Ecobee API and cannot be modified
    */
  case class Ref(name : String)


  /** Sensor connected to a [[Thermostat]]
    *
    * @note This is functionally identical to the main class, [[com.kelvaya.ecobee.client.RemoteSensor RemoteSensor]].
    * However, it is represented in JSON differently when it is associated with a [[Climate]] instance.
    */
  case class RemoteSensor(id : Option[String], name : Option[String]) extends ReadonlyApiObject

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


/** A climate used by a [[Thermostat]] in a [[Program]] which can be used in POST modification requests.
  *
  * @param name The unique climate name
  * @param isOccupied A flag indicating whether the property is occupied by persons during this climate
  * @param isOptimized A flag indicating whether ecobee optimized climate settings are used by this climate.
  * @param coolFan The cooling fan mode.
  * @param heatFan The heating fan mode.
  * @param vent The ventilator mode.
  * @param ventilatorMinOnTime The minimum time, in minutes, to run the ventilator each hour.
  * @param owner The climate owner.
  * @param type The type of climate.
  * @param colour The integer conversi  * @note This can be used in GET requests only.  Use the [[#asWritable]] method to grab an instance valid for writing in POST operations.
  *
  * on of the HEX color value used object Climate extends SprayImplicits {
  implicit val ClimateRefFormat = DefaultJsonProtocol.jsonFormat1(Ref)
  implicit val ClimateRemoteSensorFormat = DefaultJsonProtocol.jsonFormat2(RemoteSensor)
  implicit val ClimateFormat = DefaultJsonProtocol.jsonFormat14(Climate.apply)
  * to display this climate on the thermostat and on the web portal.
  * @param coolTemp The cool temperature for this climate.
  * @param heatTemp The heat temperature for this climate.
  * @param sensors The list of sensors in use for the specific climate.
  *
  * @see Climate
  */
case class ClimateModification(
    name :                String,
    isOccupied :          Option[Boolean] = None,
    isOptimized :         Option[Boolean] = None,
    coolFan :             Option[FanMode.Entry] = None,
    heatFan :             Option[FanMode.Entry] = None,
    vent :                Option[VentilatorMode.Entry] = None,
    ventilatorMinOnTime : Option[Int] = None,
    owner :               Option[Climate.Owner] = Some(Climate.Owner.System),
    `type` :              Option[Climate.Type] = Some(Climate.Type.Program),
    colour :              Option[Int] = None,
    coolTemp :            Option[Int] = None,
    heatTemp :            Option[Int] = None,
    sensors :             Option[Array[Climate.RemoteSensor]] = None
) extends WriteableApiObject


object ClimateModification extends SprayImplicits {
  implicit val ClimateFormat = DefaultJsonProtocol.jsonFormat13(ClimateModification.apply)
}
