package com.kelvaya.ecobee.client

import com.kelvaya.util.SprayImplicits
import com.kelvaya.util.Time
import com.kelvaya.util.enum.JsonStringEnum

import org.joda.time.Period

import spray.json._
import spray.json.DefaultJsonProtocol._



/** Configuration of possible alerts and reminders associated with a [[Thermostat]]
  *
  * @param emailAddresses The list of email addresses alerts and reminders will be sent to.
  * @param emailNotificationsEnabled   Whether alerts and reminders will be sent to the email addresses
  * @param equipment The list of equipment specific alert and reminder settings.
  * @param general The list of general alert and reminder settings.
  * @param limit The list of limit specific alert and reminder settings.
  */
case class NotificationSettings(
    emailAddresses : Option[Seq[String]] = None,
    emailNotificationsEnabled : Option[Boolean] = None,
    equipment : Option[Seq[NotificationSettings.Equipment]] = None,
    general : Option[Seq[NotificationSettings.General]] = None,
    limit : Option[Seq[NotificationSettings.Limit]] = None
) extends WriteableApiObject





object NotificationSettings extends SprayImplicits {


  /** A reminder or alert setting for a specific [[Thermostat]].
    *
    * @note This can be used in GET requests only.  Use the [[#asWritable]] method to grab an instance valid for writing in POST operations.
    *
    * @param filterLastChanged  The date the filter was last changed for this equipment
    * @param filterLife The value representing the life of the filter.
    * @param remindMeDate The date the reminder will be triggered.
    * @param enabled Whether alerts/reminders are enabled for this notification type
    * @param type The type of notification
    * @param remindTechnician Whether alerts/reminders should be sent to the technician/contractor assoiciated with the thermostat.
    *
    * @see EquipmentPostObject
    */
  case class Equipment(
    filterLastChanged : Option[Time.DateOnly] = None,
    filterLife : Option[FilterLife] = None,
    remindMeDate : Option[Time.DateOnly] = None,
    enabled : Option[Boolean] = None,
    `type` : EquipmentType,
    remindTechnician : Option[Boolean] = None
  ) extends ApiObject {
    def asWriteable = EquipmentModification(filterLastChanged, filterLife, enabled, remindTechnician)
  }



  /** A reminder or alert setting for a specific [[Thermostat]] which can be used in POST requests to modify the settings.
    *
    * @param filterLastChanged  The date the filter was last changed for this equipment
    * @param filterLife The value representing the life of the filter.
    * @param enabled Whether alerts/reminders are enabled for this notification type
    * @param remindTechnician Whether alerts/reminders should be sent to the technician/contractor assoiciated with the thermostat.
    *
    * @see Equipment
    */
  case class EquipmentModification(
    filterLastChanged : Option[Time.DateOnly] = None,
    filterLife : Option[FilterLife] = None,
    enabled : Option[Boolean] = None,
    remindTechnician : Option[Boolean] = None
  ) extends WriteableApiObject


  type EquipmentType = EquipmentType.Entry
  object EquipmentType extends JsonStringEnum {
    val HVAC = Val("hvac")
    val FurnaceFilter = Val("furnaceFilter")
    val HumidifierFilter = Val("humidifierFilter")
    val DehumidifierFilter = Val("dehumidifierFilter")
    val Ventilator = Val("ventilator")
    val AC = Val("ac")
    val AirFilter = Val("airFilter")
    val AirCleaner = Val("airCleaner")
    val UVLamp = Val("uvLamp")
  }

  // ################################################################

  /** A general alert or reminder in [[NotificationSettings notification settings]].
    *
    * @note This can be used in GET requests only.  Use the [[#asWritable]] method to grab an instance valid for writing in POST operations.
    *
    * @param enabled Whether alerts/reminders are enabled for this notification type
    * @param type The type of notification
    * @param remindTechnician Whether alerts/reminders should be sent to the technician/contractor assoicated with the thermostat.
    */
  case class General(enabled : Option[Boolean] = None, `type` : GeneralType, remindTechnician : Option[Boolean] = None) extends ApiObject {
    def asWriteable = GeneralModification(enabled, remindTechnician)
  }


  /** A general alert or reminder in [[NotificationSettings notification settings]] which can be used in POST modification requests.
    *
    * @param enabled   Boolean   no   no   Boolean value representing whether or not alerts/reminders are enabled for this notification type or not.
    * @param remindTechnician   Boolean   no   no   Boolean value representing whether or not alerts/reminders should be sent to the technician/contractor assoicated with the thermostat.
    *
    * @see General
    */
  case class GeneralModification(enabled : Option[Boolean] = None, remindTechnician : Option[Boolean] = None) extends WriteableApiObject


  type GeneralType = GeneralType.Entry
  object GeneralType extends JsonStringEnum {
    val Temp = Val("temp")
  }


  implicit val GeneralFormat = DefaultJsonProtocol.jsonFormat3(General)
  implicit val GeneralModificationFormat = DefaultJsonProtocol.jsonFormat2(GeneralModification)

  // ################################################################


  /** An alert or reminder in [[NotificationSettings notification settings]] associated with a specific threshold
    *
    * @note This can be used in GET requests only.  Use the [[#asWritable]] method to grab an instance valid for writing in POST operations.
    *
    * @param limit The value of the limit to set.
    * @param enabled Whether alerts/reminders are enabled for this notification type.
    * @param type The type of notification
    * @param remindTechnician Whether alerts/reminders should be sent to the technician/contractor associated with the thermostat.
    */
  sealed abstract class Limit private[NotificationSettings] (val limit : Option[Int] = None, val enabled : Option[Boolean] = None,
      val `type` : LimitType, val remindTechnician : Option[Boolean] = None) extends ApiObject


  /** An alert or reminder in [[NotificationSettings notification settings]] associated with a specific threshold
    *  which can be used in POST modification requests.
    *
    * @param limit   Integer   no   no   The value of the limit to set. For temperatures the value is expressed as degrees Fahrenheit, multipled by 10. For humidity values are expressed as a percentage from 5 to 95. See here for more information.
    * @param enabled   Boolean   no   no   Boolean value representing whether or n    emailAddresses : Option[Seq[String]],
    emailNotificationsEnabled : Option[Boolean],
    equipment : Option[Seq[NotificationSettings.Equipment]],
    general : Option[Seq[NotificationSettings.General]],
    limit : Option[Seq[NotificationSettings.Limit]]ot alerts/reminders are enabled for this notification type or not.
    * @param remindTechnician   Boolean   no   no   Boolean value representing whether or not alerts/reminders should be sent to the technician/contractor associated with the thermostat.
    *
    * @see Limit
    */
  sealed abstract class LimitModification private[NotificationSettings] (val limit : Option[Int] = None, val enabled : Option[Boolean] = None,
      val remindTechnician : Option[Boolean] = None) extends WriteableApiObject


  object Limit {

    /** An temperature alert or reminder in [[NotificationSettings notification settings]] associated with a specific threshold
      *  which can be used in POST modification requests.
      *
      * @note This can be used in GET requests only.  Use the [[#asWritable]] method to grab an instance valid for writing in POST operations.
      *
      * @param temperature The value of the temperature limit to set.
      * @param enabled Whether alerts/reminders are enabled for this notification type
      * @param isLowTempAlert When true, this is a low temperature threshold alert.  Otherwise, it is the high temperature threshold
      * @param remindTechnician Whether alerts/reminders should be sent to the technician/contractor associated with the thermostat.
      */
    case class TempLimit(temperature : Option[Temperature] = None, override val enabled : Option[Boolean] = None,
        isLowTempAlert : Boolean, override val remindTechnician : Option[Boolean] = None)
    extends Limit(temperature.map(_.degrees), enabled, if (isLowTempAlert) LimitType.LowTemp else LimitType.HiTemp, remindTechnician) {
      def asWriteable = new TempLimitModification(temperature, enabled, remindTechnician)
    }


    /** An temperature alert or reminder in [[NotificationSettings notification settings]] associated with a specific threshold
      *  which can be used in POST modification requests.
      *
      * @param temperature The value of the temperature limit to set.
      * @param enabled Whether alerts/reminders are enabled for this notification type
      * @param remindTechnician Whether alerts/reminders should be sent to the technician/contractor associated with the thermostat.
      *
      * @see TempLimit
      */
    case class TempLimitModification(temperature : Option[Temperature] = None, override val enabled : Option[Boolean] = None,
        override val remindTechnician : Option[Boolean] = None)
    extends LimitModification(temperature.map(_.degrees), enabled, remindTechnician)


    /** An humidity alert or reminder in [[NotificationSettings notification settings]] associated with a specific threshold.
      *
      * @note This can be used in GET requests only.  Use the [[#asWritable]] method to grab an instance valid for writing in POST operations.
      *
      * @param humidity The value of the humidity limit to set, from 5 - 95.
      * @param enabled Whether alerts/reminders are enabled for this notification type
      * @param isLowHumidityAlert When true, this is a low humidity threshold alert.  Otherwise, it is the high humidity threshold
      * @param remindTechnician Whether alerts/reminders should be sent to the technician/contractor associated with the thermostat.
      */
    case class HumidityLimit(humidity : Option[Int] = None, override val enabled : Option[Boolean] = None, isLowHumidityAlert : Boolean,
        override val remindTechnician : Option[Boolean] = None)
    extends Limit(humidity, enabled, if (isLowHumidityAlert) LimitType.LowHumidity else LimitType.HiHumidity, remindTechnician) {
      def asWriteable = new HumidityLimitModification(humidity, enabled, remindTechnician)
    }


    /** An humidity alert or reminder in [[NotificationSettings notification settings]] associated with a specific threshold
      *  which can be used in POST modification requests.
      *
      * @param humidity The value of the humidity limit to set, from 5 - 95.
      * @param enabled Whether alerts/reminders are enabled for this notification type
      * @param remindTechnician Whether alerts/reminders should be sent to the technician/contractor associated with the thermostat.
      *
      * @see HumidityLimit
      */
    case class HumidityLimitModification(humidity : Option[Int] = None, override val enabled : Option[Boolean] = None,
        override val remindTechnician : Option[Boolean] = None)
    extends LimitModification(humidity, enabled, remindTechnician) {
      humidity.map { h =>
        enabled.map { e =>
          if (e && (h < 5 || h > 95))
            throw new IllegalArgumentException("Humidity limit values must be between 5 and 95 percent")
        }
      }
    }


    /** An auxiliary outdoor alert or reminder in [[NotificationSettings notification settings]] associated with a specific threshold.
      *
      * @note This can be used in GET requests only.  Use the [[#asWritable]] method to grab an instance valid for writing in POST operations.
      *
      * @param temperature The value of the temperature limit to set, from 32 - 79 degrees F (0 - 26 degrees C).
      * @param enabled Whether alerts/reminders are enabled for this notification type
      * @param remindTechnician Whether alerts/reminders should be sent to the technician/contractor associated with the thermostat.
      */
    case class AuxOutdoorLimit(temperature : Option[Temperature] = None, override val enabled : Option[Boolean] = None,
        override val remindTechnician : Option[Boolean] = None)
    extends Limit(temperature.map(_.degrees), enabled, LimitType.AuxOutdoor, remindTechnician) {
      def asWriteable = new AuxOutdoorLimitModification(temperature, enabled, remindTechnician)
    }



    /** An auxiliary outdoor alert or reminder in [[NotificationSettings notification settings]] associated with a specific threshold
     *  which can be used in POST modification requests.
      *
      * @param temperature The value of the temperature limit to set, from 32 - 79 degrees F (0 - 26 degrees C).
      * @param enabled Whether alerts/reminders are enabled for this notification type
      * @param remindTechnician Whether alerts/reminders should be sent to the technician/contractor associated with the thermostat.
      *
      * @see AuxOutdoor
      */
    case class AuxOutdoorLimitModification(temperature : Option[Temperature] = None, override val enabled : Option[Boolean] = None,
        override val remindTechnician : Option[Boolean] = None)
    extends LimitModification(temperature.map(_.degrees), enabled, remindTechnician) {
      temperature.map { t =>
        enabled.map { e =>
          if (e && (t.degrees < 320 || t.degrees > 790))
            throw new IllegalArgumentException("AuxOutdoor temperature limits must be between 32 and 79 degrees Fahrenheit")
        }
      }
    }


    /** An auxiliary outdoor alert or reminder in [[NotificationSettings notification settings]] associated with a specific threshold.
      *
      * @note This can be used in GET requests only.  Use the [[#asWritable]] method to grab an instance valid for writing in POST operations.
      *
      * @param temperature The value of the temperature limit to set, from 32 - 79 degrees F (0 - 26 degrees C).
      * @param enabled Whether alerts/reminders are enabled for this notification type
      * @param remindTechnician Whether alerts/reminders should be sent to the technician/contractor associated with the thermostat.
      */
    case class AuxHeatLimit(temperature : Option[Temperature] = None, override val enabled : Option[Boolean] = None,
        override val remindTechnician : Option[Boolean] = None)
    extends Limit(temperature.map(_.degrees), enabled, LimitType.AuxHeat, remindTechnician) {
      def asWriteable = new AuxHeatLimitModification(temperature, enabled, remindTechnician)
    }

    /** An auxiliary outdoor alert or reminder in [[NotificationSettings notification settings]] associated with a specific threshold
     *  which can be used in POST modification requests.
      *
      * @param temperature The value of the temperature limit to set, from 32 - 79 degrees F (0 - 26 degrees C).
      * @param enabled Whether alerts/reminders are enabled for this notification type
      * @param remindTechnician Whether alerts/reminders should be sent to the technician/contractor associated with the thermostat.
      *
      * @see AuxOutdoor
      */
    case class AuxHeatLimitModification(temperature : Option[Temperature] = None, override val enabled : Option[Boolean] = None,
        override val remindTechnician : Option[Boolean] = None)
    extends LimitModification(temperature.map(_.degrees), enabled, remindTechnician) {
      temperature.map { t =>
        enabled.map { e =>
          if (e && (t.degrees < 0))
            throw new IllegalArgumentException("AuxHeat temperature limits must be above 0 degrees Fahrenheit")
        }
      }
    }

  }



  type LimitType = LimitType.Entry
  object LimitType extends JsonStringEnum {
    val LowTemp = Val("lowTemp")
    val HiTemp = Val("highTemp")
    val LowHumidity = Val("lowHumidity")
    val HiHumidity = Val("highHumidity")
    val AuxHeat = Val("auxHeat")
    val AuxOutdoor = Val("auxOutdoor")
  }



  implicit object LimitFormat extends RootJsonFormat[Limit] {
    import Limit._

    def read(json: JsValue): Limit = json match {
      case o : JsObject => {
        val enabled = findOptional[Boolean](o, "enabled")
        val remindTechnician = findOptional[Boolean](o, "remindTechnician")
        val limit = findOptional[Int](o, "limit")
        o.fields("type") match {
          case JsString(LimitType.LowTemp.entry) => TempLimit(limit.map(Temperature.apply), enabled, true, remindTechnician)
          case JsString(LimitType.HiTemp.entry) => TempLimit(limit.map(Temperature.apply), enabled, false, remindTechnician)
          case JsString(LimitType.LowHumidity.entry) => HumidityLimit(limit, enabled, true, remindTechnician)
          case JsString(LimitType.HiHumidity.entry) => HumidityLimit(limit, enabled, false, remindTechnician)
          case JsString(LimitType.AuxHeat.entry) => AuxHeatLimit(limit.map(Temperature.apply), enabled, remindTechnician)
          case JsString(LimitType.AuxOutdoor.entry) => AuxOutdoorLimit(limit.map(Temperature.apply), enabled, remindTechnician)
          case x => deserializationError(s"'$x' is an invalid Limit type that was detected in JSON, $json")
        }
      }
      case _ => deserializationError("Invalid Limit JSON returned: ${json}")
    }

    def write(obj: Limit): JsValue = {
      val objType = obj match {
        case o : TempLimit => if (o.isLowTempAlert) LimitType.LowTemp else LimitType.HiTemp
        case o : HumidityLimit => if (o.isLowHumidityAlert) LimitType.LowHumidity else LimitType.HiHumidity
        case _ : AuxOutdoorLimit => LimitType.AuxOutdoor
        case _ : AuxHeatLimit => LimitType.AuxHeat
      }
      val m = scala.collection.mutable.Map[String,JsValue]("type" -> JsString(objType.toString))
      obj.enabled.foreach {v => m += (("enabled", JsBoolean(v) ))}
      obj.limit.foreach {v => m += (("limit", JsNumber(v) ))}
      obj.remindTechnician.foreach {v => m += (("remindTechnician", JsBoolean(v) ))}
      JsObject(m.toMap)
    }
  }


  implicit object LimitModificationFormat extends RootJsonFormat[LimitModification] {
    import Limit._

    def read(json: JsValue): LimitModification =
      deserializationError("Cannot deserialize Limit modification JSON; limit type is indeterminate.")

    def write(obj: LimitModification): JsValue = {
      val m = scala.collection.mutable.Map.empty[String,JsValue]
      obj.enabled.foreach {v => m += (("enabled", JsBoolean(v) ))}
      obj.limit.foreach {v => m += (("limit", JsNumber(v) ))}
      obj.remindTechnician.foreach {v => m += (("remindTechnician", JsBoolean(v) ))}
      JsObject(m.toMap)
    }
  }
  // ################################################################


  object FilterLife {
    private[NotificationSettings] def parse(lifeValue : Option[Int], lifeUnit : Option[String]) : Option[FilterLife] = {
      for {
        value   <- lifeValue
        unit    <- lifeUnit
        period = unit match {
          case "month" => Period.months(value)
          case "hour"  => Period.hours(value)
          case x       => throw new IllegalArgumentException(s"Invalid Filter Life unit used: ${x}")
        }
      } yield FilterLife(period)
    }

    private[NotificationSettings] def toMap(filterLife : FilterLife) = {
      val (unit,length) =
        if (filterLife.life.getMonths > 0) (("month",filterLife.life.getMonths))
        else if (filterLife.life.getHours > 0) (("hour",filterLife.life.getHours))
        else serializationError(s"Unexpectedly found unserializable filterLife, ${filterLife}, when trying to construct Equipment object")

      Map("filterLife" -> length.toJson, "filterLifeUnits" -> unit.toJson)
    }
  }
  case class FilterLife(life : Period)


  // ########################################################


  implicit object EquipmentFormat extends RootJsonFormat[Equipment] {
    def read(json : JsValue) : Equipment = {
      json match {
        case o : JsObject ⇒try {
          Equipment(
            filterLastChanged = findOptional[Time.DateOnly](o, "filterLastChanged"),
            filterLife        = FilterLife.parse(findOptional[Int](o, "filterLife"), findOptional[String](o, "filterLifeUnits")),
            remindMeDate      = findOptional[Time.DateOnly](o, "remindMeDate"),
            enabled           = findOptional[Boolean](o, "enabled"),
            `type`            = find[EquipmentType](o, "type"),
            remindTechnician  = findOptional[Boolean](o, "remindTechnician"),
          )
        }
        catch {
          case e : Throwable => deserializationError(s"Could not parse Equipment JSON: ${json}", e)
        }
        case _ => deserializationError(s"Invalid Equipment JSON read: ${json}")
      }
    }

    def write(obj: Equipment): JsValue = {
      val map = scala.collection.mutable.Map.empty[String,JsValue]
      map += (("type", obj.`type`.toJson))
      obj.filterLastChanged foreach { v => map += (("filterLastChanged", v.toJson)) }
      obj.remindMeDate foreach { v => map += (("remindMeDate", v.toJson)) }
      obj.enabled foreach { v => map += (("enabled", v.toJson)) }
      obj.remindTechnician foreach { v => map += (("remindTechnician", v.toJson)) }
      obj.filterLife foreach { v => map ++= FilterLife.toMap(v) }
      JsObject(map.toMap)
    }
  }

  implicit val NotificationSettingsFormat = DefaultJsonProtocol.jsonFormat5(NotificationSettings.apply)
}