package com.kelvaya.ecobee.client

import com.kelvaya.ecobee.test.BaseTestSpec

class TemperatureSpec extends BaseTestSpec {

  "The temperature" must "be representable in Ecobee, Fahrenheit, and Celcius" in {
    val tempC1 = 100
    val tempC2 = 0
    val tempC3 = 10
    val tempC4 = 10.5
    val tempF1 = 212
    val tempF2 = 32
    val tempF3 = 50
    val tempF4 = 50.9

    Temperature.fromCelcius(tempC1) shouldBe Temperature.fromFahrenheit(tempF1)
    Temperature.fromCelcius(tempC2) shouldBe Temperature.fromFahrenheit(tempF2)
    Temperature.fromCelcius(tempC3) shouldBe Temperature.fromFahrenheit(tempF3)
    Temperature.fromCelcius(tempC4) shouldBe Temperature.fromFahrenheit(tempF4)

    Temperature.fromFahrenheit(tempF1) shouldBe Temperature(tempF1 * 10)
  }

}