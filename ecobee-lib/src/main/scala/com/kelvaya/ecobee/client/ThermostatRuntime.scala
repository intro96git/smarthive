package com.kelvaya.ecobee.client

import spray.json.DefaultJsonProtocol


object ThermostatRuntime {
  implicit val wtf = DefaultJsonProtocol.jsonFormat0(ThermostatRuntime.apply)
}

/** The last known thermostat running state */
case class ThermostatRuntime()

/*
runtimeRev   String   yes   no   The current runtime revision. Equivalent in meaning to the runtime revision number in the thermostat summary call.
connected   Boolean   yes   no   Whether the thermostat is currently connected to the server.
firstConnected   String   yes   no   The UTC date/time stamp of when the thermostat first connected to the ecobee server.
connectDateTime   String   no   no   The last recorded connection date and time.
disconnectDateTime   String   no   no   The last recorded disconnection date and time.
lastModified   String   yes   no   The UTC date/time stamp of when the thermostat was updated. Format: YYYY-MM-DD HH:MM:SS
lastStatusModified   String   yes   no   The UTC date/time stamp of when the thermostat last posted its runtime information. Format: YYYY-MM-DD HH:MM:SS
runtimeDate   String   yes   no   The UTC date of the last runtime reading. Format: YYYY-MM-DD
runtimeInterval   Integer   yes   no   The last 5 minute interval which was updated by the thermostat telemetry update. Subtract 2 from this interval to obtain the beginning interval for the last 3 readings. Multiply by 5 mins to obtain the minutes of the day. Range: 0-287
actualTemperature   Integer   yes   no   The current temperature displayed on the thermostat.
actualHumidity   Integer   yes   no   The current humidity % shown on the thermostat.
rawTemperature   Integer   yes   no   The dry-bulb temperature recorded by the thermostat. When Energy.FeelsLikeMode is set to humidex, Runtime.actualTemperature will report a "feels like" temperature.
showIconMode   Integer   yes   no   The currently displayed icon on the thermostat.
desiredHeat   Integer   yes   no   The desired heat temperature as per the current running program or active event.
desiredCool   Integer   yes   no   The desired cool temperature as per the current running program or active event.
desiredHumidity   Integer   yes   no   The desired humidity set point.
desiredDehumidity   Integer   yes   no   The desired dehumidification set point.
desiredFanMode   String   yes   no   The desired fan mode. Values: auto, on or null if the HVAC system is off and the thermostat is not controlling a fan independently.
desiredHeatRange   Integer[]   yes   no   This field provides the possible valid range for which a desiredHeat setpoint can be set to. This value takes into account the thermostat heat temperature limits as well the running program or active events. Values are returned as an Integer array representing the canonical minimum and maximim, e.g. [450,790].
desiredCoolRange   Integer[]   yes   no   This field provides the possible valid range for which a desiredCool setpoint can be set to. This value takes into account the thermostat cool temperature limits as well the running program or active events. Values are returned as an Integer array representing the canonical minimum and maximim, e.g. [650,920].
*/