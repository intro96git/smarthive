package com.kelvaya.lib.instrumentation


object Operation {
  def apply[T,S](f : T => S) = new SimpleOperation(f)
}


trait Operation {
  type Arg
  type Return

  def apply(m : Prober) : (Arg => Return)
}


class SimpleOperation[T,S](f : T => S) extends Operation {
  type Arg = T
  type Return = S

  def apply(m : Prober) : (T => S) = f
}