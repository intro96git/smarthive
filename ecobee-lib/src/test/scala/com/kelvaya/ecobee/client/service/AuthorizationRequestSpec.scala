package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.client._
import com.kelvaya.ecobee.client.TokenType
import com.kelvaya.ecobee.config.Settings
import com.kelvaya.ecobee.test.BaseTestSpec

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.HttpMethods
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.Uri
import spray.json._
import spray.json.AdditionalFormats
import spray.json.DefaultJsonProtocol


class AuthorizationRequestSpec extends BaseTestSpec
with SprayJsonSupport
with DefaultJsonProtocol
with AdditionalFormats {

  implicit lazy val settings = this.injector.instance[Settings]
  implicit lazy val exec = this.createExecutor(Map.empty)
  implicit lazy val client = new Client

  "Services" must "include support for registering a new application PIN" in {
    val pinReq = new PinRequest()
    PinService.execute(pinReq)

    val req: HttpRequest = pinReq.createRequest
    req.method shouldBe HttpMethods.GET
    req.entity shouldBe HttpEntity.Empty
    req.uri.path shouldBe Uri.Path("/authorize")
    req.uri.query() should contain theSameElementsAs(Seq(("response_type","ecobeePin"),("client_id",this.ClientId),("scope","smartWrite")))

    val TestResponse = PinResponse(Pin, PinExpiration, AuthCode, PinScope.SmartWrite, PinInterval)
    TestResponse.toJson shouldBe s"""{
      "ecobeePin": "${Pin}",
      "code": "${AuthCode}",
      "scope": "smartWrite",
      "expires_in": ${PinExpiration},
      "interval": ${PinInterval}
      }""".parseJson
  }


  they must "include support for getting a new set of tokens using the application PIN" in {
    val initTokenReq = new InitialTokensRequest()
    InitialTokensService.execute(initTokenReq)

    val req: HttpRequest = initTokenReq.createRequest
    req.method shouldBe HttpMethods.POST
    req.entity shouldBe HttpEntity.Empty
    req.uri.path shouldBe Uri.Path("/token")

    val TestResponse = InitialTokensResponse(AccessToken, TokenType.Bearer, TokenExpiration, RefreshToken, PinScope.SmartWrite)
    TestResponse.toJson shouldBe s"""{
      "access_token": "${AccessToken}",
      "token_type": "Bearer",
      "expires_in": ${TokenExpiration},
      "refresh_token": "${RefreshToken}",
      "scope": "smartWrite"
      }""".parseJson
  }


  they must "include support for getting a new access token using the refresh token" in (pending)
}
