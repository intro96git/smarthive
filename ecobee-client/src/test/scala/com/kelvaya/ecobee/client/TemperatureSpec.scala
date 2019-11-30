package com.kelvaya.ecobee.client

import com.kelvaya.ecobee.test.BaseTestSpec

class TemperatureSpec extends BaseTestSpec {

  "The temperature" must "be representable in Ecobee, Fahrenheit, and Celcius" in {
    val tempC1 = 100d
    val tempC2 = 0d
    val tempC3 = 10d
    val tempC4 = 10.5d
    val tempF1 = 212d
    val tempF2 = 32d
    val tempF3 = 50d
    val tempF4 = 50.9d

    Temperature.fromCelcius(tempC1) shouldBe Temperature.fromFahrenheit(tempF1)
    Temperature.fromCelcius(tempC2) shouldBe Temperature.fromFahrenheit(tempF2)
    Temperature.fromCelcius(tempC3) shouldBe Temperature.fromFahrenheit(tempF3)
    Temperature.fromCelcius(tempC4) shouldBe Temperature.fromFahrenheit(tempF4)

    Temperature.fromFahrenheit(tempF1) shouldBe Temperature(tempF1.toInt * 10)
  }

}
