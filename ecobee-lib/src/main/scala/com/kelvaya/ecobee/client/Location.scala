package com.kelvaya.ecobee.client

import spray.json._
import spray.json.DefaultJsonProtocol._
import org.joda.time.DateTimeZone
import com.kelvaya.util.SprayImplicits
import akka.event.LoggingBus
import org.slf4j.Logger

/** Physical location of [[Thermostat]].
  *
  *  May be used for weather queries.
  *
  * @param timeZoneOffsetMinutes The timezone offset in minutes from UTC.
  * @param timeZone The timezone in which the thermostat resides.
  * @param isDaylightSaving  Whether the thermostat should factor in daylight savings when displaying the date and time.
  * @param streetAddress The thermostat location street address.
  * @param city The thermostat location city.
  * @param provinceState The thermostat location State or Province.
  * @param country The thermostat location country.
  * @param postalCode The thermostat location ZIP or Postal code.
  * @param phoneNumber The thermostat owner's phone number
  * @param mapCoordinates The lat/long geographic coordinates of the thermostat location.
  */
case class Location(timeZoneOffsetMinutes : Int, timeZone : DateTimeZone, isDaylightSaving : Boolean, streetAddress : String,
    city : String, provinceState : String, country : String, postalCode : String, phoneNumber : String, mapCoordinates : String)


object Location extends SprayImplicits {
  implicit def Format(implicit ev : Logger) = DefaultJsonProtocol.jsonFormat10(Location.apply)
}

