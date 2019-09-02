package com.kelvaya.ecobee.client.service.function

import spray.json._
import spray.json.DefaultJsonProtocol._
import com.kelvaya.util.SprayImplicits._
import com.kelvaya.util.Time
import com.kelvaya.util.jsonenum.JsonStringEnum
import org.joda.time.DateTime


/** Control the on/off state of a plug
  *
  * @param plugName The name of the plug. Ensure each plug has a unique name.
  * @param plugState The state to put the plug into.
  * @param startDateTime The start date/time in thermostat time.
  * @param endDateTime The end date/time in thermostat time.
  * @param holdType The hold duration type.
  * @param holdHours The number of hours to hold for, used and required if `holdType` is `HoldHours`.
  * 
  * @see [[ControlPlugFunction]]
  */
case class ControlPlug(
    plugName :  String,
    plugState : ControlPlug.PlugState,
    startDateTime : DateTime,
    endDateTime :   DateTime,
    holdType :  ControlPlug.HoldType,
    holdHours : Int
) extends EcobeeFunction[ControlPlugFunction] {

  val name = "controlPlug"
  val params = ControlPlugFunction(plugName, plugState, new Time.DateOnly(startDateTime), new Time.TimeOnly(startDateTime),
      new Time.DateOnly(endDateTime), new Time.TimeOnly(endDateTime), holdType, holdHours)
  protected val writer = ControlPlugFunction.Format
}


object ControlPlug {
  import spray.json._
  import spray.json.DefaultJsonProtocol._
  import com.kelvaya.util.SprayImplicits._


  type PlugState = PlugState.Entry
  object PlugState extends JsonStringEnum {
    val On = Val("on")
    val Off = Val("off")
    val Resume = Val("resume")
  }

  type HoldType = HoldType.Entry
  object HoldType extends JsonStringEnum {
    val DateTime = Val("dateTime")
    val NextTransition = Val("nextTransition")
    val Indefinite = Val("indefinite")
    val HoldHours = Val("holdHours")
  }
}



/** Control the on/off state of a plug
  *
  * @note To use as a [[ThermostatFunction]], you must instead create an instance of [[ControlPlug]].
  *
  * @param plugName The name of the plug. Ensure each plug has a unique name.
  * @param plugState The state to put the plug into.
  * @param startDate The start date in thermostat time.
  * @param startTime The start time in thermostat time.
  * @param endDate The end date in thermostat time.
  * @param endTime The end time in thermostat time.
  * @param holdType The hold duration type.
  * @param holdHours The number of hours to hold for, used and required if `holdType` is `HoldHours`.
  */
case class ControlPlugFunction(
    plugName :  String,
    plugState : ControlPlug.PlugState,
    startDate : Time.DateOnly,
    startTime : Time.TimeOnly,
    endDate :   Time.DateOnly,
    endTime :   Time.TimeOnly,
    holdType :  ControlPlug.HoldType,
    holdHours : Int
)

object ControlPlugFunction {
  lazy val Format = DefaultJsonProtocol.jsonFormat8(ControlPlugFunction.apply)
}