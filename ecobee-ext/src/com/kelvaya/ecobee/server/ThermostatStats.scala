package com.kelvaya.ecobee.server

import com.kelvaya.ecobee.client.Temperature

/** Readings from a single registered thermostat 
  *
  * @param name The name of the thermostat
  * @param temp The temperature as read at the thermostat 
  */
final case class ThermostatStats(name : String, temp : Temperature)