package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.test.client.BaseTestSpec
import spray.json._

class SelectionSpec extends BaseTestSpec {

  "A request select parameter" must "serialize to JSON properly" in {
    val select = Select(SelectType.Registered, true, true, true, true, true, true, true,
        true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true)

    val formatter = implicitly[RootJsonFormat[Select]]
    val json = formatter.write(select)

    json shouldBe """{
        "selectionType" : "registered",
        "includeRuntime" : true,
        "includeExtendedRuntime" : true,
        "includeElectricity" : true,
        "includeSettings" : true,
        "includeLocation" : true,
        "includeProgram" : true,
        "includeEvents" : true,
        "includeDevice" : true,
        "includeTechnician" : true,
        "includeUtility" : true,
        "includeManagement" : true,
        "includeAlerts" : true,
        "includeReminders" : true,
        "includeHouseDetails" : true,
        "includeOemCfg" : true,
        "includeEquipmentStatus" : true,
        "includeNotificationSettings" : true,
        "includePrivacy" : true,
        "includeVersion" : true,
        "includeWeather" : true,
        "includeSecuritySettings" : true,
        "includeSensors" : true,
        "includeAudio" : true,
        "includeEnergy" : true
      }""".parseJson


    val select2 = Select(SelectType.Thermostats("test"), includeAlerts = true)

    val expected2 = """{
      "selectionType" : "thermostats",
      "selectionMatch" : "test",
      "includeAlerts" : true
      }""".parseJson

    select2.toJson shouldBe expected2

    val select3 = Select(SelectType.ManagementSet("testms"))

    val expected3 = """{
      "selectionType" : "managementSet",
      "selectionMatch" : "testms"
      }""".parseJson

    select3.toJson shouldBe expected3

  }


  it must "be deserialzed from JSON" in {
    val json =  """{
        "selectionType" : "registered",
        "includeRuntime" : true,
        "includeExtendedRuntime" : true,
        "includeElectricity" : true,
        "includeSettings" : true,
        "includeLocation" : true,
        "includeProgram" : true,
        "includeEvents" : true,
        "includeDevice" : true,
        "includeTechnician" : true,
        "includeUtility" : true,
        "includeManagement" : true,
        "includeAlerts" : true,
        "includeReminders" : true,
        "includeHouseDetails" : true,
        "includeOemCfg" : true,
        "includeEquipmentStatus" : true,
        "includeNotificationSettings" : true,
        "includePrivacy" : true,
        "includeVersion" : true,
        "includeWeather" : true,
        "includeSecuritySettings" : true,
        "includeSensors" : true,
        "includeAudio" : true,
        "includeEnergy" : true
      }""".parseJson

      val expected = Select(SelectType.Registered, true, true, true, true, true, true, true,
        true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true)

      val actual = json.convertTo[Select]

      actual shouldBe expected
  }


  it must "require selectionType and selectionMatch" in {
      val missingBoth = """{
        "includeRuntime" : true,
        "includeExtendedRuntime" : true,
        "includeElectricity" : true,
        "includeSettings" : true,
        "includeLocation" : true,
        "includeProgram" : true,
        "includeEvents" : true,
        "includeDevice" : true,
        "includeTechnician" : true,
        "includeUtility" : true,
        "includeManagement" : true,
        "includeAlerts" : true,
        "includeReminders" : true,
        "includeHouseDetails" : true,
        "includeOemCfg" : true,
        "includeEquipmentStatus" : true,
        "includeNotificationSettings" : true,
        "includePrivacy" : true,
        "includeVersion" : true,
        "includeWeather" : true,
        "includeSecuritySettings" : true,
        "includeSensors" : true,
        "includeAudio" : true,
        "includeEnergy" : true
      }""".parseJson

      val missingType = """{
        "selectionMatch" : "sm",
        "includeRuntime" : true,
        "includeExtendedRuntime" : true,
        "includeElectricity" : true,
        "includeSettings" : true,
        "includeLocation" : true,
        "includeProgram" : true,
        "includeEvents" : true,
        "includeDevice" : true,
        "includeTechnician" : true,
        "includeUtility" : true,
        "includeManagement" : true,
        "includeAlerts" : true,
        "includeReminders" : true,
        "includeHouseDetails" : true,
        "includeOemCfg" : true,
        "includeEquipmentStatus" : true,
        "includeNotificationSettings" : true,
        "includePrivacy" : true,
        "includeVersion" : true,
        "includeWeather" : true,
        "includeSecuritySettings" : true,
        "includeSensors" : true,
        "includeAudio" : true,
        "includeEnergy" : true
      }""".parseJson


      val missingMatch = """{
        "selectionType" : "thermostats",
        "includeRuntime" : true,
        "includeExtendedRuntime" : true,
        "includeElectricity" : true,
        "includeSettings" : true,
        "includeLocation" : true,
        "includeProgram" : true,
        "includeEvents" : true,
        "includeDevice" : true,
        "includeTechnician" : true,
        "includeUtility" : true,
        "includeManagement" : true,
        "includeAlerts" : true,
        "includeReminders" : true,
        "includeHouseDetails" : true,
        "includeOemCfg" : true,
        "includeEquipmentStatus" : true,
        "includeNotificationSettings" : true,
        "includePrivacy" : true,
        "includeVersion" : true,
        "includeWeather" : true,
        "includeSecuritySettings" : true,
        "includeSensors" : true,
        "includeAudio" : true,
        "includeEnergy" : true
      }""".parseJson


      intercept[DeserializationException](missingBoth.convertTo[Select])
      intercept[DeserializationException](missingType.convertTo[Select])
      intercept[DeserializationException](missingMatch.convertTo[Select])
  }


  it must "default all non-required boolean fields to 'false'" in {
    val json = """{
        "selectionType" : "registered"
        }""".parseJson

    val expected = Select(SelectType.Registered, false, false, false, false, false, false, false, false, false, false,
        false, false, false, false, false, false, false, false, false, false, false, false, false, false)

    val alsoExpected = Select(SelectType.Registered)

    val actual = json.convertTo[Select]

    actual shouldBe expected
    actual shouldBe alsoExpected
  }



  it must "properly handle all values for SelectionType" in {
    val thermoJson = """{
        "selectionType" : "registered"
        }""".parseJson
    val registeredJson = """{
        "selectionType" : "thermostats",
        "selectionMatch" : "sm,sm2,sm3"
        }""".parseJson
    val manageJson = """{
        "selectionType" : "managementSet",
        "selectionMatch" : "sm"
        }""".parseJson
    val badJson = """{
        "selectionType" : "helloworld",
        "selectionMatch" : "sm"
        }""".parseJson

    thermoJson.convertTo[Select] shouldBe Select(SelectType.Registered)
    registeredJson.convertTo[Select] shouldBe Select(SelectType.Thermostats("sm","sm2","sm3"))
    manageJson.convertTo[Select] shouldBe Select(SelectType.ManagementSet("sm"))
    intercept[DeserializationException](badJson.convertTo[Select])
  }
}