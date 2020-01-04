package com.kelvaya.ecobee.test.client

import com.kelvaya.ecobee.client.ClientSettings
import com.typesafe.config.ConfigFactory


trait TestClientSettings extends ClientSettings {
  val settings = new TestClientSettings.TestClientService
}

object TestClientSettings {
  class TestClientService extends ClientSettings.Service[Any] {
    val config = ConfigFactory.load("test.conf")
    
    override lazy val EcobeeAppKey = TestConstants.ClientId
  }

  object Default extends TestClientSettings
}
