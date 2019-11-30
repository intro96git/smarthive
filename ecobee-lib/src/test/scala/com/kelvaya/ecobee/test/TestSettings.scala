package com.kelvaya.ecobee.test

import com.kelvaya.ecobee.config.Settings
import com.typesafe.config.ConfigFactory

object TestSettings extends TestSettings

abstract class TestSettings extends Settings(ConfigFactory.load()) {
  override val EcobeeAppKey = TestConstants.ClientId
}