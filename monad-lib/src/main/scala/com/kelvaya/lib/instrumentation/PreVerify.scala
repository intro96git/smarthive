package com.kelvaya.lib.instrumentation

object PreVerify {
  def apply(op : Operation)(verifyFn : op.Arg => Boolean) = new PreVerify {
    val operation=op
    val fn=verifyFn.asInstanceOf[operation.Arg => Boolean] // YUCK...consider going back to type params w/ no typeclasses
  }
}

trait PreVerify {
  val operation : Operation
  val fn : operation.Arg => Boolean
}