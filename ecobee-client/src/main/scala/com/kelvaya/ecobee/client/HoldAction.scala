package com.kelvaya.ecobee.client

import com.kelvaya.util.jsonenum.JsonStringEnum

/** The end time setting for temperature holds */
object HoldAction extends JsonStringEnum {
  val Hour4 = Val("useEndTime4hour")

  /** @note This can only be used by EMS thermostats */
  val Hour2 = Val("useEndTime2hour")

  val NextPeriod = Val("nextPeriod")
  val Indefinite = Val("indefinite")
  val AskMe = Val("askMe")
}