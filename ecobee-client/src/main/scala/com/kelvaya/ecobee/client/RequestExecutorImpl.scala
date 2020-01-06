package com.kelvaya.ecobee.client

import spray.json._

import com.twitter.finagle.http.{Request => HttpRequest}
import com.twitter.finagle.http.{Response => HttpResponse}
import com.twitter.finagle.http.{Status => HttpStatus}
import com.twitter.finagle.Http

import zio.{IO,Task,ZIO}
import zio.interop.twitter._

import com.typesafe.scalalogging.Logger


/** Executor which calls the Ecobee REST endpoints
  *
  * Use [[RequestExecutorImpl$]] to create an instance.
  * @see [[com.kelvaya.ecobee.client client]]
  */
trait RequestExecutorImpl extends RequestExecutor {

  val settings : ClientSettings.Service[Any]

  private lazy val _log = Logger[RequestExecutorImpl]

  private val _finagle : HttpRequest => zio.Task[HttpResponse] = { req => 
    val service = zio.Managed.make(newService) { svc => zio.UIO { svc.close(); () } }
    service.use[Any,Throwable,HttpResponse] { svc => Task.fromTwitterFuture(IO(svc(req))) }
  }

  private def newService = IO {
    val root = settings.EcobeeServerRoot  
    val base = Http.client
    val client = {
      if (root.getProtocol == "https") base.withTls(root.getHost)
      else base
    }

    client.newService(root.getAuthority)
  }

  final val requestExecutor = new RequestExecutor.Service[Any] {

    def executeRequest[E<:ServiceError,S:JsonFormat](request: HttpRequest, err: JsObject => E, fail: (Throwable,Option[HttpResponse]) => E) : IO[E,S] = {    
      val reqExec = {
        _log.info(s"Connecting to ${request.uri}")
        _finagle(request)
      }
      
      val response : IO[E,S] = 
        reqExec
          .map { r => _log.info(s"Response to ${request.uri} completed with status ${r.status}"); r }
          .foldM(
            e  => ZIO.fail(fail(e,None)),
            hr => hr.status match {
              case HttpStatus.Ok => 
                Task(hr.contentString.parseJson.asJsObject)
                  .map(_.convertTo[S])
                  .mapError(e => fail(e, Some(hr))) : IO[E,S]
              case _ =>
                Task(hr.contentString.parseJson.asJsObject)
                  .map(err)
                  .mapError { e => fail(new RuntimeException(s"Bad response: $hr", e), Some(hr)) }
                  .flatMap { r => 
                    println(s"REQUEST: ${request.headerMap}")
                    ZIO.fail(r)
                   } : IO[E,S]
            }
      )
      
      response
    }
  }
}

/** Factory for [[RequestExecutorImpl]] */
object RequestExecutorImpl {
  
  /** Returns a new [[RequestExecutorImpl]].
    *
    * @note This will require both [[ClientSettings]] to be provide in the environment.
    */ 
  def create : zio.URIO[ClientSettings,RequestExecutorImpl] = zio.ZIO.environment[ClientSettings].map { s =>
    new RequestExecutorImpl { val settings: ClientSettings.Service[Any] = s.settings }
  }
}