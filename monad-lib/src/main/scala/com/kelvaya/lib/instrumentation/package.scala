package com.kelvaya.lib

package object instrumentation {
  def instrument[A,B](fn : Instrumentor[Operation {type Arg=A; type Return=B}]#Probe => A => B) : Instrumented[Function1[A,B]] = ???
  def timed[A,B](fn : A => B) : Instrumented[Function1[A,B]] = ???
}