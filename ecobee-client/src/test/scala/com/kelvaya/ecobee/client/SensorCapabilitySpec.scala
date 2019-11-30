package com.kelvaya.ecobee.client

import com.kelvaya.ecobee.test.BaseTestSpec
import spray.json._
import com.kelvaya.util.SprayImplicits

class SensorCapabilitySpec extends BaseTestSpec with SprayImplicits {

  "Sensor Capabilities" must "be deserializable for temperatures" in {
    val actual = """{ "id" : "mysensor", "type" : "temperature", "value" : "750" }""".parseJson
    val expected = SensorCapability("mysensor", SensorCapability.Type.Temperature, SensorValue.TemperatureValue(Temperature(750)))
    actual.convertTo[SensorCapability] shouldBe expected

    intercept[DeserializationException] {
      """{ "id" : "mysensor", "type" : "temperature", "value" : "A" }""".parseJson.convertTo[SensorCapability]
    }

    intercept[DeserializationException] {
      """{ "id" : "mysensor", "type" : "temperature", "value" : "" }""".parseJson.convertTo[SensorCapability]
    }
  }



  they must "be deserializable for occupancies" in {
    val actual = """{ "id" : "mysensor", "type" : "occupancy", "value" : "true" }""".parseJson
    val expected = SensorCapability("mysensor", SensorCapability.Type.Occupancy, SensorValue.BooleanValue(true))
    actual.convertTo[SensorCapability] shouldBe expected

    val actual2 = """{ "id" : "mysensor", "type" : "occupancy", "value" : "false" }""".parseJson
    val expected2 = SensorCapability("mysensor", SensorCapability.Type.Occupancy, SensorValue.BooleanValue(false))
    actual2.convertTo[SensorCapability] shouldBe expected2

    intercept[DeserializationException] {
      """{ "id" : "mysensor", "type" : "occupancy", "value" : "A" }""".parseJson.convertTo[SensorCapability]
    }

    intercept[DeserializationException] {
      """{ "id" : "mysensor", "type" : "occupancy", "value" : "" }""".parseJson.convertTo[SensorCapability]
    }
  }



  they must "be deserializable for humidities" in {
    val actual = """{ "id" : "mysensor", "type" : "humidity", "value" : "100" }""".parseJson
    val expected = SensorCapability("mysensor", SensorCapability.Type.Humidity, SensorValue.IntValue(100))
    actual.convertTo[SensorCapability] shouldBe expected

    intercept[DeserializationException] {
      """{ "id" : "mysensor", "type" : "humidity", "value" : "A" }""".parseJson.convertTo[SensorCapability]
    }

    intercept[DeserializationException] {
      """{ "id" : "mysensor", "type" : "humidity", "value" : "" }""".parseJson.convertTo[SensorCapability]
    }
  }



  they must "be deserializable for unknowns" in {
    val actual = """{ "id" : "mysensor", "type" : "unknown", "value" : "unknown" }""".parseJson
    val expected = SensorCapability("mysensor", SensorCapability.Type.Unknown, SensorValue.StringValue("unknown"))
    actual.convertTo[SensorCapability] shouldBe expected

    intercept[DeserializationException] {
      """{ "id" : "mysensor", "type" : "unknown", "value" : "A" }""".parseJson.convertTo[SensorCapability]
    }

    intercept[DeserializationException] {
      """{ "id" : "mysensor", "type" : "unknown", "value" : "" }""".parseJson.convertTo[SensorCapability]
    }

    intercept[DeserializationException] {
      """{ "id" : "mysensor", "type" : "unknown", "value" : "100" }""".parseJson.convertTo[SensorCapability]
    }
  }



  they must "be deserializable for all other valid types" in {
    val actual = """{ "id" : "mysensor", "type" : "adc", "value" : "helloworld" }""".parseJson
    val expected = SensorCapability("mysensor", SensorCapability.Type.ADC, SensorValue.StringValue("helloworld"))
    actual.convertTo[SensorCapability] shouldBe expected

    val actual2 = """{ "id" : "mysensor", "type" : "co2", "value" : "helloworld" }""".parseJson
    val expected2 = SensorCapability("mysensor", SensorCapability.Type.CO2, SensorValue.StringValue("helloworld"))
    actual2.convertTo[SensorCapability] shouldBe expected2

    val actual3 = """{ "id" : "mysensor", "type" : "dryContact", "value" : "helloworld" }""".parseJson
    val expected3 = SensorCapability("mysensor", SensorCapability.Type.DryContact, SensorValue.StringValue("helloworld"))
    actual3.convertTo[SensorCapability] shouldBe expected3
  }



  they must "not be writeable to JSON" in {
    val expected = SensorCapability("mysensor", SensorCapability.Type.Temperature, new SensorValue.BooleanValue(false))
    intercept[SerializationException] { expected.toJson }
  }
}
