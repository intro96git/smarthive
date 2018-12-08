package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.test.BaseTestSpec
import com.kelvaya.ecobee.test.TestDependencies

import java.nio.charset.StandardCharsets

import akka.actor.ActorSystem
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.model.headers
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.HttpMethod
import akka.http.scaladsl.model.HttpMethods
import com.kelvaya.ecobee.config.Settings
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.ContentTypes
import com.kelvaya.ecobee.client.RequestExecutor
import com.kelvaya.ecobee.client.Request
import com.kelvaya.ecobee.client._

import spray.json._
import spray.json.AdditionalFormats
import spray.json.DefaultJsonProtocol
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.ResponseEntity
import akka.http.scaladsl.marshalling.Marshal

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future
import scala.concurrent.Await
import akka.http.scaladsl.model.RequestEntity
import akka.http.scaladsl.model.HttpMethods


class AuhorizationRequestSpec extends BaseTestSpec with SprayJsonSupport with DefaultJsonProtocol with AdditionalFormats {

  implicit lazy val settings = this.injector.instance[Settings]
  lazy val ResponseMap = this.createRequestMap(Map(
      HttpRequest(HttpMethods.GET, s"authorize?response_type=ecobeePin&client_id=${ClientId}&scope=smartWrite") -> s"""{
         "ecobeePin": "${Pin}",
         "code": "${AuthCode}",
         "scope": "smartWrite",
         "expires_in": ${PinExpiration},
         "interval": ${PinInterval}
       }"""
    ))


  implicit lazy val exec = this.createExecutor(ResponseMap)
  implicit lazy val client = new Client

  val TestResponse = PinResponse(Pin, PinExpiration, AuthCode, PinScope.SmartWrite, PinInterval)

  "The library" must "support registering a new application PIN" in {
    val pinReq = new PinRequest()
    val req: HttpRequest = pinReq.createRequest

    req.method shouldBe HttpMethods.GET
    req.entity shouldBe HttpEntity.Empty
    req.uri.path shouldBe Settings.PinRequestUri

    val unrealizedPinResponse = PinService.execute(pinReq)
    val pinResponse = this.realize(unrealizedPinResponse)

    pinResponse shouldBe Right(TestResponse)
  }

  it must "support getting a new set of tokens using the application PIN" in (pending)

  it must "support getting a new access token using the refresh token" in (pending)
}
