package com.kelvaya.lib

import scala.language.implicitConversions

package object instrumentation {
  type ProbeFn[-P<:Prober,-A,+B] = P => (A => B)
  type Probed[P<:Prober,A,B] = Instrumented[ProbeFn[P,A,B]]

  def instrument[P<:Prober,A,B](fn : ProbeFn[P,A,B])(implicit ev : ProbingInstrumentor[P,A=>B]) : Probed[P,A,B] = new Instrumented(fn)

  def timed[P<:Prober,A,B](fn : A => B)(implicit ev : FunctionInstrumentor[A,B]) = new Instrumented(fn)
}