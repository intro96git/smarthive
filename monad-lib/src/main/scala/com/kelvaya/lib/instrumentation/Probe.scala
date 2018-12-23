package com.kelvaya.lib.instrumentation

object Prober {
  implicit val BasicProber : Prober = BasicProbe
}

trait Prober {

}