package com.kelvaya.ecobee.client

import com.kelvaya.util.enum.JsonIntEnum
import spray.json.DefaultJsonProtocol._
import spray.json.DefaultJsonProtocol
import com.kelvaya.util.SprayImplicits

/** The forecast at a [[Thermostat thermostat's]] [[Location location]].
  *
  * @param weatherSymbol Iconic value of weather condition.
  * @param dateTime The time stamp of the weather forecast.
  * @param condition Textual value of the current weather condition.
  * @param temperature The current temperature.
  * @param pressure The current barometric pressure.
  * @param relativeHumidity The current humidity.
  * @param dewpoint The dewpoint.
  * @param visibility The visibility in meters; 0 - 70,000.
  * @param windSpeed The wind speed as an integer in mph * 1000.
  * @param windGust The wind gust speed.
  * @param windDirection The wind direction.
  * @param windBearing The wind bearing.
  * @param pop   Probability of precipitation.
  * @param tempHigh The predicted high temperature for the day.
  * @param tempLow The predicted low temperature for the day.
  * @param sky The cloud cover condition.
  */
case class WeatherForecast(
    weatherSymbol :    WeatherForecast.WeatherIcon,
    dateTime :         String,
    condition :        String,
    temperature :      Int,
    pressure :         Int,
    relativeHumidity : Int,
    dewpoint :         Int,
    visibility :       Int,
    windSpeed :        Int,
    windGust :         Int,
    windDirection :    String,
    windBearing :      Int,
    pop :              Int,
    tempHigh :         Int,
    tempLow :          Int,
    sky :              WeatherForecast.CloudCover
)


object WeatherForecast extends SprayImplicits {
  type WeatherIcon = WeatherIcon.Entry
  type CloudCover = CloudCover.Entry

  implicit val WeatherForecastFormat = DefaultJsonProtocol.jsonFormat16(WeatherForecast.apply)

  /** Iconic representation of the weather condition */
  object WeatherIcon extends JsonIntEnum {
    val None = Val(-2)
    val Sunny = Val(0)
    val FewClouds = Val(1)
    val PartlyCloudy = Val(2)
    val MostlyCloudy = Val(3)
    val Overcast = Val(4)
    val Drizzle = Val(5)
    val Rain = Val(6)
    val FreezingRain = Val(7)
    val Showers = Val(8)
    val Hail = Val(9)
    val Snow = Val(10)
    val Flurries = Val(11)
    val FreezingSnow = Val(12)
    val Blizzard = Val(13)
    val Pellets = Val(14)
    val Thunderstorm = Val(15)
    val Windy = Val(16)
    val Tornado = Val(17)
    val Fog = Val(18)
    val Haze = Val(19)
    val Smoke = Val(20)
    val Dust = Val(21)
  }


  /** The cloud cover weather condition */
  object CloudCover extends JsonIntEnum {
    val Sunny = Val(1)
    val Clear = Val(2)
    val MostlySunny = Val(3)
    val MostlyClear = Val(4)
    val HazySunshine = Val(5)
    val Haze = Val(6)
    val PassingClouds = Val(7)
    val MoreSunThanClouds = Val(8)
    val ScatteredClouds = Val(9)
    val PartlyCloudy = Val(10)
    val SunAndClouds = Val(11)
    val HighLevelClouds = Val(12)
    val MoreCloudsThanSun = Val(13)
    val PartlySunny = Val(14)
    val BrokenClouds = Val(15)
    val MostlyCloudy = Val(16)
    val Cloudy = Val(17)
    val Overcast = Val(18)
    val LowClouds = Val(19)
    val LightFog = Val(20)
    val Fog = Val(21)
    val DenseFog = Val(22)
    val IceFog = Val(23)
    val Sandstorm = Val(24)
    val Duststorm = Val(25)
    val IncreasingCloudiness = Val(26)
    val DecreasingCloudiness = Val(27)
    val ClearingSkies = Val(28)
    val SunLate = Val(29)
    val EarlyFog = Val(30)
    val AfternoonClouds = Val(31)
    val MorningClouds = Val(32)
    val Smoke = Val(33)
    val LowLevelHaze = Val(34)
  }
}