package com.kelvaya.lib.instrumentation

import scala.annotation.implicitNotFound

@implicitNotFound("ProbingInstrumentor for type [${A}] required, but not found.  Check that (a) type [${A}] is a function type; and (b) there is a Prober implicitly available as well.")
trait ProbingInstrumentor[P<:Prober,A] extends Instrumentor[Function1[P,A]] {
  type Probe = P
  final protected def applyProbe(fn : P => (Arg=>Return)) : Arg => Return = { fn(this.probe) }
}


final class SimpleProbingInstrumentor[P<:Prober,A,B](implicit p : P) extends ProbingInstrumentor[P,Function1[A,B]] {
  type Arg = A
  type Return = B
  type Data = B

  protected val probe = p
}


final class DefaultProbingInstrumentor[A,B] extends ProbingInstrumentor[BasicProbe.type,Function1[A,B]] {
  type Arg = A
  type Return = B
  type Data = B

  protected val probe = BasicProbe
}