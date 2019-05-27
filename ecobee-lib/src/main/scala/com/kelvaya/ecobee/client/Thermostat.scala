package com.kelvaya.ecobee.client

import spray.json._
import spray.json.DefaultJsonProtocol._
import com.kelvaya.ecobee.client.service.EquipmentStatusListItem

object Thermostat {
  implicit object Format extends RootJsonFormat[Thermostat] {
    def read(json: JsValue): Thermostat = {
      ???
    }

    def write(obj: Thermostat): JsValue = {
      ???
    }
  }
}


/** Ecobee thermostat
  *
  * @note This can be used in GET requests only.  Use the [[#asWritable]] method to grab an instance valid for writing in POST operations.
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
  * @param privacy The privacy settings for the Thermostat.
  * @param version The firmware version information for the Thermostat
  * @param securitySettings The security settings for the Thermostat.
  * @param remoteSensors The list of `RemoteSensor` objects for the Thermostat.
  */
case class Thermostat(
    identifier :           String,
    name :                 Option[String] = None,
    thermostatRev :        Option[String] = None,
    isRegistered :         Option[Boolean] = None,
    modelNumber :          Option[String] = None,
    brand :                Option[String] = None,
    features :             Option[String] = None,
    lastModified :         Option[String] = None,
    thermostatTime :       Option[String] = None,
    utcTime :              Option[String] = None,
    audio :                Option[Audio] = None,
    alerts :               Option[Array[Alert]] = None,
//    reminders :            Option[Array[ThermostatReminder2]] = None, -- NB: Specs missing from Ecobee docs
    settings :             Option[ThermostatSettings] = None,
    runtime :              Option[ThermostatRuntime] = None,
    extendedRuntime :      Option[ExtendedRuntime] = None,
    electricity :          Option[Electricity] = None,
    devices :              Option[Array[Device]] = None,
    location :             Option[Location] = None,
//    energy :               Option[Energy] = None,  -- NB: Specs missing from Ecobee docs
    technician :           Option[Technician] = None,
    utility :              Option[Utility] = None,
    management :           Option[Management] = None,
    weather :              Option[Weather] = None,
    events :               Option[Array[Event]] = None,
    program :              Option[Program] = None,
    houseDetails :         Option[HouseDetails] = None,
//    oemCfg :               Option[ThermostatOemCfg] = None, -- NB: Specs missing from Ecobee docs
    equipmentStatus :      Option[EquipmentStatusListItem] = None,
    notificationSettings : Option[NotificationSettings] = None,
    privacy :              Option[ThermostatPrivacy] = None,
    version :              Option[Version] = None,
    securitySettings :     Option[SecuritySettings] = None,
    remoteSensors :        Option[Array[RemoteSensor]] = None
) extends ApiObject {
  def asWriteable =
    ThermostatModification(identifier, name, audio, settings, location, program,
        houseDetails, notificationSettings, privacy, securitySettings)
}


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
  * @param privacy The privacy settings for the Thermostat.
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
    privacy :              Option[ThermostatPrivacy]    = None,
    securitySettings :     Option[SecuritySettings]     = None
) extends WriteableApiObject