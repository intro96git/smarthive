package com.kelvaya.ecobee.server
import com.typesafe.config.ConfigFactory

trait Settings {
  val settings : Settings.Service[Any]
}

object Settings {
  trait Service[R] {

  }

  trait Live extends Settings {
    val settings = new Service[Any] {
      private lazy val _config = ConfigFactory.load()

    }
  }
}