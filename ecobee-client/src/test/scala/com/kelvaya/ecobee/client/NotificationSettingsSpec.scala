package com.kelvaya.ecobee.client

import com.kelvaya.ecobee.test.client.BaseTestSpec

import spray.json._
import com.kelvaya.util.Time.DateOnly
import org.joda.time.DateTime
import org.joda.time.Period
import com.kelvaya.ecobee.client.NotificationSettings.Limit._
import com.kelvaya.util.Time

class NotificationSettingsSpec extends BaseTestSpec {

  val emailAddresses = """"emailAddresses" : [ "a@example.com", "b@example.com" ]"""
  val emailNotifications = """"emailNotificationsEnabled" : true"""
  val equip1month = """{ "filterLastChanged" : "2010-05-06", "filterLife" : 1, "filterLifeUnits" : "month",
        "remindMeDate" : "2010-06-06", "enabled" : true, "type" : "hvac", "remindTechnician" : false }"""
  val equip1hour = """{ "filterLastChanged" : "2010-05-06", "filterLife" : 1, "filterLifeUnits" : "hour",
        "remindMeDate" : "2010-06-06", "enabled" : false, "type" : "airFilter", "remindTechnician" : false }"""
  val general = """{ "enabled" : false, "type" : "temp", "remindTechnician" : false }"""
  val limitTemp = """{ "limit" : 500, "enabled" : true, "type" : "lowTemp", "remindTechnician" : false }"""
  val limitHumidity = """{ "limit" : 95, "enabled" : true, "type" : "lowHumidity", "remindTechnician" : false }"""

  val emailAddressesObj = Some(Seq("a@example.com", "b@example.com"))
  val equip1monthObj = NotificationSettings.Equipment(
          filterLastChanged = Some(new DateOnly(DateTime.parse("2010-05-06"))),
          filterLife        = Some(NotificationSettings.FilterLife(Period.months(1))),
          remindMeDate      = new DateOnly(DateTime.parse("2010-06-06")),
          enabled           = Some(true),
          `type`            = NotificationSettings.EquipmentType.HVAC,
          remindTechnician  = Some(false)
        )
  val equip1hourObj = NotificationSettings.Equipment(
          filterLastChanged = Some(new DateOnly(DateTime.parse("2010-05-06"))),
          filterLife        = Some(NotificationSettings.FilterLife(Period.hours(1))),
          remindMeDate      = new DateOnly(DateTime.parse("2010-06-06")),
          enabled           = Some(false),
          `type`            = NotificationSettings.EquipmentType.AirFilter,
          remindTechnician  = Some(false)
        )
  val generalObj = Some(Seq(NotificationSettings.General(Some(false), NotificationSettings.GeneralType.Temp, Some(false))))
  val limitTempObj = NotificationSettings.Limit.TempLimit(
          temperature      = Some(Temperature.fromCelcius(10)),
          enabled          = Some(true),
          isLowTempAlert   = true,
          remindTechnician = Some(false)
        )
  val limitHumidityObj = NotificationSettings.Limit.HumidityLimit(
          humidity           = Some(95),
          enabled            = Some(true),
          isLowHumidityAlert = true,
          remindTechnician   = Some(false)
        )


  // ########################################################################################
  // ########################################################################################
  // ########################################################################################

  "Notification ClientSettings JSON serialization GET requests" must "capture full objects" in {
    val expected = s"""{
      $emailAddresses,
      $emailNotifications,
      "equipment" : [
        $equip1month,
        $equip1hour
      ],
      "general" : [ $general ],
      "limit" : [
        $limitTemp,
        $limitHumidity
      ]
      }"""

    val actual = NotificationSettings(
      emailAddresses            = emailAddressesObj,
      emailNotificationsEnabled = Some(true),
      equipment                 = Some(Seq(equip1monthObj,equip1hourObj)),
      general                   = generalObj,
      limit                     = Some(Seq(limitTempObj,limitHumidityObj))
    )

    actual.toJson shouldBe expected.parseJson
    actual.toJson.convertTo[NotificationSettings] shouldBe actual
  }


  it must "capture optional parameters" in {
    val expected = "{}"
    val actual = NotificationSettings()

    actual.toJson shouldBe expected.parseJson

    val expected2 = """{
      "equipment" : [ {"type" : "airFilter", "remindMeDate" : "2010-10-01"} ],
      "general" : [ { "type" : "temp" } ],
      "limit" : [ { "type" : "auxOutdoor" } ]
      }"""
    val actual2 = NotificationSettings(
        equipment = Some(Seq(NotificationSettings.Equipment(`type` = NotificationSettings.EquipmentType.AirFilter,
            remindMeDate = new Time.DateOnly(DateTime.parse("2010-10-01"))))),
        general = Some(Seq(NotificationSettings.General(`type` = NotificationSettings.GeneralType.Temp))),
        limit = Some(Seq(NotificationSettings.Limit.AuxOutdoorLimit()))
    )
    actual2.toJson shouldBe expected2.parseJson
    actual2.toJson.convertTo[NotificationSettings] shouldBe actual2
  }



  it must "fail to parse when missing required parameters" in {
    val good1 = """{ "limit" : [ { "type" : "auxOutdoor" } ]  }"""
    val bad1 = """{ "limit" : [ {} ]  }"""
    val expected1 = NotificationSettings(limit = Some(Seq(NotificationSettings.Limit.AuxOutdoorLimit())))

    val good2 = """{ "general" : [ { "type" : "temp" } ]  }"""
    val bad2 = """{ "general" : [ {} ]  }"""
    val expected2 = NotificationSettings(general = Some(Seq(NotificationSettings.General(`type` = NotificationSettings.GeneralType.Temp))))

    val good3 = """{ "equipment" : [ { "type" : "uvLamp", "remindMeDate" : "2019-10-01" } ]  }"""
    val bad3 = """{ "equipment" : [ {} ]  }"""
    val expected3 = NotificationSettings(equipment = Some(Seq(NotificationSettings.Equipment(`type` = NotificationSettings.EquipmentType.UVLamp,
        remindMeDate = new Time.DateOnly(DateTime.parse("2019-10-01"))))))

    good1.parseJson.convertTo[NotificationSettings] shouldBe expected1
    good2.parseJson.convertTo[NotificationSettings] shouldBe expected2
    good3.parseJson.convertTo[NotificationSettings] shouldBe expected3

    intercept[DeserializationException] { bad1.parseJson.convertTo[NotificationSettings] }
    intercept[DeserializationException] { bad2.parseJson.convertTo[NotificationSettings] }
    intercept[DeserializationException] { bad3.parseJson.convertTo[NotificationSettings] }
  }


  it must "handle all Limit types" in {
    import NotificationSettings._

    LimitType.entries foreach limitMatch

    def limitMatch(limit : LimitType) = limit match {
      case LimitType.LowTemp => check(limit, TempLimit(isLowTempAlert=true))
      case LimitType.HiTemp => check(limit, TempLimit(isLowTempAlert=false))
      case LimitType.LowHumidity => check(limit, HumidityLimit(isLowHumidityAlert=true))
      case LimitType.HiHumidity => check(limit, HumidityLimit(isLowHumidityAlert=false))
      case LimitType.AuxHeat => check(limit, AuxHeatLimit())
      case LimitType.AuxOutdoor => check(limit, AuxOutdoorLimit())
      case _ => throw new NotImplementedError(s"$limit missing a test")
    }

    def check(check : LimitType, obj : Limit) = {
      val settingJson = s""" { "limit" : [ { "type" : "${check.toString}" } ] }"""
      settingJson.parseJson.convertTo[NotificationSettings] shouldBe NotificationSettings(limit = Some(Seq(obj)))
    }

  }

}
