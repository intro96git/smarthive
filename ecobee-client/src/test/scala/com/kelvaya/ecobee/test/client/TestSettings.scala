package com.kelvaya.ecobee.test.client

import com.typesafe.config.ConfigFactory
import com.kelvaya.ecobee.client.ConfigSettings

object TestSettings extends TestSettings

abstract class TestSettings extends ConfigSettings(ConfigFactory.load()) {
  override val EcobeeAppKey = TestConstants.ClientId
}