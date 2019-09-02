package com.kelvaya.ecobee.client.service.function

import spray.json._
import spray.json.DefaultJsonProtocol._


/** Delete a vacation event from a [[Thermostat]]
  *
  * @param vacationName The vacation event name to delete.
  *
  * @see [[DeleteVacationFunction]]
  */
case class DeleteVacation(vacationName : String) extends EcobeeFunction[DeleteVacationFunction] {
  val name = "deleteVacation"
  val params = DeleteVacationFunction(vacationName)
  val writer = DeleteVacationFunction.Format
}


/** Delete a vacation event from a [[Thermostat]]
  *
  * @note To use as a [[ThermostatFunction]], you must instead create an instance of [[DeleteVacation]].
  *
  * @param name The vacation event name to delete.
  */
case class DeleteVacationFunction(name : String)
object DeleteVacationFunction {
  lazy val Format = DefaultJsonProtocol.jsonFormat1(DeleteVacationFunction.apply)
}