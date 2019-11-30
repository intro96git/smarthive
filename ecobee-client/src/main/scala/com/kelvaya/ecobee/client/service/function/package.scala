package com.kelvaya.ecobee.client.service

/** Collection of convenience [[ThermostatFunction ThermostatFunctions]] that are supported by the Ecobee API.
  *
  *  The base trait for all of these functions is [[EcobeeFunction]].  All concrete classes of `EcobeeFunction`
  *  can be implicitly converted to a `ThermostatFunction`, which means that they can be directly used in a call
  *  to [[ThermostatPostRequest]] or [[ThermostatPostService]].
  *
  *  Thermostat "functions" are additional parameters that can be sent on thermostat POST requests to allow
  *  more complicated actions other than simple thermostat property modifications.
  *
  *  @example
{{{


val startVacation = CreateVacation(
  vacationName = "vaca",
  coolHoldTemp = Temperature.fromCelcius(30),
  heatHoldTemp = Temperature.fromCelcius(65),
  startDateTime = DateTime.now(),
  endDateTime = DateTime.now().plusWeeks(2),
  fan = FanMode.Auto,
  fanMinOnTime = 0
)

val httpRequest = ThermostatPostService(selectType = SelectType.Registered("main"), functions = Seq(startVacation))
...

val endVacation = DeleteVacation("vaca")
val httpRequest = ThermostatPostService(selectType = SelectType.Registered("main"), functions = Seq(endVacation))
}}}
  */
package object function