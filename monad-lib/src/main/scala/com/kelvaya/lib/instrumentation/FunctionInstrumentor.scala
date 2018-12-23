package com.kelvaya.lib.instrumentation

class FunctionInstrumentor[A,B] extends Instrumentor[Function1[A,B]] {
  type Arg = A
  type Return = B
  type Data = B

  protected def applyProbe(fn : Function1[A,B]) : A => B = { fn }
  protected def probe : Probe = throw new NoSuchElementException
}
