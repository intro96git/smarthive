package com.kelvaya.ecobee.client.service.function

import spray.json._
import spray.json.DefaultJsonProtocol._
import com.kelvaya.util.SprayImplicits._
import com.kelvaya.util.Time
import com.kelvaya.ecobee.client.Event.FanMode
import org.joda.time.DateTime
import com.kelvaya.ecobee.client.Temperature

/** Creates a vacation event on a [[Thermostat]]
  *
  * @param vacationName The vacation event name.
  * @param coolHoldTemp The temperature to set the cool vacation hold at.
  * @param heatHoldTemp The temperature to set the heat vacation hold at.
  * @param startDateTime The start date in thermostat time.
  * @param endDateTime The end date in thermostat time.
  * @param fan The fan mode during the vacation.
  * @param fanMinOnTime The minimum number of minutes to run the fan each hour.
  *
  * @see [[VacationFunction]]
  *
  */
case class CreateVacation(
    vacationName :  String,
    coolHoldTemp :  Temperature,
    heatHoldTemp :  Temperature,
    startDateTime : DateTime,
    endDateTime :   DateTime,
    fan :           FanMode.Entry,
    fanMinOnTime :  Int
) extends EcobeeFunction[VacationFunction] {

  val name = "createVacation"
  val params = VacationFunction(vacationName, coolHoldTemp.degrees, heatHoldTemp.degrees, new Time.DateOnly(startDateTime),
      new Time.TimeOnly(startDateTime), new Time.DateOnly(endDateTime), new Time.TimeOnly(endDateTime), fan, fanMinOnTime)
  protected val writer = VacationFunction.Format
}



/** Creates a vacation event on a [[Thermostat]]
  *
  *  @note To use as a [[ThermostatFunction]], you must instead create an instance of [[CreateVacation]].
  *
  * @param name The vacation event name.
  * @param coolHoldTemp The temperature to set the cool vacation hold at.
  * @param heatHoldTemp The temperature to set the heat vacation hold at.
  * @param startDate The start date in thermostat time.
  * @param startTime The start time in thermostat time.
  * @param endDate The end date in thermostat time.
  * @param endTime The end time in thermostat time.
  * @param fan The fan mode during the vacation.
  * @param fanMinOnTime The minimum number of minutes to run the fan each hour.
  */
case class VacationFunction(
    name :         String,
    coolHoldTemp : Int,
    heatHoldTemp : Int,
    startDate :    Time.DateOnly,
    startTime :    Time.TimeOnly,
    endDate :      Time.DateOnly,
    endTime :      Time.TimeOnly,
    fan :          FanMode.Entry,
    fanMinOnTime : Int
)


object VacationFunction {
  lazy val Format = DefaultJsonProtocol.jsonFormat9(VacationFunction.apply)
}