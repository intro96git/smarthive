package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.client.AuthorizedRequest
import com.kelvaya.ecobee.client.Request
import com.kelvaya.ecobee.client.RequestExecutor
import com.kelvaya.ecobee.client.ApiError
import com.kelvaya.ecobee.client.AuthError
import com.kelvaya.ecobee.client.ServiceError
import com.kelvaya.ecobee.client.Status

import spray.json._

import zio.ZIO

import akka.http.scaladsl.model.HttpResponse



/** Response from the Ecobee API to an HTTP Request
  *
  * Used within an [[EcobeeService]]
  *
  * @tparam T Payload of the API response
  */
trait EcobeeResponse[T]

// ---------------------


/** Web service supported by the Ecobee Thermostat API
  *
  * @tparam T The `Request` used to query the API
  * @tparam S The response type from the API
  *
  * @define T T
  * @define S S
  */
abstract class EcobeeService[T <: Request[_], S] {

  /** Execute the given request, returning either a [[ServiceError]] or a response of type $S.
    *
    * Will return a [[ServiceError]] if the Ecobee API request fails.
    *
    * @param req The $T used to query the API
    */
  def execute(req: T) : ZIO[ClientEnv,ServiceError,S]
}

// ---------------------


/** Helper function for [[EcobeeJsonService]] and [[EcobeeJsonAuthService]] */
object EcobeeJsonService {
  private[service] def exec[T <: Request[_], S : JsonFormat,E <: ServiceError](request: T, err: JsObject => E, fail: (Throwable,Option[HttpResponse]) => E) : ZIO[ClientEnv,ServiceError,S] =     
    for {
      req  <- request.createRequest
      rez  <- ZIO.accessM[RequestExecutor](_.requestExecutor.executeRequest(req, err, fail))
    } yield rez
}

/** JSON Web service supported by the Ecobee Thermostat API for authorized requests
  *
  * @tparam T The `AuthorizedRequest` used to query the API
  * @tparam S The JSON response type from the API
  *
  * @define T T
  * @define S JSON response
  */
abstract class EcobeeJsonService[T <: AuthorizedRequest[_], S : JsonFormat] extends EcobeeService[T,S] {
  final def execute(request: T) : ZIO[ClientEnv,ServiceError,S] = {
    
    val fn : ((Throwable,Option[HttpResponse]) => ApiError) = { (t,rsp) =>
      rsp
        .map { r => ApiError(Status(-1, s"Unexpected response, $r, during API request processing at ${request}: [${t.getClass.getName}] ${t.getMessage()}")) }
        .getOrElse { ApiError(Status(-1, s"Unexpected error during API request processing at ${request}: [${t.getClass.getName}] ${t.getMessage()}")) }
    }
    
    EcobeeJsonService.exec(request, parseErrorResponse, fn)
  }

  private def parseErrorResponse(r : JsObject) : ApiError = r.convertTo[ApiError]
}

/** Authorization endpoints against the Ecobee API
  *
  * @tparam T The `Request` used to query the API
  * @tparam S The JSON response type from the API
  *
  * @define T T
  * @define S JSON response
  */
abstract class EcobeeJsonAuthService[T <: Request[_], S : JsonFormat] extends EcobeeService[T,S] {
  final def execute(request: T) : ZIO[ClientEnv,ServiceError,S] = { 
    
    val fn : ((Throwable,Option[HttpResponse]) => AuthError) = { (t,rsp) => 
      rsp
        .map { r => AuthError(s"Unexpected Authorization Response $r", s"[${t.getClass}] ${t.getMessage}", request.toString) }
        .getOrElse { AuthError("Unexpected Authorization Error", s"[${t.getClass}] ${t.getMessage}", request.toString) } 
    }
    
    EcobeeJsonService.exec(request, parseErrorResponse, fn)
  }

  private def parseErrorResponse(r : JsObject) : AuthError = r.convertTo[AuthError]
}