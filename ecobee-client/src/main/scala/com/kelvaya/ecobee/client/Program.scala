package com.kelvaya.ecobee.client

import spray.json.DefaultJsonProtocol._
import spray.json._

/** [[Thermostat]] program
  *
  * @param schedule The program schedule.
  * @param climates The list of Climate objects defining all the climates in the program schedule.
  * @param currentClimateRef The currently active climate.
  */
case class Program(schedule : ProgramSchedule, climates : Array[Climate], currentClimateRef : Climate.Ref)
extends WriteableApiObject



object Program {
  implicit val ProgramFormat = DefaultJsonProtocol.jsonFormat3(Program.apply)
}
