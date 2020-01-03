package com.kelvaya.ecobee.test.server

import com.kelvaya.ecobee.server.ServerSettings


trait ServerTestSettings extends ServerSettings {
  val settings = new ServerSettings.LiveService {
  }
}

object ServerTestSettings {
  object Default extends ServerTestSettings
}