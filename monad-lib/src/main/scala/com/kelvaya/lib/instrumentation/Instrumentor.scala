package com.kelvaya.lib.instrumentation

import scala.reflect._


object Instrumentor {
  implicit def nonprobe[A,B] : FunctionInstrumentor[A,B] = new FunctionInstrumentor[A,B]
  implicit def probe[A,B,P<:Prober](implicit prober : P) : ProbingInstrumentor[P,A=>B] = new SimpleProbingInstrumentor[P,A,B]
}


trait Instrumentor[F] {
  type Arg
  type Return
  type Probe <: Prober
  type Data

  def probe(f : F, arg : Arg) : Return = applyProbe(f)(arg)

  // think about this a bit more...sig seems off, since this won't be blindly called
  // after everything else is done.  Would think that probe might be here too w/ info?
  // And what about the stuff the instrumentor does itself?  Are we just using "stateful"
  // crap to track that?
  def collectData(ret : Return) : Executed[Data] = ???

  protected def applyProbe(f : F) : (Arg) => Return
  protected def probe : Probe
}

