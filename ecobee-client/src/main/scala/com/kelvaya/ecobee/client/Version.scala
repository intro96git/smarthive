package com.kelvaya.ecobee.client

import spray.json._
import spray.json.DefaultJsonProtocol._

object Version {
  implicit val VersionFormat = DefaultJsonProtocol.jsonFormat1(Version.apply)
}


/** The thermostat firmware version number */
case class Version(thermostatFirmwareVersion : String) extends ReadonlyApiObject