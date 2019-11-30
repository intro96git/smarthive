package com.kelvaya.ecobee.client.service.function

import com.kelvaya.ecobee.client.Temperature
import com.kelvaya.util.SprayImplicits._
import com.kelvaya.util.Time
import com.kelvaya.util.jsonenum._

import org.joda.time.DateTime

import spray.json._
import spray.json.DefaultJsonProtocol._


/** Sets the [[Thermostat]] into a hold with the specified temperature.
  *
  * @param coolHoldTemp The temperature to set the cool hold at.
  * @param heatHoldTemp The temperature to set the heat hold at.
  * @param holdClimateRef The climate to use as reference for setting the coolHoldTemp, heatHoldTemp and fan settings for this hold (optional).
  * @param startDate The start date in thermostat time.
  * @param endDate The end date in thermostat time.
  * @param holdType The hold duration type
  * @param holdHours The number of hours to hold for, used and required if holdType='holdHours'.
  * 
  * @see [[SetHoldFunction]]
  */
case class SetHold(
    coolHoldTemp :   Temperature,
    heatHoldTemp :   Temperature,
    holdClimateRef : Option[String],
    startDate :      Option[DateTime],
    endDate :        Option[DateTime],
    holdType :       SetHold.HoldType,
    holdHours :      Int
) extends EcobeeFunction[SetHoldFunction] {

  val name = "setHold"
  val params = SetHoldFunction(coolHoldTemp.degrees, heatHoldTemp.degrees, holdClimateRef, startDate map {new Time.DateOnly(_)},
      startDate map {new Time.TimeOnly(_)}, endDate map {new Time.DateOnly(_)}, endDate map {new Time.TimeOnly(_)}, holdType, holdHours)
  val writer = SetHoldFunction.Format
}



object SetHold {
  type HoldType = HoldType.Entry
  object HoldType extends JsonStringEnum {
    /** Use the provided start and end dates for the event. */
    val DateTime = Val("dateTime")

    /** The end time  will be set to the next climate transition in the program. */
    val NextTransition = Val("nextTransition")

    /** The hold will not end and require to be cancelled explicitly. */
    val Indefinite = Val("indefinite")

    /** Use the value in the "holdHours" parameter of the [[SetHold]] instance to set the end date/time for the event. */
    val HoldHours = Val("holdHours")
  }
}



/** Sets the [[Thermostat]] into a hold with the specified temperature.
  *
  * @note To use as a [[ThermostatFunction]], you must instead create an instance of [[SetHold]].
  * 
  * @param coolHoldTemp The temperature to set the cool hold at.
  * @param heatHoldTemp The temperature to set the heat hold at.
  * @param holdClimateRef The climate to use as reference for setting the coolHoldTemp, heatHoldTemp and fan settings for this hold.
  * @param startDate The start date in thermostat time.
  * @param startTime The start time in thermostat time.
  * @param endDate The end date in thermostat time.
  * @param endTime The end time in thermostat time.
  * @param holdType The hold duration type
  * @param holdHours The number of hours to hold for, used and required if holdType='holdHours'.
  */
case class SetHoldFunction(
    coolHoldTemp :   Int,
    heatHoldTemp :   Int,
    holdClimateRef : Option[String],
    startDate :      Option[Time.DateOnly],
    startTime :      Option[Time.TimeOnly],
    endDate :        Option[Time.DateOnly],
    endTime :        Option[Time.TimeOnly],
    holdType :       SetHold.HoldType,
    holdHours :      Int
)
object SetHoldFunction {
  lazy val Format = DefaultJsonProtocol.jsonFormat9(SetHoldFunction.apply)
}