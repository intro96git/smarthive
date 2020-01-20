package com.kelvaya.ecobee.test.server

import zio.DefaultRuntime
import zio.Runtime
import zio.blocking.Blocking
import zio.clock.Clock
import zio.console.Console
import zio.internal.Platform
import zio.random.Random
import zio.system.System


class TestRuntime extends Runtime[ServerEnv] with ServerTestConstants {
  private lazy val _default = new DefaultRuntime {}
  private lazy val _env = 
    new Clock.Live 
    with Console.Live 
    with System.Live 
    with Random.Live 
    with Blocking.Live 
    with ServerTestSettings 

  override val platform: Platform = _default.platform.withReportFailure(_ => ())
  override val environment: ServerEnv = _env    
}
