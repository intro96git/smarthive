package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.test.BaseTestSpec
import com.kelvaya.ecobee.client.Client
import com.kelvaya.ecobee.config.Settings

class ThermostatServiceSpec extends BaseTestSpec {

  implicit lazy val settings = this.injector.instance[Settings]
  implicit lazy val exec = this.createTestExecutor(Map.empty)
  implicit lazy val client = new Client

  "The thermostat service" must "serialize requests correctly" in {

    val selection = Select(SelectType.Thermostats, includeRuntime=true)

    val service = new ThermostatService()
    service.execute(selection)
  }

  it must "refuse to parse poorly-formed responses" in (pending)
}