package com.kelvaya.lib.instrumentation


object PostVerify {
  def apply(op : Operation)(verifyFn : op.Arg => Boolean) : PostVerify = ???
}


abstract class PostVerify extends Operation