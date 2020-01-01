package com.kelvaya.ecobee.test.client

import com.kelvaya.ecobee.client.ClientSettings


trait TestClientSettings extends ClientSettings {
  val settings = new TestClientSettings.TestClientService
}

object TestClientSettings {
  class TestClientService extends ClientSettings.LiveService {
    override lazy val EcobeeAppKey = TestConstants.ClientId
  }

  object Default extends TestClientSettings
}
