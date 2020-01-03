package com.kelvaya.ecobee.client

import com.kelvaya.util.Time

import spray.json._
import spray.json.DefaultJsonProtocol._
import com.kelvaya.ecobee.client.Event.FanMode
import com.kelvaya.util.SprayImplicits


object ThermostatRuntime extends SprayImplicits {
  implicit val ThermostatRuntimeFormat = DefaultJsonProtocol.jsonFormat20(ThermostatRuntime.apply)
}

/** The last known thermostat running state
  *
  * @param runtimeRev The current runtime revision
  * @param connected Whether the thermostat is currently connected to the server.
  * @param firstConnected The date/time of when the thermostat first connected to the ecobee server.
  * @param connectDateTime The last recorded connection date and time.
  * @param disconnectDateTime The last recorded disconnection date and time.
  * @param lastModified The date/time of when the thermostat was updated
  * @param lastStatusModified The date/time of when the thermostat last posted its runtime information.
  * @param runtimeDate The date of the last runtime reading
  * @param runtimeInterval The last 5 minute interval which was updated by the thermostat telemetry update. Subtract 2 from this interval to obtain the beginning interval for the last 3 readings. Multiply by 5 mins to obtain the minutes of the day. Range: 0-287
  * @param actualTemperature The current temperature displayed on the thermostat.
  * @param actualHumidity The current humidity % shown on the thermostat.
  * @param rawTemperature The dry-bulb temperature recorded by the thermostat. When Energy.FeelsLikeMode is set to humidex, Runtime.actualTemperature will report a "feels like" temperature.
  * @param showIconMode The currently displayed icon on the thermostat.
  * @param desiredHeat The desired heat temperature as per the current running program or active event.
  * @param desiredCool The desired cool temperature as per the current running program or active event.
  * @param desiredHumidity The desired humidity set point.
  * @param desiredDehumidity The desired dehumidification set point.
  * @param desiredFanMode The desired fan mode
  * @param desiredHeatRange The possible valid range for which a desiredHeat setpoint can be set to
  * @param desiredCoolRange The possible valid range for which a desiredCool setpoint can be set to
  */
case class ThermostatRuntime(
    runtimeRev : String, connected : Boolean, firstConnected : Time.FullDate, connectDateTime : Time.FullDate, disconnectDateTime : Time.FullDate,
    lastModified : Time.FullDate, lastStatusModified : Time.FullDate, runtimeDate : Time.DateOnly, runtimeInterval : Int,
    actualTemperature : Int, actualHumidity : Int, rawTemperature : Int, showIconMode : WeatherForecast.WeatherIcon, desiredHeat : Int,
    desiredCool : Int, desiredHumidity : Int, desiredDehumidity : Int, desiredFanMode : FanMode.Entry,
    desiredHeatRange : Array[Int], desiredCoolRange : Array[Int]
)
