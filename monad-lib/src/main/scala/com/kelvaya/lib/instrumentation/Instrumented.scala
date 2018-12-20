package com.kelvaya.lib.instrumentation

object Instrumented {
}



trait Instrumented[+T] {
  type Arg
  type Return

  def probe(arg : Arg)(implicit probe : Prober) : Executed[Return]
  def probeWith(prober : Prober, arg : Arg) : Executed[Return] = this.probe(arg)(prober)

  def map[F1 <: Operation : Instrumentor](mapOp : F1) : Instrumented[F1] = {
//    val mapFn = mapOp(instrumentor)
    ???
  }

  def flatMap[F1 <: Operation : Instrumentor](fn : F1) : Instrumented[F1] = {
//    ev.track(fn(e))
    ???
  }

  def foreach(fn : T => Any) : Unit = {
//    fn(e)
    ()
  }

}
