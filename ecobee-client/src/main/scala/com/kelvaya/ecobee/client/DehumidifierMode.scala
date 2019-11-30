package com.kelvaya.ecobee.client

import com.kelvaya.util.jsonenum.JsonStringEnum

/** Whether the dehumidifier is enabled */
object DehumidifierMode extends JsonStringEnum {
  val On = Val("on")
  val Off = Val("off")
}