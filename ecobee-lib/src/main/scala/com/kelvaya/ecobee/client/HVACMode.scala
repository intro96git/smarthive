package com.kelvaya.ecobee.client

import com.kelvaya.util.enum.JsonStringEnum

/** The HVAC mode of the thermostat is in */
object HVACMode extends JsonStringEnum {
  val Auto = Val("auto")
  val AuxHeatOnly = Val("autoHeadOnly")
  val Cool = Val("cool")
  val Heat = Val("heat")
  val Off = Val("off")
}