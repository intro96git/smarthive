package com.kelvaya.ecobee.client

import spray.json.DefaultJsonProtocol

object ThermostatPrivacy {
  implicit val ThermostatPrivacyFormat = DefaultJsonProtocol.jsonFormat0(ThermostatPrivacy.apply)
}

case class ThermostatPrivacy()