package com.kelvaya.ecobee.server



/** Readings from a single registered thermostat 
  *
  * @param name The name of the thermostat
  * @param tempC The temperature as read at the thermostat (in Celcius)
  * @param tempF The temperature as read at the thermostat (in Fahrenheit)
  */
final case class ThermostatStats(name : String, celcius : Float, fahrenheit : Float)



/** Readings from a all registered thermostats 
  *
  * @param thermostats The statistics from all read thermostats
  */
final case class Thermostats(thermostats : Iterable[ThermostatStats])
