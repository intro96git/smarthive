package com.kelvaya.ecobee.client.service

import akka.event.LoggingBus
import spray.json.RootJsonFormat
import spray.json.JsonFormat

object Select {
  private[client] implicit def getFormat(implicit lb : LoggingBus) : RootJsonFormat[Select] = new SelectFormat
}


/** Specifies the data to return when sending requests to the Ecobee API
  *
  * @param selectType   The type of data requested
  * @param includeRuntime   Include the thermostat runtime object. Defaults to false.
  * @param includeExtendedRuntime   Include the extended thermostat runtime object. Defaults to false.
  * @param includeElectricity   Include the electricity readings object. Defaults to false.
  * @param includeSettings   Include the thermostat settings object. Defaults to false.
  * @param includeLocation   Include the thermostat location object. Defaults to false.
  * @param includeProgram   Include the thermostat program object. Defaults to false.
  * @param includeEvents   Include the thermostat calendar events objects. Defaults to false.
  * @param includeDevice   Include the thermostat device configuration objects. Defaults to false.
  * @param includeTechnician   Include the thermostat technician object. Defaults to false.
  * @param includeUtility   Include the thermostat utility company object. Defaults to false.
  * @param includeManagement   Include the thermostat management company object. Defaults to false.
  * @param includeAlerts   Include the thermostat's unacknowledged alert objects. Defaults to false.
  * @param includeReminders Include the theromstat's reminders.  Defaults to false.
  * @param includeWeather   Include the current thermostat weather forecast object. Defaults to false.
  * @param includeHouseDetails   Include the current thermostat house details object. Defaults to false.
  * @param includeOemCfg   Include the current thermostat OemCfg object. Defaults to false.
  * @param includeEquipmentStatus   Include the current thermostat equipment status information. Defaults to false.
  * @param includeNotificationSettings   Include the current thermostat alert and reminders settings. Defaults to false.
  * @param includePrivacy   Include the current thermostat privacy settings. Defaults to false.
  * @param includeVersion   Include the current firmware version the Thermostat is running. Defaults to false.
  * @param includeSecuritySettings   Include the current securitySettings object for the selected Thermostat(s). Defaults to false.
  * @param includeSensors   Include the list of current thermostatRemoteSensor objects for the selected Thermostat(s). Defaults to false.
  * @param includeAudio   Include the audio configuration for the selected Thermostat(s). Defaults to false.
  * @param includeEnergy  Include the energy configuration for the selected Thermostat(s). Defaults to false.
  */
case class Select(
    selectType :                  SelectType,
    includeRuntime :              Boolean = false,
    includeExtendedRuntime :      Boolean = false,
    includeElectricity :          Boolean = false,
    includeSettings :             Boolean = false,
    includeLocation :             Boolean = false,
    includeProgram :              Boolean = false,
    includeEvents :               Boolean = false,
    includeDevice :               Boolean = false,
    includeTechnician :           Boolean = false,
    includeUtility :              Boolean = false,
    includeManagement :           Boolean = false,
    includeAlerts :               Boolean = false,
    includeReminders :            Boolean = false,
    includeWeather :              Boolean = false,
    includeHouseDetails :         Boolean = false,
    includeOemCfg :               Boolean = false,
    includeEquipmentStatus :      Boolean = false,
    includeNotificationSettings : Boolean = false,
    includePrivacy :              Boolean = false,
    includeVersion :              Boolean = false,
    includeSecuritySettings :     Boolean = false,
    includeSensors :              Boolean = false,
    includeAudio :                Boolean = false,
    includeEnergy :               Boolean = false
)
