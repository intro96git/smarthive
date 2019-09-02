package com.kelvaya.ecobee.client

import com.kelvaya.util.jsonenum.JsonStringEnum

/** Whether Demand Response requests are accepted by this thermostat. */
object DRAccept extends JsonStringEnum {
  val Always = Val("always")
  val AskMe = Val("askMe")
  val CustomerSelect = Val("customerSelect")
  val DefaultAccept = Val("defaultAccept")
  val DefaultDecline = Val("defaultDecline")
  val Never = Val("never")
}