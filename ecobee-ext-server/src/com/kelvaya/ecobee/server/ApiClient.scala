package com.kelvaya.ecobee.server

import com.kelvaya.ecobee.client._

import com.kelvaya.ecobee.client.service.ThermostatService
import com.kelvaya.ecobee.client.service.ThermostatService._
import com.kelvaya.ecobee.client.service.Select
import com.kelvaya.ecobee.client.service.SelectType

import zio.ZIO

/** Ecobee API client used by the server 
  * 
  * @see [[ApiClientImpl]]
  */
trait ApiClient {

  /** Ecobee API client used by the server */
  val apiClient : ApiClient.Service[Any]
}

/** Module defining API client used by the server */
object ApiClient {

  /** Ecobee API client used by the server 
    * 
    * @tparam R Environment needed to provide access for this client
    * 
    * @see [[ApiClientImpl]]
    */
  trait Service[R] {

    /** Return the current statistics for the given thermostat 
      *
      * If the thermostat is not found, the implementation should return [[ClientError.ThermostatNotFound]]
      * 
      * @param id The ID of the thermostat 
      */
    def readThermostat(id : ThermostatID) : ZIO[R,ClientError,ThermostatStats]

    /** Return the current statistics for all registered thermostat */
    def readThermostats : ZIO[R,ClientError,Iterable[ThermostatStats]]
  }
}


/** Default implementation of the Ecobee client API, using the Ecobee client library's [[RequestExecutor]] 
  *  to provide access to the thermostat data.
  */ 
trait ApiClientImpl extends ApiClient {
  val loggingBus : akka.event.LoggingBus
  val account : AccountID
  val env : ClientEnv

  private implicit lazy val _logging = loggingBus
  private implicit lazy val _settings = env.settings
  
  val apiClient = new ApiClient.Service[Any] {

    def readThermostat(id : ThermostatID) : ZIO[Any,ClientError,ThermostatStats] = {      
      ThermostatService
        .execute(account, Select(SelectType.Registered(id.id), includeRuntime=true))
        .mapError(ClientError.ApiServiceError)
        .flatMap { res =>
          zio.IO.fromEither {
            val tempOpt = for {
              t <- res.thermostatList.headOption
              r <- t.runtime
            } yield ThermostatStats(t.name, Temperature(r.rawTemperature))

            tempOpt.map(t => Right(t)).getOrElse(Left(ClientError.ThermostatNotFound))
          }
        }
        .provide(env)
    }

    def readThermostats : ZIO[Any,ClientError,Iterable[ThermostatStats]] = {
      ThermostatService
        .execute(account, Select(SelectType.Thermostats, includeRuntime=true))
        .mapError(ClientError.ApiServiceError)
        .map(_.thermostatList.flatMap(t => t.runtime.map(r => ThermostatStats(t.name, Temperature(r.rawTemperature))))) 
        .provide(env)
    }
  }

}
