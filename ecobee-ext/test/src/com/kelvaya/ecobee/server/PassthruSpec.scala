package com.kelvaya.ecobee.server

import com.twitter.finagle.http.Request
import com.twitter.finagle.http.Response

import org.scalamock.scalatest.MockFactory
import org.scalatest.compatible.Assertion

import com.kelvaya.ecobee.client._
import com.kelvaya.ecobee.client.service.InitialTokensRequest
import com.kelvaya.ecobee.client.service.PinRequest
import com.kelvaya.ecobee.client.service.PinResponse
import com.kelvaya.ecobee.client.service.Select
import com.kelvaya.ecobee.client.service.SelectType
import com.kelvaya.ecobee.client.service.ThermostatRequest
import com.kelvaya.ecobee.client.service.ThermostatResponse
import com.kelvaya.ecobee.client.service.TokensResponse
import com.kelvaya.ecobee.client.tokens.Tokens
import com.kelvaya.ecobee.client.tokens.TokenStorageError

import com.kelvaya.ecobee.client.tokens.TokenStorage
import com.kelvaya.ecobee.test.server._

import spray.json.JsObject
import spray.json.JsonFormat

import zio._
import zio.clock.Clock
import zio.blocking.Blocking
import zio.console.{Console => ZConsole}
import zio.system.System
import zio.random.{Random => ZRandom}

import scala.util.Random

class PassthruSpec extends ZioServerTestSpec with MockFactory { spec =>

  def runWithMock[R >: ServerEnv](ts : Option[TokenStorage.Service[Any]] = None)(test : ZIO[R,Throwable,Assertion]) = {

    val rt = this.runtime.map { e =>
      new RequestExecutor with TokenStorage with ServerSettings with Clock with ZConsole with System with ZRandom with Blocking {
        val blocking = e.blocking
        val clock = e.clock
        val console = e.console
        val random = e.random
        val settings = e.settings
        val system = e.system
        val requestExecutor = mockRequestExec
        val tokenStorage = ts.getOrElse(spec.runtime.environment.tokenStorage)
      }
    }
    
    rt.unsafeRun(test)
  }

  val mockRequestExec = mock[RequestExecutor.Service[Any]]
  
  def setupMockRequestExecExpectations[E<:ServiceError,S] = 
    toMockFunction4(mockRequestExec.executeRequest[E,S](_ : Request,_ : JsObject=>E,_ : (Throwable, Option[Response]) => E)(_ : JsonFormat[S]))

  def requestMatcher[E<:ServiceError,S](expected : Request) = where {(req:Request,_ : JsObject=>E,_ : (Throwable, Option[Response]) => E, _ : JsonFormat[S]) => 
    req.uri == expected.uri && req.method == expected.method && req.params == expected.params && req.content == expected.content && req.headerMap == expected.headerMap
  }


  // #########################################################
  // #########################################################


  "The Extension server" must "support reading a thermostat's temperature" in {
    val expectedReq = ThermostatRequest(Account, Select(SelectType.Thermostats("testtherm"),includeRuntime = true))
    val temp = Random.nextInt()
    val rt = thermRuntime(rawTemperature = temp)
    val mockResult = thermResponse(therm("testtherm", name="testtherm", runtime = Some(rt)))
    val emptyMockResult = thermResponse()

    runWithMock() { 
      for {
        expectedHttp <- expectedReq.createRequest
        _            =  setupMockRequestExecExpectations[ApiError,ThermostatResponse].expects(requestMatcher(expectedHttp)).returning(zio.UIO(mockResult))
        _            =  setupMockRequestExecExpectations[ApiError,ThermostatResponse].expects(requestMatcher(expectedHttp)).returning(zio.IO.fail(ApiError(Statuses.NotAuthorized)))
        _            =  setupMockRequestExecExpectations[ApiError,ThermostatResponse].expects(requestMatcher(expectedHttp)).returning(zio.UIO(emptyMockResult))

        d            <- ApiClient.Live.readThermostat(Account, new ThermostatID("testtherm"))
        _            <- d shouldBe ThermostatStats("testtherm", Temperature(temp))
        
        e1           <- ApiClient.Live.readThermostat(Account, new ThermostatID("testtherm")).flip.mapError(_ => fail("Should have failed with 'NotAuthorized'"))
        _            <- e1 shouldBe ClientError.ApiServiceError(ApiError(Statuses.NotAuthorized))
        
        e2           <- ApiClient.Live.readThermostat(Account, new ThermostatID("testtherm")).flip.mapError(_ => fail("Should have failed with no results"))
        _            <- e2 shouldBe ClientError.ThermostatNotFound
      } yield succeed
    }
  }

  
  it must "support reading all of an account's thermostats simultaneously" in {
    val expectedReq = ThermostatRequest(Account, Select(SelectType.Registered, includeRuntime = true))
    val (temp1,temp2) = (Random.nextInt(),Random.nextInt())
    val (rt1,rt2) = (thermRuntime(rawTemperature = temp1),thermRuntime(rawTemperature = temp2))
    val mockResult = thermResponse(therm("testtherm", name="testtherm", runtime = Some(rt1)), therm("testtherm2", name="testtherm2", runtime = Some(rt2)))
    val emptyMockResult = thermResponse()

    runWithMock() {
      for {
        expectedHttp <- expectedReq.createRequest
        _            =  setupMockRequestExecExpectations[ApiError,ThermostatResponse].expects(requestMatcher(expectedHttp)).returning(zio.UIO(mockResult))
        _            =  setupMockRequestExecExpectations[ApiError,ThermostatResponse].expects(requestMatcher(expectedHttp)).returning(zio.IO.fail(ApiError(Statuses.NotAuthorized)))
        _            =  setupMockRequestExecExpectations[ApiError,ThermostatResponse].expects(requestMatcher(expectedHttp)).returning(zio.UIO(emptyMockResult))

        d1           <- ApiClient.Live.readThermostats(Account)
        _            <- d1 should contain theSameElementsAs Seq(ThermostatStats("testtherm2", Temperature(temp2)),ThermostatStats("testtherm", Temperature(temp1)))
        
        e1           <- ApiClient.Live.readThermostats(Account).flip.mapError(_ => fail("Should have failed with 'NotAuthorized'"))
        _            <- e1 shouldBe ClientError.ApiServiceError(ApiError(Statuses.NotAuthorized))
        
        d2           <- ApiClient.Live.readThermostats(Account)
        _            <- d2 shouldBe empty
      } yield succeed
    }
  }


  it must "support registering via PIN" in {
    val newAccount = new AccountID("NewPinRequestAcct")
    val expectedReq = new PinRequest()
    val mockResult = PinResponse(Pin, 9, AuthCode, PinScope.SmartWrite, 5)
    val mockErr = AuthError(AuthError.ErrorCodes.SlowDown.error, "err", "https://example.org")
    val store = mock[TokenStorage.Service[Any]]

    runWithMock(Some(store)) {
      for {
        expectedHttp <- expectedReq.createRequest
        _            =  setupMockRequestExecExpectations[AuthError,PinResponse].expects(requestMatcher(expectedHttp)).returning(zio.UIO(mockResult))
        _            =  setupMockRequestExecExpectations[AuthError,PinResponse].expects(requestMatcher(expectedHttp)).returning(zio.IO.fail(mockErr))
        _            =  (store.storeTokens _).expects(newAccount, Tokens(Some(AuthCode), None, None)).returning(zio.UIO.unit)

        d1           <- ApiClient.Live.register(newAccount)
        _            <- d1 shouldBe Registration(Pin, 9, 5)
        
        e1           <- ApiClient.Live.register(newAccount).flip.mapError(_ => fail("Should have failed with 'SlowDown'"))
        _            <- e1 shouldBe ClientError.ApiServiceError(mockErr)
      } yield succeed
    }
  }


  it must "support using the PIN to complete registration" in {
    val PinTestAccount = new AccountID("pinTestAccount")
    val expectedReq = new InitialTokensRequest(PinTestAccount)
    val expectedResp = zio.UIO.succeed(TokensResponse("12345", TokenType.Bearer, 60, "ABCDEF", PinScope.SmartWrite))
    val mockErr = AuthError(AuthError.ErrorCodes.AuthorizationExpired.error, "err", "https://example.org")
    val store = mock[TokenStorage.Service[Any]]

    runWithMock(Some(store)) {

      (store.getTokens _).expects(PinTestAccount).returning(zio.UIO.succeed(Tokens(Some(AuthCode), None, None)))

      for {
        expectedHttp <- expectedReq.createRequest
      
        _            =  (store.getTokens _).expects(PinTestAccount).returning(zio.IO.fail(TokenStorageError.InvalidAccountError)).twice

        e0           <- ApiClient.Live.authorize(PinTestAccount).flip.mapError(e => fail(s"Should have failed with 'InvalidAccount'.  Actual: $e"))
        _            <- e0 shouldBe ClientError.ApiServiceError(RequestError.TokenAccessError(TokenStorageError.InvalidAccountError))

        _            =  inSequence {
                          (store.getTokens _).expects(PinTestAccount).returning(zio.UIO.succeed(Tokens(Some(AuthCode), None, None))).twice
                          setupMockRequestExecExpectations[AuthError,TokensResponse].expects(requestMatcher(expectedHttp)).returning(expectedResp)
                          (store.storeTokens _).expects(PinTestAccount, Tokens(None, Some("12345"), Some("ABCDEF"))).returning(zio.UIO.unit)
                        }


        d1           <- ApiClient.Live.authorize(PinTestAccount)
        _            <- d1 shouldBe AuthStatus.Succeeded(60)

        _            =  (store.getTokens _).expects(PinTestAccount).returning(zio.UIO.succeed(Tokens(None, Some(AccessToken), Some(RefreshToken))))

        d2           <- ApiClient.Live.authorize(PinTestAccount)
        _            <- d2 shouldBe AuthStatus.AlreadyAuthorized
                
        _            =  inSequence {
                          (store.getTokens _).expects(PinTestAccount).returning(zio.UIO.succeed(Tokens(Some(AuthCode), None, None))).twice
                          setupMockRequestExecExpectations[AuthError,TokensResponse].expects(requestMatcher(expectedHttp)).returning(zio.IO.fail(mockErr))
                        }

        d3           <- ApiClient.Live.authorize(PinTestAccount)
        _            <- d3 shouldBe AuthStatus.RegistrationExpired
      } yield succeed
    }
  }


  it must "support refreshing tokens automatically" in (pending)


  it must "support reading a thermostat's humidity" in (pending)
  it must "support reading an account's exterior temperature, humidity, wind chill, heat index, and weather" in (pending)
  it must "support turning on or off a thermostat's heating and cooling" in (pending)
  it must "support changing the temperature setting" in (pending)
  it must "support changing the home/away mode" in (pending)
  it must "support setting or resetting the vacation mode with its timing" in (pending)
  it must "support showing all of a thermostat's sensor readings" in (pending)
  it must "not call the Thermostat API unless data has changed" in (pending)
}
