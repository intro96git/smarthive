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

case class Thermostat(
    identifier :           String,
    name :                 String,
    thermostatRev :        String,
    isRegistered :         Boolean,
    modelNumber :          String,
    brand :                String,
    features :             String,
    lastModified :         String,
    thermostatTime :       String,
    utcTime :              String,
    audio :                Audio,
    alerts :               Array[Alert],
//    reminders :            Array[ThermostatReminder2], -- NB: Specs missing from Ecobee docs
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
    program :              Program,
    houseDetails :         HouseDetails,
//    oemCfg :               ThermostatOemCfg, -- NB: Specs missing from Ecobee docs
    equipmentStatus :      EquipmentStatusListItem,
    notificationSettings : NotificationSettings,
    privacy :              ThermostatPrivacy,
    version :              Version,
    securitySettings :     SecuritySettings,
    remoteSensors :        Array[RemoteSensor]
)


/*
identifier   String   yes   yes   The unique thermostat serial number.
name   String   no   no   A user defined name for a thermostat.
thermostatRev   String   yes   no   The current thermostat configuration revision.
isRegistered   Boolean   yes   no   Whether the user registered the thermostat.
modelNumber   String   yes   no   The thermostat model number.

Values: apolloSmart, apolloEms, idtSmart, idtEms, siSmart, siEms, athenaSmart, athenaEms, corSmart, nikeSmart, nikeEms
brand   String   yes   no   The thermostat brand.
features   String   yes   no   The comma-separated list of the thermostat's additional features, if any.
lastModified   String   yes   no   The last modified date time for the thermostat configuration.
thermostatTime   String   yes   no   The current time in the thermostat's time zone
utcTime   String   yes   no   The current time in UTC.
audio   Audio   no   no   The thermostat audio configuration
alerts   Alert[]   yes   no   The list of Alert objects tied to the thermostat
reminders   ThermostatReminder2[]   yes   no
settings   Settings   no   no   The thermostat Setting object linked to the thermostat
runtime   Runtime   yes   no   The Runtime state object for the thermostat
extendedRuntime   ExtendedRuntime   yes   no   The ExtendedRuntime object for the thermostat
electricity   Electricity   yes   no   The Electricity object for the thermostat
devices   Device[]   yes   no   The list of Device objects linked to the thermostat
location   Location   no   no   The Location object for the thermostat
energy   Energy   no   no   The thermostat energy configuration
technician   Technician   yes   no   The Technician object associated with the thermostat containing the technician contact information
utility   Utility   yes   no   The Utility object associated with the thermostat containing the utility company information
management   Management   yes   no   The Management object associated with the thermostat containing the management company information
weather   Weather   yes   no   The Weather object linked to the thermostat representing the current weather on the thermostat.
events   Event[]   yes   no   The list of Event objects linked to the thermostat representing any events that are active or scheduled.
program   Program   no   no   The Program object for the thermostat
houseDetails   HouseDetails   no   no   The houseDetails object contains contains the information about the house the thermostat is installed in.
oemCfg   ThermostatOemCfg   no   no   The OemCfg object contains information about the OEM specific thermostat.
equipmentStatus   String   yes   no   The status of all equipment controlled by this Thermostat. Only running equipment is listed in the CSV String.

Values: heatPump, heatPump2, heatPump3, compCool1, compCool2, auxHeat1, auxHeat2, auxHeat3, fan, humidifier, dehumidifier, ventilator, economizer, compHotWater, auxHotWater.

Note: If no equipment is currently running an empty String is returned. If Settings.hasHeatPump is true, heatPump value will be returned for heating, compCool for cooling, and auxHeat for aux heat. If Settings.hasForcedAir or Settings.hasBoiler is true, auxHeat value will be returned for heating and compCool for cooling (heatPump will not show up for heating).
notificationSettings   NotificationSettings   no   no   The NotificationSettings object containing the configuration for Alert and Reminders for the Thermostat.
privacy   ThermostatPrivacy   no   no   The Privacy object containing the privacy settings for the Thermostat. Note: access to this object is restricted to callers with implict authentication.
version   Version   yes   no   The Version object containing the firmware version information for the Thermostat. For example: "3.5.0.3957".
securitySettings   SecuritySettings   no   no   The SecuritySettings object containing the security settings for the Thermostat.
remoteSensors   RemoteSensor[]   yes   no   The list of RemoteSensor objects for the Thermostat.
*/