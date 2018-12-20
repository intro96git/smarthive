package com.kelvaya.lib.instrumentation

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import scala.util.Try

class InstrumentorSpec extends FlatSpec with Matchers {

  "An operation" must "be instrumentable to track timings" in {

//    val op1 = new Operation {
//      type Arg = Unit
//      type Return = String
//      def apply(i : Instrumentor[this.type]) : (Arg => Return) = { _ => "hello world" }
//      lazy val estimatedSize = 0
//    }
//    implicit val instrumentor = TestInstrumentor
//    val i1 = Instrumented(op1)
//    val ret = i1(())
//    ret.value shouldBe a [String]
//
//    {
//      val op2 = new Operation {
//        type Arg = String
//        type Return = String
//        def apply(i : Instrumentor[this.type]) : (Arg => Return) = { s => s"$s: hello world" }
//        lazy val estimatedSize = 0
//      }
//      import BaseInstrumentor._
//      val i2 = Instrumented(op2)
//      val ret2 = i2("testme")
//      ret2.value shouldBe "testme: hello world"
//    }
  }

  it must "be easy-to-use" in {

    def testFunction(i : Int) : Boolean = if (i >=0) true else false
    val noEstimateOp = Operation(testFunction)
    val preVerifyOp = PreVerify(noEstimateOp) { i : Int =>
      true
    }
    val postVerifyOp = PostVerify(preVerifyOp) { b : Boolean =>
      true
    }

    val results = for {
      v <- Instrumented(postVerifyOp, 4)
      u <- Instrumented(Operation { b : Boolean => !b }, v)
    } yield u

    results shouldBe Instrumented(false)
  }

  it must "be able to change the way timings are performed" in (pending)

  it must "be able to report timings to a file" in (pending)

  it must "be able to read in timings to form estimates" in (pending)

  it must "be able to project estimates based on differing input sizes" in (pending)

  it must "be able to change the way estimates are computed" in (pending)

  it must "be able to determine input size for estimatations" in (pending)

  it must "be able to report progress through an operation" in (pending)
}
