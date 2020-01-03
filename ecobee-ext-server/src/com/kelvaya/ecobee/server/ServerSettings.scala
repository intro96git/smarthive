package com.kelvaya.ecobee.server

import com.kelvaya.ecobee.client.ClientSettings

trait ServerSettings extends ClientSettings {
  val settings : ServerSettings.Service[Any]
}

object ServerSettings {
  trait Service[R] extends ClientSettings.Service[R] {
  }

  trait Live extends ServerSettings {
    val settings = new LiveService
  }
  object Live extends Live

  class LiveService extends ClientSettings.LiveService with Service[Any]
}