package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.client._
import com.kelvaya.ecobee.client.TokenType
import com.kelvaya.ecobee.client.tokens.TokenStorage
import com.kelvaya.ecobee.test.BaseTestSpec

import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.HttpMethods
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.Uri

import spray.json._

class AuthorizationRequestSpec extends BaseTestSpec {


  import deps.Implicits._


  val store : TokenStorage = this.createStorage()

  "Services" must "include support for registering a new application PIN" in {
    val pinReq = new PinRequest(account)
    "PinService.execute(pinReq)" should compile

    // Confirm generated HTTP request is validly structured
    val req: HttpRequest = this.runtime.unsafeRun(pinReq.createRequest.provide(store))
    req.method shouldBe HttpMethods.GET
    req.entity shouldBe HttpEntity.empty(Request.ContentTypeJson)
    req.uri.path shouldBe Uri.Path("/authorize")
    req.uri.query() should contain theSameElementsAs(Seq(("format","json"),("response_type","ecobeePin"),("client_id",this.ClientId),("scope","smartWrite")))

    // Confirm expected response is validly structured
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
    val initTokenReq = new InitialTokensRequest(account)
    "InitialTokensService.execute(initTokenReq)" should compile
    "InitialTokensService.execute(account)" should compile

    // Confirm generated HTTP request is validly structured
    val req: HttpRequest = this.runtime.unsafeRun(initTokenReq.createRequest.provide(store))
    req.method shouldBe HttpMethods.POST
    req.entity shouldBe HttpEntity.empty(Request.ContentTypeJson)
    req.uri.path shouldBe Uri.Path("/token")
    req.uri.query() should contain theSameElementsAs(Seq(("format","json"),("grant_type","ecobeePin"),("code",this.AuthCode),("client_id",this.ClientId)))

    // Confirm expected response is validly structured
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
    val tokenReq = new RefreshTokensRequest(account)
    "RefreshTokensService.execute(tokenReq)" should compile

    // Confirm generated HTTP request is validly structured
    val req: HttpRequest = this.runtime.unsafeRun(tokenReq.createRequest.provide(store))
    req.method shouldBe HttpMethods.POST
    req.entity shouldBe HttpEntity.empty(Request.ContentTypeJson)
    req.uri.path shouldBe Uri.Path("/token")
    req.uri.query() should contain theSameElementsAs(Seq(("format","json"),("grant_type","refresh_token"),("refresh_token",this.RefreshToken),("client_id",this.ClientId)))

    // Confirm expected response is validly structured
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
    val TestError = AuthError("not_supported", "test error message", "http://example.org/testme")
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
      case (e,s) => AuthError(e, "", "").statusCode shouldBe s
    }

    val bad = AuthError("bad_error", "", "")
    intercept[NoSuchElementException] {
      bad.statusCode
    }
  }
}
