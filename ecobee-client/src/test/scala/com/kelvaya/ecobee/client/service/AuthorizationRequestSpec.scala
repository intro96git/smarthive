package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.client._
import com.kelvaya.ecobee.client.TokenType
import com.kelvaya.ecobee.client.tokens.TokenStorage
import com.kelvaya.ecobee.test.client.BaseTestSpec

import com.twitter.finagle.http.{Request => HttpRequest}
import com.twitter.finagle.http.{Uri => HttpUri}
import com.twitter.finagle.http.{Status => HttpStatus}

import spray.json._
import com.twitter.finagle.http.Method
import com.twitter.finagle.http.Message

class AuthorizationRequestSpec extends BaseTestSpec {


  import deps.Implicits._


  val store : TokenStorage = this.createStorage()

  "Services" must "include support for registering a new application PIN" in {
    val pinReq = new PinRequest
    "PinService.execute(pinReq)" should compile

    // Confirm generated HTTP request is validly structured
    val req: HttpRequest = this.runtime.unsafeRun(pinReq.createRequest.provide(store))
    req.method shouldBe Method.Get
    req.contentType shouldBe Some(Message.ContentTypeJson)
    req.contentString shouldBe ""
    req.uri should startWith("/authorize?")
    HttpUri.fromRequest(req).params should contain theSameElementsAs(Seq(("format","json"),("response_type","ecobeePin"),("client_id",this.ClientId),("scope","smartWrite")))
    
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
    req.method shouldBe Method.Post
    req.contentType shouldBe Some(Message.ContentTypeJson)
    req.contentString shouldBe ""
    req.uri should startWith("/token?")
    HttpUri.fromRequest(req).params should contain theSameElementsAs(Seq(("format","json"),("grant_type","ecobeePin"),("code",this.AuthCode),("client_id",this.ClientId)))

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
    req.method shouldBe Method.Post
    req.contentType shouldBe Some(Message.ContentTypeJson)
    req.contentString shouldBe ""
    req.uri should startWith("/token?")
    HttpUri.fromRequest(req).params should contain theSameElementsAs(Seq(("format","json"),("grant_type","refresh_token"),("refresh_token",this.RefreshToken),("client_id",this.ClientId)))

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
    "access_denied" -> HttpStatus.Found,
    "invalid_request" -> HttpStatus.BadRequest,
    "invalid_client"  -> HttpStatus.Unauthorized,
    "invalid_grant" -> HttpStatus.BadRequest,
    "unauthorized_client"-> HttpStatus.BadRequest,
    "unsupported_grant_type" -> HttpStatus.BadRequest,
    "invalid_scope" -> HttpStatus.BadRequest,
    "not_supported" -> HttpStatus.BadRequest,
    "account_locked" -> HttpStatus.Unauthorized,
    "account_disabled"-> HttpStatus.Unauthorized,
    "authorization_pending"-> HttpStatus.Unauthorized,
    "slow_down"-> HttpStatus.Unauthorized
    ).map {
      case (e,s) => AuthError(e, "", "").statusCode shouldBe s
    }

    val bad = AuthError("bad_error", "", "")
    intercept[NoSuchElementException] {
      bad.statusCode
    }
  }
}
