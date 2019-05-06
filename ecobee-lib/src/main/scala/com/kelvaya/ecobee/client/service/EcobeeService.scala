package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.client.Request
import com.kelvaya.ecobee.client.RequestExecutor
import com.kelvaya.ecobee.config.Settings
import akka.http.scaladsl.model.HttpResponse
import com.kelvaya.util.Realizer

import scala.language.higherKinds
import com.kelvaya.ecobee.client.Client
import spray.json.JsonFormat
import spray.json.DefaultJsonProtocol
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.HttpRequest



object EcobeeResponse {
  implicit object HttpResponse extends EcobeeResponse[HttpResponse]
}

/** Response from the Ecobee API to an HTTP Request
  *
  * Used within an [[EcobeeService]]
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
    * @param req The $T used to query the API
    * @param client (implicit) The API client
    * @tparam R The container object holding the return value (in the `Realizer` type-class)
    */
  def execute[R[_] : Realizer](req: T)(implicit client : Client) : R[Either[ServiceError,S]]
}

// ---------------------


/** JSON Web service supported by the Ecobee Thermostat API
  *
  * @tparam T The `Request` used to query the API
  * @tparam S The JSON response type from the API
  *
  * @define T T
  * @define S S
  */
abstract class EcobeeJsonService[T <: Request[_], S : JsonFormat] extends EcobeeService[T,S] {
  final def execute[R[_] : Realizer](req: T)(implicit client : Client) : R[Either[ServiceError,S]] = client.executeRequest(req.createRequest)
}
