package com.kelvaya.ecobee.test

import com.kelvaya.util.Identity
import com.kelvaya.ecobee.client.TestExecutor
import com.kelvaya.util.Realizer
import com.kelvaya.ecobee.client.RequestExecutor

import scala.concurrent.Await
import scala.concurrent.Future
import scala.language.higherKinds

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.scalatest.OptionValues

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.RequestEntity
import akka.http.scaladsl.model.ResponseEntity
import net.codingwell.scalaguice.InjectorExtensions._
import spray.json._
import spray.json.AdditionalFormats
import spray.json.DefaultJsonProtocol
import akka.http.scaladsl.model.HttpRequest
import com.kelvaya.ecobee.config.Settings

trait BaseTestSpec extends FlatSpec
with Matchers
with OptionValues
with TestConstants
with SprayJsonSupport
with DefaultJsonProtocol
with AdditionalFormats {

  implicit val actorSys = ActorSystem("ecobee-lib-test-suite")
  implicit val injector: ScalaInjector = TestDependencies.injector

  implicit object TestRealizer extends Realizer[Identity] {
    def realize[S](v : Identity[S]) : S = v.get
    def pure[S](v: S): Identity[S] = new Identity(v)
  }


//  def realize[T[_],S](v : T[S])(implicit r : Realizer[T]) = r.realize(v)

  def createTestExecutor(reqResp : Map[HttpRequest,JsObject])(implicit settings : Settings) : RequestExecutor = new TestExecutor(reqResp)


  def createRequestMap(mapping : Map[HttpRequest, String])(implicit settings : Settings) : Map[HttpRequest, JsObject] = {
    mapping.map {
      case (k,v) =>  (k.withUri(settings.EcobeeServerRoot),v.parseJson.asJsObject)
    }
  }

}