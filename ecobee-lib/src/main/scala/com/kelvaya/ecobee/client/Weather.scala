package com.kelvaya.ecobee.client

import spray.json._
import spray.json.DefaultJsonProtocol._
import com.kelvaya.util.Time.FullDate
import com.kelvaya.util.SprayImplicits


/** Weather and forcasts for the [[Thermostat thermostat's]] [[Location]].
 *  timestamp   String   yes   no   The time stamp in UTC of the weather forecast.
weatherStation   String   yes   no   The weather station identifier.
forecasts   WeatherForecast[]   yes   no   The list of latest weather station forecasts.
 */
case class Weather(timestamp : Option[FullDate], weatherStation : Option[String], forecasts : Option[Seq[WeatherForecast]])
extends ReadonlyApiObject

object Weather extends SprayImplicits {
  implicit val WeatherFormat = DefaultJsonProtocol.jsonFormat3(Weather.apply)

}
