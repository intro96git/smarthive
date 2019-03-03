package com.kelvaya.ecobee.client

import spray.json.DefaultJsonProtocol

object NotificationSettings {
  implicit val NotificationSettingsFormat = DefaultJsonProtocol.jsonFormat0(NotificationSettings.apply)
}

case class NotificationSettings()