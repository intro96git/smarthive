package com.kelvaya.ecobee.client

import com.kelvaya.ecobee.client.service.ServiceError
import com.kelvaya.ecobee.client.tokens.TokenStorage

import akka.http.scaladsl.model.HttpRequest

import spray.json.JsonFormat

import zio.IO
import zio.ZIO


/** Executes HTTP requests to the Ecobee API */
trait RequestExecutor {

  /** Return the results of executing an HTTP request.
    *
    * @define S S
    *
    * @param req The HTTP Request to execute
    *
    * @tparam S The return payload type (must be in the typeclass of `JsonFormat` used to deserialize it)
    *
    */
  def executeRequest[S : JsonFormat](req : ZIO[TokenStorage,RequestError,HttpRequest]) : IO[ServiceError,S]
}
