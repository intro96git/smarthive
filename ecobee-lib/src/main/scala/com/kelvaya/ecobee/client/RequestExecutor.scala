package com.kelvaya.ecobee.client

import com.kelvaya.ecobee.client.service.ServiceError

import scala.language.higherKinds

import akka.http.scaladsl.model.HttpRequest

import monix.eval.Task

import spray.json.JsonFormat


/** Executes HTTP requests to the Ecobee API */
trait RequestExecutor[F[_],M[_]] {

  /** Return the results of executing an HTTP request.
    *
    * @define S S
    *
    * @note The results will be either an error or the return payload.  This is encapsulated
    * as an `Either` of [[com.kelvaya.ecobee.client.service.ServiceError ServiceError]] or an object of type $S.
    *
    * @usecase def executeRequest[S](req : EitherM[RequestError,Task[HttpRequest]]) : EitherM[ServiceError,S]
    *
    * @param req The HTTP Request to execute
    *
    * @tparam S The return payload type (must be in the typeclass of `JsonFormat` used to deserialize it)
    *
    */
  def executeRequest[S : JsonFormat](req : Task[F[Either[RequestError,HttpRequest]]]) : M[Either[ServiceError,S]]
}
