package com.kelvaya.ecobee.client

import com.kelvaya.util.enum.JsonStringEnum

/** The type of ventilator present for the Thermostat. */
object VentilatorType extends JsonStringEnum {
  val None = Val("none")
  val Ventilator = Val("ventilator")
  val HRV = Val("hrv")
  val ERV = Val("erv")
}