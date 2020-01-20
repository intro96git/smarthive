package com.kelvaya.ecobee.server

import zio.DefaultRuntime
import zio.Runtime
import zio.blocking.Blocking
import zio.clock.Clock
import zio.console.Console
import zio.internal.Platform
import zio.random.Random
import zio.system.System


/** ZIO Runtime used by the server which uses an environment containing [[ServerEnv]] */
class ServerRuntime extends Runtime[ServerEnv] {
  private lazy val _default = new DefaultRuntime {}

  override val platform: Platform = _default.platform
  override val environment : ServerEnv = new ServerSettings.Live with Clock.Live with Console.Live with System.Live with Random.Live with Blocking.Live {}
}