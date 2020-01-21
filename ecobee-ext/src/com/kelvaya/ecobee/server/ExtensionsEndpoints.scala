package com.kelvaya.ecobee.server

import io.finch._

import zio.{Task,UIO,ZIO}
import zio.interop.catz._
import com.kelvaya.ecobee.client.AccountID
import com.kelvaya.ecobee.server.ClientError._
import com.kelvaya.ecobee.server.AuthStatus._
import com.twitter.finagle.http.Status
import io.circe._


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
  private val RegisterEP    = post("register")  
  private val ValidateEP    = post("validate")
  private val ThermostatsEP = get("thermostats")

  lazy val Root = 
    for {
      client      <-  zio.ZIO.access[ApiClient](_.apiClient)
      register    =   _register(client, TestAccount)  
      validate    =   _validate(client, TestAccount)
      thermostats =   _therm(client, TestAccount)
    } yield {
      ThermostatsEP(thermostats) :+: 
      RegisterEP(register) :+: 
      ValidateEP(validate)
    }


  // ########################################################
  // ########################################################

  private def run[A](call : ZIO[ServerEnv,ClientError,Output[A]]) = { () =>
    val out : ZIO[ServerEnv, Nothing, Output[A]] = call.catchAll {
      case ConfigurationError   => UIO.succeed(ServiceUnavailable(ConfigurationError))
      case ThermostatNotFound   => UIO.succeed(NotFound(ThermostatNotFound))
      case ApiServiceError(err) => UIO.succeed(ServiceUnavailable(err)) 
    }
    z.unsafeRun(out)
  }

  // ########################################################
  // ########################################################

  private def _register(client : ApiClient.Service[Any], account : AccountID) = run {
    client.register(account).map { reg => Ok(reg) }
  }

    
  private def _validate(client: ApiClient.Service[Any], account : AccountID) = run { 
    import io.circe.generic.auto._
    val i = implicitly[Encoder[AuthStatus]]
    val e = implicitly[Encoder[AuthorizationError]]
    
    client.authorize(account).map { 
      case AlreadyAuthorized        =>  NoContent.map(i.apply)
      case WaitingForAuthorization  =>  Accepted.map(i.apply)
      case NeedsAuthorization       =>  Output.payload(e(AuthorizationError.MissingRegistration), Status.Unauthorized)
      case RegistrationExpired      =>  Output.payload(e(AuthorizationError.RegistrationExpired), Status.Unauthorized)
      case s @ Succeeded(_)         =>  Ok(s).map(i.apply)
    }
  }


  private def _therm(client : ApiClient.Service[Any], account : AccountID) = run {
    client.readThermostats(account).map { t => Ok(Thermostats(t)) }
  }
}


// ########################################################
// ########################################################


/** Authorization error (HTTP 401) returned during token authorization against the Ecobee API 
  * 
  * The errors are enumerated in [[AuthorizationError$]]
  */
final case class AuthorizationError private (message : String) extends RuntimeException

/** Enumeration of possible HTTP 401 errors from Ecobee API token authorization */
object AuthorizationError {
  val MissingRegistration = AuthorizationError("Credentials and/or PIN missing.  You must call /register first.")
  val RegistrationExpired = AuthorizationError("You PIN registration has expired.  You must call /register again.")
}
