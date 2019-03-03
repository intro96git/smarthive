package com.kelvaya.ecobee.client

import com.kelvaya.util.enum.JsonStringEnum

/** The humidifier mode. */
object HumidifierMode extends JsonStringEnum {
  val Auto = Val("auto")
  val Manual = Val("manual")
  val Off = Val("off")
}