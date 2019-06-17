package com.kelvaya.ecobee.client

import spray.json._
import spray.json.DefaultJsonProtocol._
import com.kelvaya.ecobee.client.service.EquipmentStatusListItem
import com.kelvaya.util.SprayImplicits
import akka.event.LoggingBus
import com.kelvaya.util.Time.FullDate


/** Ecobee thermostat
  *
  * @note This can be used in GET requests only.  Use [[ThermostatModification]] to create an instance valid for writing in POST operations.
  *
  * @param identifier The unique thermostat serial number.
  * @param name A user defined name for a thermostat.
  * @param thermostatRev The current thermostat configuration revision.
  * @param isRegistered Whether the user registered the thermostat.
  * @param modelNumber The thermostat model number.
  * @param brand The thermostat brand.
  * @param features The comma-separated list of the thermostat's additional features, if any.
  * @param lastModified The last modified date time for the thermostat configuration.
  * @param thermostatTime The current time in the thermostat's time zone
  * @param utcTime The current time in UTC.
  * @param audio The thermostat audio configuration
  * @param alerts The list of `Alert` objects tied to the thermostat
  * @param settings The `Setting` object linked to the thermostat
  * @param runtime The `Runtime` state object for the thermostat
  * @param extendedRuntime The `ExtendedRuntime` object for the thermostat
  * @param electricity The `Electricity` object for the thermostat
  * @param devices The list of `Device` objects linked to the thermostat
  * @param location The `Location` object for the thermostat
  * @param technician The technician contact information
  * @param utility The utility company information
  * @param management The management company information
  * @param weather The current weather on the thermostat.
  * @param events The list of events that are active or scheduled.
  * @param program The `Program` object for the thermostat
  * @param houseDetails The information about the house the thermostat is installed in.
  * @param equipmentStatus The status of all running equipment controlled by this Thermostat.
  * @param notificationSettings  The configuration for Alert and Reminders for the Thermostat.
  * @param version The firmware version information for the Thermostat
  * @param securitySettings The security settings for the Thermostat.
  * @param remoteSensors The list of `RemoteSensor` objects for the Thermostat.
  */
case class Thermostat(
    identifier :           String,
    name :                 String,
    thermostatRev :        String,
    isRegistered :         Boolean,
    modelNumber :          String,
    brand :                String,
    features :             String,
    lastModified :         FullDate,
    thermostatTime :       FullDate,
    utcTime :              FullDate,
    audio :                Audio,
    alerts :               Array[Alert],
//    reminders :            Option[Array[ThermostatReminder2], -- NB: Specs missing from Ecobee docs
    settings :             ThermostatSettings,
    runtime :              ThermostatRuntime,
    extendedRuntime :      ExtendedRuntime,
    electricity :          Electricity,
    devices :              Array[Device],
    location :             Location,
//    energy :               Energy,  -- NB: Specs missing from Ecobee docs
    technician :           Technician,
    utility :              Utility,
    management :           Management,
    weather :              Weather,
    events :               Array[Event],
    houseDetails :         HouseDetails,
    program :              Program,
    equipmentStatus :      EquipmentStatusListItem,
    notificationSettings : NotificationSettings,
    version :              Version,
    securitySettings :     SecuritySettings,
//    oemCfg :               ThermostatOemCfg, -- NB: Specs missing from Ecobee docs
//    privacy :              ThermostatPrivacy, -- NB: Specs missing from Ecobee docs
    remoteSensors :        Array[RemoteSensor]
) extends ReadonlyApiObject


/** Ecobee thermostat which can be used in POST modification requests
  *
  * @param identifier The unique thermostat serial number.
  * @param name A user defined name for a thermostat.
  * @param audio The thermostat audio configuration
  * @param settings The `Setting` object linked to the thermostat
  * @param location The `Location` object for the thermostat
  * @param program The `Program` object for the thermostat
  * @param houseDetails The information about the house the thermostat is installed in.
  * @param notificationSettings  The configuration for Alert and Reminders for the Thermostat.
  * @param securitySettings The security settings for the Thermostat.
  *
  * @see Thermostat
  */
case class ThermostatModification(
    identifier : String,
    name :       Option[String]             = None,
    audio :      Option[Audio]              = None,
    settings :   Option[ThermostatSettings] = None,
    location :   Option[Location]           = None,
    //    energy :               Option[Energy] = None,  -- NB: Specs missing from Ecobee docs
    program :      Option[Program]      = None,
    houseDetails : Option[HouseDetails] = None,
    //    oemCfg :               Option[ThermostatOemCfg] = None, -- NB: Specs missing from Ecobee docs
    notificationSettings : Option[NotificationSettings] = None,
//    privacy :              Option[ThermostatPrivacy]    = None, -- NB: Specs missing from Ecobee docs
    securitySettings :     Option[SecuritySettings]     = None
) extends WriteableApiObject



object Thermostat extends SprayImplicits {
  implicit def thermostatFormat(implicit ev : LoggingBus) = new RootJsonFormat[Thermostat] {
    def read(json: JsValue): Thermostat = json match {
      case o : JsObject =>
      Thermostat(
        identifier = find[String](o, "identifier"),
        name = find[String](o, "name"),
        thermostatRev = find[String](o, "thermostatRev"),
        isRegistered = find[Boolean](o, "isRegistered"),
        modelNumber = find[String](o, "modelNumber"),
        brand = find[String](o, "brand"),
        features = find[String](o, "features"),
        lastModified = find[FullDate](o, "lastModified"),
        thermostatTime = find[FullDate](o, "thermostatTime"),
        utcTime = find[FullDate](o, "utcTime"),
        audio = find[Audio](o, "audio"),
        alerts = find[Array[Alert]](o, "alerts"),
        settings = find[ThermostatSettings](o, "settings"),
        runtime = find[ThermostatRuntime](o, "runtime"),
        extendedRuntime = find[ExtendedRuntime](o, "extendedRuntime"),
        electricity = find[Electricity](o, "electricity"),
        devices = find[Array[Device]](o, "devices"),
        location = find[Location](o, "location"),
        technician = find[Technician](o, "technician"),
        utility = find[Utility](o, "utility"),
        management = find[Management](o, "management"),
        weather = find[Weather](o, "weather"),
        events = find[Array[Event]](o, "events"),
        houseDetails = find[HouseDetails](o, "houseDetails"),
        program = find[Program](o, "program"),
        equipmentStatus = find[EquipmentStatusListItem](o, "equipmentStatus"),
        notificationSettings = find[NotificationSettings](o, "notificationSettings"),
        version = find[Version](o, "version"),
        securitySettings = find[SecuritySettings](o, "securitySettings"),
        remoteSensors = find[Array[RemoteSensor]](o, "remoteSensors")
      )
      case _ => deserializationError(s"$json is not a valid Thermostat payload")
    }

    def write(obj: Thermostat): JsValue = serializationError("Thermostat is a read-only object and cannot be serialized.")
  }
}



object ThermostatModification {
  implicit def thermostatModificationFormat(implicit ev : LoggingBus) = DefaultJsonProtocol.jsonFormat9(ThermostatModification.apply)
}
