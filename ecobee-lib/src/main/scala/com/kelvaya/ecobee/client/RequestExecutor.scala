package com.kelvaya.ecobee.client

import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.HttpResponse

import spray.json.JsonFormat

import zio.IO
import spray.json.JsObject


/** Executes HTTP requests to the Ecobee API */
trait RequestExecutor {

  /** Return the results of executing an HTTP request.
    *
    * @define S S
    * @define E ServiceError
    *
    * @param req The HTTP Request to execute
    * @param err The function to run when the HttpResponse is an error response
    * @param fail The function to run when an unhandled exception is thrown during request execution
    *
    * @tparam S The return payload type (must be in the typeclass of `JsonFormat` used to deserialize it)
    * @tparam E The return payload type for the error
    *
    */
  def executeRequest[S:JsonFormat,E<:ServiceError](
    req: HttpRequest, 
    err: JsObject => E, 
    fail: (Throwable,Option[HttpResponse]) => E) : IO[E,S]
}
