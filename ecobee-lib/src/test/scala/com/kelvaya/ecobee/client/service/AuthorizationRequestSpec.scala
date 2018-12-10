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
import akka.http.scaladsl.model.StatusCodes


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
    req.uri.query() should contain theSameElementsAs(Seq(("grant_type","ecobeePin"),("code",this.AuthCode),("client_id",this.ClientId)))

    val TestResponse = TokensResponse(InitAccessToken, TokenType.Bearer, InitTokenExpiration, InitRefreshToken, PinScope.SmartWrite)
    TestResponse.toJson shouldBe s"""{
      "access_token": "${InitAccessToken}",
      "token_type": "Bearer",
      "expires_in": ${InitTokenExpiration},
      "refresh_token": "${InitRefreshToken}",
      "scope": "smartWrite"
      }""".parseJson
  }


  they must "include support for getting a new access token using the refresh token" in {
    val tokenReq = new RefreshTokensRequest()
    RefreshTokensService.execute(tokenReq)

    val req: HttpRequest = tokenReq.createRequest
    req.method shouldBe HttpMethods.POST
    req.entity shouldBe HttpEntity.Empty
    req.uri.path shouldBe Uri.Path("/token")
    req.uri.query() should contain theSameElementsAs(Seq(("grant_type","refresh_token"),("refresh_token",this.RefreshToken),("client_id",this.ClientId)))

    val TestResponse = TokensResponse(AccessToken, TokenType.Bearer, TokenExpiration, RefreshToken, PinScope.SmartWrite)
    TestResponse.toJson shouldBe s"""{
      "access_token": "${AccessToken}",
      "token_type": "Bearer",
      "expires_in": ${TokenExpiration},
      "refresh_token": "${RefreshToken}",
      "scope": "smartWrite"
      }""".parseJson
  }


  they must "handle error statuses" in {
    val TestError = ServiceError("not_supported", "test error message", "http://example.org/testme")
    TestError.toJson shouldBe """{
      "error" : "not_supported",
      "error_description" : "test error message",
      "error_uri" : "http://example.org/testme"
      }""".parseJson

    Map(
    "access_denied" -> StatusCodes.Found,
    "invalid_request" -> StatusCodes.BadRequest,
    "invalid_client"  -> StatusCodes.Unauthorized,
    "invalid_grant" -> StatusCodes.BadRequest,
    "unauthorized_client"-> StatusCodes.BadRequest,
    "unsupported_grant_type" -> StatusCodes.BadRequest,
    "invalid_scope" -> StatusCodes.BadRequest,
    "not_supported" -> StatusCodes.BadRequest,
    "account_locked" -> StatusCodes.Unauthorized,
    "account_disabled"-> StatusCodes.Unauthorized,
    "authorization_pending"-> StatusCodes.Unauthorized,
    "slow_down"-> StatusCodes.Unauthorized
    ).map {
      case (e,s) => ServiceError(e, "", "").statusCode shouldBe s
    }

    val bad = ServiceError("bad_error", "", "")
    intercept[NoSuchElementException] {
      bad.statusCode
    }
  }
}
