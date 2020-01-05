package com.kelvaya.ecobee.client

import spray.json._
import spray.json.DefaultJsonProtocol._

import org.joda.time.DateTimeZone

import com.kelvaya.util.SprayImplicits

import com.typesafe.scalalogging.Logger


/** Physical location of [[Thermostat]].
  *
  *  May be used for weather queries.
  *
  * @note This can be used in GET requests only.  Use [[LocationModification]] to create an instance valid for writing in POST operations.
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
case class Location(
  timeZoneOffsetMinutes : Int,
  timeZone : DateTimeZone,
  isDaylightSaving : Boolean,
  streetAddress : String,
  city : String,
  provinceState : String,
  country : String,
  postalCode : String,
  phoneNumber : String,
  mapCoordinates : String
) extends ReadonlyApiObject

object Location extends SprayImplicits {
  implicit def locationFormat(implicit ev : Logger) = DefaultJsonProtocol.jsonFormat10(Location.apply)
}

/** Physical location of [[Thermostat]] which can be used in POST modification requests.
  *
  *  May be used for weather queries.
  *
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
case class LocationModification(
    timeZone : Option[DateTimeZone] = None,
    isDaylightSaving : Option[Boolean] = None,
    streetAddress : Option[String] = None,
    city : Option[String] = None,
    provinceState : Option[String] = None,
    country : Option[String] = None,
    postalCode : Option[String] = None,
    phoneNumber : Option[String] = None,
    mapCoordinates : Option[String] = None
  ) extends WriteableApiObject


object LocationModification extends SprayImplicits {
  implicit def locationModificationFormat(implicit ev : Logger) = DefaultJsonProtocol.jsonFormat9(LocationModification.apply)
}
