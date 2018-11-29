package com.kelvaya.ecobee.test

import org.scalatest.OptionValues
import org.scalatest.Matchers
import org.scalatest.FlatSpec
import akka.actor.ActorSystem
import net.codingwell.scalaguice.InjectorExtensions._

trait BaseTestSpec extends FlatSpec with Matchers with OptionValues with TestConstants {
  implicit val actorSys = ActorSystem("ecobee-lib-test-suite")
  implicit val injector: ScalaInjector = TestDependencies.injector
}