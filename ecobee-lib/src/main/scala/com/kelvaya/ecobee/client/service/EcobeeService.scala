package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.client.Request
import com.kelvaya.ecobee.client.RequestExecutor

import scala.language.higherKinds
import spray.json.JsonFormat

import cats.Monad


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
  * @tparam F The monad type containing the request (from the chosen TokenStorage instance)
  * @tparam M The container type that will hold the response (from dependency injection, `DI`)
  *
  * @define T T
  * @define S S
  */
abstract class EcobeeService[F[_] : Monad, M[_], T <: Request[F,_], S] {

  /** Execute the given request, returning either a [[ServiceError]] or a response of type $S.
    *
    * Will return a [[ServiceError]] if the Ecobee API request fails.
    *
    * @param req The $T used to query the API
    * @param exec (implicit) The executor responsible for sending the request to the Ecobee API  (from dependency injection, `DI`)
    */
  def execute(req: T)(implicit exec : RequestExecutor[F,M]) : M[Either[ServiceError,S]]
}

// ---------------------


/** JSON Web service supported by the Ecobee Thermostat API
  *
  * @tparam T The `Request` used to query the API
  * @tparam S The JSON response type from the API
  * @tparam F The monad type containing the request (from the chosen TokenStorage instance)
  * @tparam M The monad container type that will hold the response  (from dependency injection, `DI`)
  *
  * @define T T
  * @define S JSON response
  */
abstract class EcobeeJsonService[F[_] : Monad, M[_], T <: Request[F,_], S : JsonFormat] extends EcobeeService[F,M,T,S] {
  final def execute(req: T)(implicit exec : RequestExecutor[F,M]) : M[Either[ServiceError,S]] = exec.executeRequest(req.createRequest)
}
