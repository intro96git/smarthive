package com.kelvaya.ecobee.client

import spray.json.JsObject
import spray.json.JsonFormat

import zio.ZIO

import com.twitter.finagle.http.{Request => HttpRequest}
import com.twitter.finagle.http.{Response => HttpResponse}


/** Module to send HTTP requests to the Ecobee API */
trait RequestExecutor {
  val requestExecutor : RequestExecutor.Service[Any]
}

/** Service definition for [[RequestExecutor]] module */
object RequestExecutor {

  trait Service[R] {
    /** Return the results of executing an HTTP request.
      *
      * @define S S
      * @define E ServiceError
      *
      * @param req The HTTP Request to execute
      * @param err The function to run when the Response is an error response
      * @param fail The function to run when an unhandled exception is thrown during request execution
      *
      * @tparam S The return payload type (must be in the typeclass of `JsonFormat` used to deserialize it)
      * @tparam E The return payload type for the error
      *
      */
    def executeRequest[E<:ServiceError,S:JsonFormat](
      req: HttpRequest,
      err: JsObject => E, 
      fail: (Throwable,Option[HttpResponse]) => E) : ZIO[R,E,S]
  }

  object > extends Service[RequestExecutor] {
    def executeRequest[E<:ServiceError,S:JsonFormat](req: HttpRequest, err: JsObject => E, fail: (Throwable, Option[HttpResponse]) => E): ZIO[RequestExecutor,E,S] = {
      ZIO.accessM[RequestExecutor](_.requestExecutor.executeRequest[E,S](req,err,fail))
    }
  }
}
