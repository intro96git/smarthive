package com.kelvaya.lib.instrumentation

import scala.reflect._

trait Instrumentor[F] {
  type Arg
  type Return
  type Probe <: Prober

  def probe(f : F, arg : Arg)(implicit probe : Prober) : Executed[Return] =
    new Executed(applyProbe(probe,f)(arg), Seq.empty)

  def probeWith(prober : Prober, f : F, arg : Arg) : Executed[Return] = this.probe(f, arg)(prober)

  protected def applyProbe(probe : Prober, f : F) : (Arg) => Return
}
