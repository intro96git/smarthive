package com.kelvaya.lib.instrumentation

import scala.reflect._

class Instrumented[F : Instrumentor](x : F) {
  def map[F1: Instrumentor](mapOp : F => F1) : Instrumented[F1] = new Instrumented(mapOp(x))
  def flatMap[F1: Instrumentor](fn : F => Instrumented[F1]) : Instrumented[F1] = fn(x)
  def foreach(fn : F => Any) : Unit = { fn(x); () }
}
