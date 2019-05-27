package com.kelvaya.ecobee.client

import spray.json.DefaultJsonProtocol._
import spray.json._

/** A temperature reading.
  *
  *  @note To create a new `Temperature` class from Celcius or Fahrenheit, use the appropriate function on the
  *  companion object.
  *
  *  @param degrees The temperature, in modified Fahrenheit degrees, as represented within the Ecobee API.
  */
case class Temperature(degrees : Int) {
  lazy val C = (F - 32) * 5 / 9
  lazy val F = degrees / 10f
}


object Temperature {
  def fromFahrenheit(degrees : Double) = Temperature((degrees * 10).toInt)
  def fromCelcius(degrees : Double) = fromFahrenheit((degrees * 9 / 5) + 32)

  implicit val TemperatureFormat = DefaultJsonProtocol.jsonFormat(Temperature.apply _, "degrees")
}
