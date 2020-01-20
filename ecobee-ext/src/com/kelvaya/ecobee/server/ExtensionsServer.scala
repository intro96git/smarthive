package com.kelvaya.ecobee.server

import io.finch._
import io.finch.circe._
import io.circe.generic.auto._

import better.files._

import com.twitter.app.Flag
import com.twitter.finagle.Http
import com.twitter.server.TwitterServer
import com.twitter.util.Await

import zio.{IO,Task,UIO,ZIO,ZManaged}
import zio.blocking._
import zio.interop.catz._
import zio.interop.twitter._

import com.twitter.finagle.ListeningServer
import com.typesafe.scalalogging.Logger

import com.kelvaya.ecobee.client.tokens.H2DbTokenStorage

/** Main entry point for the Ecobee Extensions server */
object ExtensionsServer extends TwitterServer with Endpoint.Module[Task] {

  val useFile : Flag[java.io.File] = flag("useFileTokens", "If set, uses a file-based token storage instead of an H2 database.")
  val initDb : Flag[Boolean] = flag("initDb", "If set, will create a new H2 token storage database if none exists.")
    
  // NB: An implicit runtime is necessary for the ZIO Cats implicits to properly work
  // (Just including the interop will not be enough without this extra implicit)
  implicit private lazy val _rt = new ServerRuntime
  
  private lazy val _transactor = {
    implicit val s = _rt.environment.settings 
    if (initDb.isDefined) 
      H2DbTokenStorage.initAndConnect
    else 
      H2DbTokenStorage.connect
  }

  private lazy val _fileToken = useFile.get.map { _.toScala }


  private val _httpServer = {

    val startServer : ZIO[ServerSettings with ApiClient, Throwable, ListeningServer] = ExtensionsEndpoints.root.flatMap { endpoints =>
      val service = Bootstrap.configure().serve[Application.Json](endpoints).toService
      zio.ZIO.access[ServerSettings](s => Http.serve(s.settings.ListenPort, service))
    }
    
    ZManaged.make(startServer){ svr => 
      Task
        .fromTwitterFuture(IO(svr.close()))
        .catchAll { 
          case t => 
            Logger[ExtensionsServer.type].warn(s"Server shutdown was not clean; [${t.getClass.getName}] ${t.getMessage}")
            UIO.unit
        } 
    }
  }
  
  // ############################################################################
  // ############################################################################
  
  
  def main() : Unit = {

    val program = _transactor.use { xa => 
      val runServer = _httpServer.use { svr =>
        for {
          log   <-  UIO(Logger[ExtensionsServer.type])
          _     =   log.info("Starting Extensions Server")
          fib   <-  blocking(IO(Await.ready(this.adminHttpServer))).fork
          _     =   log.info(s"Extensions Server started at ${svr.boundAddress}")
          _     <-  fib.join
          _     =   log.info("Extensions Server received shutdown command")
        } yield 0
      }

      val environment =
        for {
          env     <-  zio.ZIO.environment[ServerEnv]
          clientE <-  _fileToken.map(createEnvUsingFileStore).getOrElse(UIO.succeed(createEnv(UIO(xa))))
          client  <-  ApiClient.Default.newClient.provide(clientE)
          newEnv  =   new ServerSettings with Blocking with ApiClient {
            val apiClient = client.apiClient
            val blocking = env.blocking
            val settings = env.settings
          }
        } yield newEnv

      runServer
        .catchAll {
          case t =>
            Logger[ExtensionsServer.type].error(s"Uncaught error, exiting: [${t.getClass.getName}] ${t.getMessage}")
            UIO.succeed(1)
          }
        .provideSomeM(environment)
    }
  


    val _ = _rt.unsafeRun(program)
  }
}