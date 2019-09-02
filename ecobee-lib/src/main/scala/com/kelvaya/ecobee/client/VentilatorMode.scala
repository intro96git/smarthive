package com.kelvaya.ecobee.client

import com.kelvaya.util.jsonenum.JsonStringEnum

/** The ventilator mode. */
object VentilatorMode extends JsonStringEnum {
  val Auto = Val("auto")
  val MinOnTime = Val("minontime")
  val On = Val("on")
  val Off = Val("off")
}