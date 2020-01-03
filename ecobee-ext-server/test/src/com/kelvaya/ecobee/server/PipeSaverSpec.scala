package com.kelvaya.ecobee.server

import com.kelvaya.ecobee.test.server._

class PipeSaverSpec extends ZioServerTestSpec {

  "The Pipe Saver" must "be able to read the outside temperature" in (pending)
  it must "be able to read the outside wind speed" in (pending)
  it must "be able to determine the outside wind chill" in (pending)
  it must "be able to trigger the pipe-saver mode based on outside readings" in (pending)
  it must "support a graduated heating mode based on the outside readings" in (pending)
  it must "turn on the heat in small increments" in (pending)
  it must "not violate the Ecobee API restrictions on settings changes" in (pending)
  it must "only work when the heat-mode is enabled on the thermostat" in (pending)
  it must "send an alert when it is activated" in (pending)
  it must "send an alert when it increases its graduated heating length" in (pending)
}