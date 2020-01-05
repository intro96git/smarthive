package com.kelvaya.ecobee.server

import zio.DefaultRuntime
import zio.Runtime
import zio.Task
import zio.blocking.Blocking
import zio.clock.Clock
import zio.console.Console
import zio.internal.Platform
import zio.random.Random
import zio.system.System

import akka.actor.ActorSystem

import com.kelvaya.ecobee.client.RequestExecutorImpl
import com.kelvaya.ecobee.client.tokens.H2DbTokenStorage

class ServerRuntime(xa : doobie.Transactor[Task])(implicit sys : ActorSystem) extends Runtime[ServerEnv] {
  private lazy val _default = new DefaultRuntime {}

  override val platform: Platform = _default.platform
  override val environment : ServerEnv = new RequestExecutorImpl with ServerSettings.Live with Clock.Live with Console.Live 
    with System.Live with Random.Live with Blocking.Live with H2DbTokenStorage {
      val transactor: doobie.Transactor[zio.Task] = xa
    }

}