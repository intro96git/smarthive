package com.kelvaya.ecobee.test

import com.kelvaya.ecobee.client.TestStorage
import com.kelvaya.ecobee.client.TestClient
import com.kelvaya.ecobee.config.Settings

import scala.language.implicitConversions

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.scalatest.OptionValues

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.HttpRequest
import cats.Id
import cats.data.EitherT
import spray.json._
import spray.json.AdditionalFormats
import spray.json.DefaultJsonProtocol
import com.kelvaya.ecobee.config.DI

trait BaseTestSpec extends FlatSpec
with Matchers
with OptionValues
with TestConstants
with SprayJsonSupport
with DefaultJsonProtocol
with AdditionalFormats {

  /** Creates default test dependencies.  Override to do something different */
  val deps : DI[Id] = BaseTestSpec.createDependencies()

  /*
   * Convenience test method to allow a quick conversion from the (frequent) return of an `EitherT` of the Identity type
   * to the underlying value.  It assumes that the Either is always successful (i.e.: a right projection), which is
   * a very safe assumption during unit testing using Identities.
   */
  implicit def assumeIdentityRightEither[A](ioer : EitherT[Id,_,A]) : A = ioer.value.getOrElse(fail("Unexpected error: assumption incorrect!"))

}

object BaseTestSpec {

  /** Setup system dependencies.  Defaults to using [[TestSettings]] */
  def createDependencies(reqResp : Map[HttpRequest,JsObject] = Map.empty, deps: DI.Dependencies[Id] = DI.Dependencies(ActorSystem("ecobee-lib-test-suite"),settings=Some(TestSettings))) = {
    val newDeps = deps.copy(executor=deps.executor.orElse(Some(new TestClient(TestStorage(),reqResp)(deps.settings.get, deps.actorSystem))))
    DI(newDeps)
  }

  def createRequestMap(mapping : Map[HttpRequest, String])(implicit settings : Settings) : Map[HttpRequest, JsObject] = {
    mapping.map {
      case (k,v) =>  (k.withUri(settings.EcobeeServerRoot),v.parseJson.asJsObject)
    }
  }
}
