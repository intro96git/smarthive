package com.kelvaya.ecobee.client

import spray.json.DefaultJsonProtocol

object SecuritySettings {
  implicit val SecuritySettingsFormat = DefaultJsonProtocol.jsonFormat0(SecuritySettings.apply)
}

case class SecuritySettings()