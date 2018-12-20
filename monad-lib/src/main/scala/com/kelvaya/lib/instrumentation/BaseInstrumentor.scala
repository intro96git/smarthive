package com.kelvaya.lib.instrumentation

import java.util.Date

import scala.language.higherKinds

object BaseInstrumentor {
  implicit def nonprobe[A,B] = new FunctionInstrumentor[A,B]
  implicit def probe[A,B]    = new SimpleProbingInstrumentor[A,B]
}

final class FunctionInstrumentor[A,B] extends Instrumentor[Function1[A,B]] {
  type Arg = A
  type Return = B
  object Probe extends Prober

  protected def applyProbe(probe : Prober, fn : Function1[A,B]) : A => B = { fn }
}


trait ProbingInstrumentor[A] extends Instrumentor[Function1[Prober,A]] {
  type Return = A
  final protected def applyProbe(probe : Prober, fn : Function1[Prober,A]) = { arg : Arg => fn(probe) }
}


final class SimpleProbingInstrumentor[A,B] extends ProbingInstrumentor[Function1[A,B]] {
  type Arg = A
}