package com.kelvaya.ecobee.server

import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.HttpResponse

import org.scalatest.compatible.Assertion
import org.scalamock.scalatest.MockFactory

import com.kelvaya.ecobee.client.ApiError
import com.kelvaya.ecobee.client.ClientSettings
import com.kelvaya.ecobee.client.RequestExecutor
import com.kelvaya.ecobee.client.ServiceError
import com.kelvaya.ecobee.client.Statuses
import com.kelvaya.ecobee.client.Temperature
import com.kelvaya.ecobee.client.service.ThermostatRequest
import com.kelvaya.ecobee.client.service.Select
import com.kelvaya.ecobee.client.service.SelectType
import com.kelvaya.ecobee.client.service.ThermostatResponse
import com.kelvaya.ecobee.client.tokens.TokenStorage
import com.kelvaya.ecobee.test.server._

import spray.json.JsObject
import spray.json.JsonFormat

import zio.ZIO

import scala.util.Random

class PassthruSpec extends ZioServerTestSpec with MockFactory { spec =>

  def runWithMock[R >: ServerEnv](re : RequestExecutor.Service[Any])(testFn : ApiClient.Service[Any] => ZIO[R,Throwable,Assertion]) = {
    val testToRun = { 
      val client = new ApiClientImpl { 
        val account = Account
        val env: ClientEnv = new RequestExecutor with TokenStorage with ClientSettings {
          val settings: ClientSettings.Service[Any] = spec.runtime.environment.settings  
          val requestExecutor: RequestExecutor.Service[Any] = re
          val tokenStorage: TokenStorage.Service[Any] = spec.runtime.environment.tokenStorage
        }
      }
      testFn(client.apiClient)
    }
    this.run(testToRun)
  }

  val mockRequestExec = mock[RequestExecutor.Service[Any]]
  
  def setupMockRequestExecExpectations[S,E<:ServiceError] = 
    toMockFunction4(mockRequestExec.executeRequest[S,E](_ : HttpRequest,_ : JsObject=>E,_ : (Throwable, Option[HttpResponse]) => E)(_ : JsonFormat[S]))


  // #########################################################
  // #########################################################


  "The Extension server" must "support reading a thermostat's temperature" in {
    val expectedReq = ThermostatRequest(Account, Select(SelectType.Registered("testtherm"),includeRuntime = true))
    val temp = Random.nextInt()
    val rt = thermRuntime(rawTemperature = temp)
    val mockResult = thermResponse(therm("testtherm", name="testtherm", runtime = Some(rt)))
    val emptyMockResult = thermResponse()

    runWithMock(mockRequestExec) { client =>
      for {
        expectedHttp <- expectedReq.createRequest
        _            =  setupMockRequestExecExpectations[ThermostatResponse,ApiError].expects(expectedHttp,*,*,*).returning(zio.UIO(mockResult))
        _            =  setupMockRequestExecExpectations[ThermostatResponse,ApiError].expects(expectedHttp,*,*,*).returning(zio.IO.fail(ApiError(Statuses.NotAuthorized)))
        _            =  setupMockRequestExecExpectations[ThermostatResponse,ApiError].expects(expectedHttp,*,*,*).returning(zio.UIO(emptyMockResult))

        d            <- client.readThermostat(new ThermostatID("testtherm"))
        _            <- d shouldBe ThermostatStats("testtherm", Temperature(temp))
        
        e1           <- client.readThermostat(new ThermostatID("testtherm")).flip.mapError(_ => fail("Should have failed with 'NotAuthorized'"))
        _            <- e1 shouldBe ClientError.ApiServiceError(ApiError(Statuses.NotAuthorized))
        
        e2           <- client.readThermostat(new ThermostatID("testtherm")).flip.mapError(_ => fail("Should have failed with no results"))
        _            <- e2 shouldBe ClientError.ThermostatNotFound
      } yield succeed
    }
  }

  
  it must "support reading all of an account's thermostats simultaneously" in {
    val expectedReq = ThermostatRequest(Account, Select(SelectType.Thermostats, includeRuntime = true))
    val (temp1,temp2) = (Random.nextInt(),Random.nextInt())
    val (rt1,rt2) = (thermRuntime(rawTemperature = temp1),thermRuntime(rawTemperature = temp2))
    val mockResult = thermResponse(therm("testtherm", name="testtherm", runtime = Some(rt1)), therm("testtherm2", name="testtherm2", runtime = Some(rt2)))
    val emptyMockResult = thermResponse()

    runWithMock(mockRequestExec) { client =>
      for {
        expectedHttp <- expectedReq.createRequest
        _            =  setupMockRequestExecExpectations[ThermostatResponse,ApiError].expects(expectedHttp,*,*,*).returning(zio.UIO(mockResult))
        _            =  setupMockRequestExecExpectations[ThermostatResponse,ApiError].expects(expectedHttp,*,*,*).returning(zio.IO.fail(ApiError(Statuses.NotAuthorized)))
        _            =  setupMockRequestExecExpectations[ThermostatResponse,ApiError].expects(expectedHttp,*,*,*).returning(zio.UIO(emptyMockResult))

        d1           <- client.readThermostats
        _            <- d1 should contain theSameElementsAs Seq(ThermostatStats("testtherm2", Temperature(temp2)),ThermostatStats("testtherm", Temperature(temp1)))
        
        e1           <- client.readThermostats.flip.mapError(_ => fail("Should have failed with 'NotAuthorized'"))
        _            <- e1 shouldBe ClientError.ApiServiceError(ApiError(Statuses.NotAuthorized))
        
        d2           <- client.readThermostats
        _            <- d2 shouldBe empty
      } yield succeed
    }
  }


  it must "support reading a thermostat's humidity" in (pending)
  it must "support reading an account's exterior temperature, humidity, wind chill, heat index, and weather" in (pending)
  it must "support turning on or off a thermostat's heating and cooling" in (pending)
  it must "support changing the temperature setting" in (pending)
  it must "support changing the home/away mode" in (pending)
  it must "support setting or resetting the vacation mode with its timing" in (pending)
  it must "support showing all of a thermostat's sensor readings" in (pending)
  it must "not call the Thermostat API unless data has changed" in (pending)
}
