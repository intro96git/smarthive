package com.kelvaya.ecobee.client

import spray.json._
import spray.json.DefaultJsonProtocol._


import com.kelvaya.ecobee.client.service.EquipmentStatusListItem
import com.kelvaya.util.SprayImplicits
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
final case class Thermostat(
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
    audio :                Option[Audio] = None,
    alerts :               Option[Array[Alert]] = None,
//    reminders :          Option[  Option][Array[ThermostatReminder2], -- NB: Specs missing from Ecobee docs
    settings :             Option[ThermostatSettings] = None,
    runtime :              Option[ThermostatRuntime] = None,
    extendedRuntime :      Option[ExtendedRuntime] = None,
    electricity :          Option[Electricity] = None,
    devices :              Option[Array[Device]] = None,
    location :             Option[Location] = None,
//    energy :             Option[  Energy],  -- NB: Specs missing from Ecobee docs
    technician :           Option[Technician] = None,
    utility :              Option[Utility] = None,
    management :           Option[Management] = None,
    weather :              Option[Weather] = None,
    events :               Option[Array[Event]] = None,
    houseDetails :         Option[HouseDetails] = None,
    program :              Option[Program] = None,
    equipmentStatus :      Option[EquipmentStatusListItem] = None,
    notificationSettings : Option[NotificationSettings] = None,
    version :              Option[Version] = None,
    securitySettings :     Option[SecuritySettings] = None,
//    oemCfg :             Option[  ThermostatOemCfg], -- NB: Specs missing from Ecobee docs
//    privacy :            Option[  ThermostatPrivacy], -- NB: Specs missing from Ecobee docs
    remoteSensors :        Option[Array[RemoteSensor]] = None
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
  * @see [[Thermostat]]
  */
case class ThermostatModification(
    identifier : String,
    name :       Option[String]                         = None,
    audio :      Option[AudioModification]              = None,
    settings :   Option[ThermostatSettingsModification] = None,
    location :   Option[LocationModification]           = None,
    //    energy :               Option[Energy] = None,  -- NB: Specs missing from Ecobee docs
    program :      Option[Program]      = None,
    houseDetails : Option[HouseDetails] = None,
    //    oemCfg :               Option[ThermostatOemCfg] = None, -- NB: Specs missing from Ecobee docs
    notificationSettings : Option[NotificationSettings] = None,
    //    privacy :              Option[ThermostatPrivacy]    = None, -- NB: Specs missing from Ecobee docs
    securitySettings : Option[SecuritySettings] = None
) extends WriteableApiObject



object Thermostat extends SprayImplicits {
  implicit val ThermostatFormat = new RootJsonFormat[Thermostat] {
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
        audio = findOptional[Audio](o, "audio"),
        alerts = findOptional[Array[Alert]](o, "alerts"),
        settings = findOptional[ThermostatSettings](o, "settings"),
        runtime = findOptional[ThermostatRuntime](o, "runtime"),
        extendedRuntime = findOptional[ExtendedRuntime](o, "extendedRuntime"),
        electricity = findOptional[Electricity](o, "electricity"),
        devices = findOptional[Array[Device]](o, "devices"),
        location = findOptional[Location](o, "location"),
        technician = findOptional[Technician](o, "technician"),
        utility = findOptional[Utility](o, "utility"),
        management = findOptional[Management](o, "management"),
        weather = findOptional[Weather](o, "weather"),
        events = findOptional[Array[Event]](o, "events"),
        houseDetails = findOptional[HouseDetails](o, "houseDetails"),
        program = findOptional[Program](o, "program"),
        equipmentStatus = findOptional[EquipmentStatusListItem](o, "equipmentStatus"),
        notificationSettings = findOptional[NotificationSettings](o, "notificationSettings"),
        version = findOptional[Version](o, "version"),
        securitySettings = findOptional[SecuritySettings](o, "securitySettings"),
        remoteSensors = findOptional[Array[RemoteSensor]](o, "remoteSensors")
      )
      case _ => deserializationError(s"$json is not a valid Thermostat payload")
    }

    def write(obj: Thermostat): JsValue = serializationError("Thermostat is a read-only object and cannot be serialized.")
  }
}



object ThermostatModification {
  implicit val ThermostatModificationFormat = DefaultJsonProtocol.jsonFormat9(ThermostatModification.apply)
}
