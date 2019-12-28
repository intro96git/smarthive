package com.kelvaya.ecobee.server

import zio.Runtime
import zio.DefaultRuntime
import zio.internal.Platform
import zio.clock.Clock
import zio.console.Console
import zio.system.System
import zio.random.Random
import zio.blocking.Blocking

trait ServerRuntime extends Runtime[ServerEnv] {
  private lazy val _default = new DefaultRuntime {}

  override val platform: Platform = _default.platform
  override val environment: ServerEnv = new Clock.Live with Console.Live with System.Live with Random.Live with Blocking.Live with Settings.Live
}
