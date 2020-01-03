package com.kelvaya.ecobee.test.server

import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.HttpResponse

import zio.DefaultRuntime
import zio.Runtime
import zio.blocking.Blocking
import zio.clock.Clock
import zio.console.Console
import zio.internal.Platform
import zio.random.Random
import zio.system.System

import spray.json.JsObject
import spray.json.JsonFormat

import com.kelvaya.ecobee.client.RequestExecutor
import com.kelvaya.ecobee.client.ServiceError
import com.kelvaya.ecobee.client.tokens.Tokens
import com.kelvaya.ecobee.test.client.TestStorage


class TestRuntime extends Runtime[ServerEnv] with ServerTestConstants {
  private lazy val _default = new DefaultRuntime {}
  private lazy val _env = 
    new NoExecutor 
    with Clock.Live 
    with Console.Live 
    with System.Live 
    with Random.Live 
    with Blocking.Live 
    with ServerTestSettings 
    with TestStorage { lazy val testStorageParams = Map(Account -> Tokens(Some(AuthCode), Some(AccessToken), Some(RefreshToken))) }

  override val platform: Platform = _default.platform.withReportFailure(_ => ())
  override val environment: ServerEnv = _env    
}

trait NoExecutor extends RequestExecutor {
  val requestExecutor = new RequestExecutor.Service[Any] {
    def executeRequest[S:JsonFormat,E<:ServiceError](req: HttpRequest, err: JsObject => E, fail: (Throwable, Option[HttpResponse]) => E) : zio.ZIO[Any,E,S] = ???
  }
}