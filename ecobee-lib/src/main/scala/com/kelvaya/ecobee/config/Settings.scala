package com.kelvaya.ecobee.config

import com.typesafe.config.ConfigFactory
import com.typesafe.config.Config
import akka.http.scaladsl.model.Uri

object Settings extends Settings(ConfigFactory.load()) {
}

abstract class Settings(config: Config) {
  lazy val EcobeeServerRoot = Uri("http://example.org")
  lazy val EcobeeAppKey = ""
}