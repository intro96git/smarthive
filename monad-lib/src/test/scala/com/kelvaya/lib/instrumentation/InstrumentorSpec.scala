package com.kelvaya.lib.instrumentation

import com.kelvaya.lib.instrumentation._

import org.scalatest.FlatSpec
import org.scalatest.Matchers

class InstrumentorSpec extends FlatSpec with Matchers {

  "An instrumentor" must "be implicitly creatable from a simple function" in {
    def testFunction(i : Int) : Boolean = if (i >=0) true else false

    val i1 = implicitly[Instrumentor[Int=>Boolean]]
    i1 shouldBe a [FunctionInstrumentor[_,_]]

    def passFn[A,B](f : A => B)(implicit ev : Instrumentor[A=>B]) = { ev shouldBe a [FunctionInstrumentor[_,_]]}
    passFn(testFunction)

    def passArg[A](f : A)(implicit ev : Instrumentor[A]) = { ev shouldBe a [FunctionInstrumentor[_,_]] }
    passArg(testFunction _)


    val testLambda = { i : Int => (i >= 0) }
    val i2 = implicitly[Instrumentor[Int=>Boolean]]
    passFn(testLambda)
    passArg(testLambda)
  }



  it must "be implicitly creatable from a prober function" in {
    val testProber = { p : Prober => i : Int => (i >= 0) }

    val i1 = implicitly[ProbingInstrumentor[Prober,Int => Boolean]]
    i1 shouldBe a [SimpleProbingInstrumentor[_,_,_]]

    def passFn[A,B](f : Prober => A => B)(implicit ev : ProbingInstrumentor[Prober,A=>B]) = {
      ev shouldBe a [SimpleProbingInstrumentor[_,_,_]]
    }
    passFn(testProber)

    def passArg[A](f : A)(implicit ev : ProbingInstrumentor[Prober,A]) = { ev shouldBe a [SimpleProbingInstrumentor[_,_,_]] }
    passArg(testProber)
  }


  // #################################################


  "An instrumented function" must "be easy-to-create" in {

    val fun = { i : Int => (i>=0) }
    "instrument { fn }" shouldNot compile

    timed { fun }

    val fun2 = { p : Prober => fun }
    instrument { fun2 }
    timed { fun2 }
  }


  it must "be able to define it's own prober" in {

    val fun = { p : TestProber => { i : Int => (i>=0) } }
    "instrument { fun }" shouldNot compile

    implicit val prober = new TestProber(1)
    instrument { fun }
  }


  it must "be able to define it's own instrumentor" in {
  }



  // #################################################


  class TestProber(val flag : Any) extends Prober

}
