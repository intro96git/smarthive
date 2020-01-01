package com.kelvaya.ecobee.test.client

import com.kelvaya.ecobee.client.AccountID
import com.kelvaya.ecobee.client.TestClient
import com.kelvaya.ecobee.client.DI
import com.kelvaya.ecobee.client.ClientSettings

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.scalatest.OptionValues
import org.scalatest.compatible.Assertion

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.HttpRequest

import spray.json._
import spray.json.AdditionalFormats
import spray.json.DefaultJsonProtocol

import zio.DefaultRuntime
import zio.Task

trait BaseTestSpec extends FlatSpec
with Matchers
with OptionValues
with TestConstants
with SprayJsonSupport
with DefaultJsonProtocol
with AdditionalFormats {

  /** Creates default test dependencies.  Override to do something different */
  val deps : DI = BaseTestSpec.createDependencies()

  /** General [[Account]] that can be used by any tests */
  val account = BaseTestSpec.DefaultAccount
  
  /** ZIO Runtime to use when evaluating effectful operations */
  val runtime = new DefaultRuntime { }

  /** Creates a [[TestStorage]] for use in testing */
  def createStorage() = TestStorage(account)
}

object BaseTestSpec {

  final val DefaultAccount = new AccountID("test")

  /** Setup system dependencies.  Defaults to using `TestSettings` and `TestClient` */
  def createDependencies(deps: DI.Dependencies = DI.Dependencies(ActorSystem("ecobee-lib-test-suite"),settings=Some(TestSettings))) = {
    val newDeps = deps.copy(executor=deps.executor.orElse(Some(new TestClient)))
    DI(newDeps)
  }

  def createRequestMap(mapping : Map[HttpRequest, String])(implicit settings : ClientSettings) : Map[HttpRequest, JsObject] = {
    mapping.map {
      case (k,v) =>  (k.withUri(settings.EcobeeServerRoot),v.parseJson.asJsObject)
    }
  }
}


trait ZioTest extends BaseTestSpec {
  import scala.language.implicitConversions
  
  implicit def toZio(a : => Assertion) = Task(a)
  
  final def run(t : Task[Assertion]) = this.runtime.unsafeRun(t.either).fold(
        e => throw e,
        s => s
      )      
}
