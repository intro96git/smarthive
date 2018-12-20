package com.kelvaya.lib.instrumentation

abstract trait Timing {
  val step : String
  val time : Long
  val size : Long
}


case class TimingEntry(step : String, time : Long, size : Long) extends Timing