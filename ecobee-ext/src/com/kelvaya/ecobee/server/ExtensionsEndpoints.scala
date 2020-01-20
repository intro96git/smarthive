package com.kelvaya.ecobee.server

import io.finch._

import zio.{Task,UIO,ZIO}
import zio.interop.catz._
import com.kelvaya.ecobee.client.AccountID
import com.kelvaya.ecobee.server.ClientError._


/** [[Endpoint]] factory for the [[ExtensionsServer]] */
object ExtensionsEndpoints {

  /** Returns the top-level `Endpoint` containing all endpoints exposed by the server */
  def root(implicit z : ServerRuntime) = (new ExtensionsEndpionts).Root
}

/** [[Endpoint]] factory for the [[ExtensionsServer]]
  *
  * @note Instantiate with [[ExtensionsEndpoints$#root]] 
  */
private final class ExtensionsEndpionts(implicit z : ServerRuntime) extends Endpoint.Module[Task] {
  private val TestAccount = new AccountID("example")
  private val Register    = get("register")  
  private val Validate    = get("validate") 
  private val Thermostats = get("thermostats")

  lazy val Root = 
    for {
      client      <-  zio.ZIO.access[ApiClient](_.apiClient)
      register    =   _register(client, TestAccount)  
      validate    =   Ok("validate")
      thermostats =   Ok("therm")
    } yield {
      Thermostats(thermostats) :+: 
      Register(register) :+: 
      Validate(validate)
    }


  // ########################################################
  // ########################################################

  private def run[A](out : ZIO[ServerEnv,Nothing,A]) = z.unsafeRun(out) 

  private def _register(client : ApiClient.Service[Any], account : AccountID) = { () =>
    val apiCall = 
      client
        .register(account)
        .map { reg => Ok(reg) }
        .catchAll[Any, Nothing, Output[Registration]] {
          case ConfigurationError   => UIO.succeed(ServiceUnavailable(ConfigurationError))
          case ThermostatNotFound   => UIO.succeed(NotFound(ThermostatNotFound))
          case ApiServiceError(err) => UIO.succeed(ServiceUnavailable(err)) 
        }

    run(apiCall)
  }
}