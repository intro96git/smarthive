package com.kelvaya.ecobee.test

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.scalatest.OptionValues
import zio.DefaultRuntime
import org.scalatest.compatible.Assertion
import zio.Task

trait BaseServerTestSpec extends FlatSpec with Matchers with OptionValues


trait ZioServerTestSpec extends BaseServerTestSpec {
  import scala.language.implicitConversions

  /** ZIO Runtime to use when evaluating effectful operations */
  val runtime = new DefaultRuntime { }

  
  implicit def toZio(a : => Assertion) = Task(a)
  
  final def run(t : Task[Assertion]) = this.runtime.unsafeRun(t.either).fold(
        e => throw e,
        s => s
      )      
}