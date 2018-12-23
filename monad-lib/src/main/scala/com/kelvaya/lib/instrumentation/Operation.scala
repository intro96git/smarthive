package com.kelvaya.lib.instrumentation


object Operation {
  def apply[T,S](f : T => S) = new SimpleOperation(f)
  def apply[T,S](f : Instrumentor[ProbeableOp[T,S]]#Probe => T => S) = new ProbeableOp(f)
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


class ProbeableOp[T,S](f : Instrumentor[ProbeableOp[T,S]]#Probe => T => S) {
  type Arg = T
  type Return = S
  def apply = f.apply _
}