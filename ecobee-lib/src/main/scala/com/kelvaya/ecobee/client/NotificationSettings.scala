package com.kelvaya.ecobee.client

import com.kelvaya.util.SprayImplicits
import com.kelvaya.util.Time
import com.kelvaya.util.enum.JsonStringEnum

import scala.concurrent.duration.Duration

import spray.json._
import spray.json.DefaultJsonProtocol

case class NotificationSettings(
    emailAddresses : Option[Seq[String]],
    emailNotificationsEnabled : Option[Boolean],
    equipment : Option[Seq[NotificationSettings.Equipment]],
    general : Option[Seq[NotificationSettings.General]],
    limit : Option[Seq[NotificationSettings.Limit]]
)





object NotificationSettings extends SprayImplicits {


  /*
   * filterLastChanged   String   no   no   The date the filter was last changed for this equipment. String format: YYYY-MM-DD
filterLife   Integer   no   no   The value representing the life of the filter. This value is expressed in month or hour, which is specified in the the filterLifeUnits property.
filterLifeUnits   String   no   no   The units the filterLife field is measured in. Possible values are: month, hour. month has a range of 1 - 12. hour has a range of 100 - 10000.
remindMeDate   String   yes   no   The date the reminder will be triggered. This is a read-only field and cannot be modified through the API. The value is calculated and set by the thermostat.
enabled   Boolean   no   no   Boolean value representing whether or not alerts/reminders are enabled for this notification type or not.
type   String   yes   yes   The type of notification. Possible values are: hvac, furnaceFilter, humidifierFilter, dehumidifierFilter, ventilator, ac, airFilter, airCleaner, uvLamp
remindTechnician   Boolean   no   no   Boolean value representing whether or not alerts/reminders should be sent to the technician/contractor assoicated with the thermostat.
   */
  case class Equipment(
    filterLastChanged : Option[Time.DateOnly],
    filterLife : Option[FilterLife],
    remindMeDate : ReadOnly[Option[Time.DateOnly]],
    enabled : Option[Boolean],
    `type` : ReadOnly[NotificationType],
    remindTechnician : Option[Boolean]
  )

  case class General()

  case class Limit()


  type NotificationType = NotificationType.Entry
  object NotificationType extends JsonStringEnum {
    /* hvac, furnaceFilter, humidifierFilter, dehumidifierFilter, ventilator, ac, airFilter, airCleaner, uvLamp */
  }

  case class FilterLife(duration : Duration)

  implicit val GeneralFormat = DefaultJsonProtocol.jsonFormat0(General)
  implicit val LimitFormat = DefaultJsonProtocol.jsonFormat0(Limit)
  implicit val FilterLifeFormat = new JsonFormat[FilterLife] {
    def read(json: JsValue): FilterLife = ???
    def write(obj: FilterLife): JsValue = ???
  }
  implicit val EquipmentFormat = DefaultJsonProtocol.jsonFormat6(Equipment)

  implicit val NotificationSettingsFormat = DefaultJsonProtocol.jsonFormat5(NotificationSettings.apply)
}