package com.kelvaya.ecobee.client

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer

import spray.json._
import spray.json.DefaultJsonProtocol._

import zio.{IO,Task,ZIO}
import akka.event.Logging

/** Executor which calls the Ecobee REST endpoints
  *
  * @param settings (implicit) The application global settings  (from dependency injection, `DI`)
  * @param system (implicit) Akka Actor system for the HTTP service  (from dependency injection, `DI`)
  *
  * @see [[com.kelvaya.ecobee.client client]]
  */
class RequestExecutorImpl(implicit system : ActorSystem) extends RequestExecutor with SprayJsonSupport {

  private implicit val _materializer = ActorMaterializer()
  private lazy val _log = Logging(system, classOf[RequestExecutorImpl])

  def executeRequest[S:JsonFormat,E<:ServiceError](request: HttpRequest, err: JsObject => E, fail: (Throwable,Option[HttpResponse]) => E) : IO[E,S] = {    
    val reqExec = Task.fromFuture { _ => 
      _log.info(s"Connecting to ${request.uri}")
      Http().singleRequest(request) 
    }
    
    val response : IO[E,S] = 
      reqExec
        .map { r => _log.info(s"Response to ${request.uri} completed with status ${r.status}"); r }
        .foldM(
          e  => ZIO.fail(fail(e,None)),
          hr => hr match {
            case r @ HttpResponse(StatusCodes.OK, _, _, _) => 
              Task.fromFuture(implicit ec => Unmarshal(r.entity).to[JsObject])
                .map(_.convertTo[S])
                .mapError(e => fail(e, Some(r))) : IO[E,S]
            case r =>
              Task.fromFuture(implicit ec => Unmarshal(r.entity).to[JsObject])
                .map(err)
                .mapError(e => fail(e, Some(r)))
                .flatMap(r => ZIO.fail(r)) : IO[E,S]
          }
    )
    
    response
  }
}