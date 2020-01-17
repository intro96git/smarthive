package com.kelvaya.ecobee.client

import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.OptionValues

import spray.json._
import spray.json.DefaultJsonProtocol._
import com.kelvaya.util.SprayImplicits


class AlertNumberSpec extends AnyFlatSpec with Matchers with OptionValues with SprayImplicits {
  "Alert numbers" must "be able to encapsulate single value alerts" in {

    val num = AlertNumber.ACMaintenance
    val rawJsonValue = AlertNumber.ACMaintenance.id
    rawJsonValue.toJson shouldBe num.toJson
    rawJsonValue.toJson.convertTo[AlertNumber.Entry] shouldBe num
  }

  they must "be able to encapsulate ranged alerts" in {

    // The "default" for ranged is the first value in the range
    val num = AlertNumber.ClimateTalkFault
    val rawJsonValue = AlertNumber.ClimateTalkFault.id
    rawJsonValue.toJson shouldBe num.toJson
    num.entry.code shouldBe 4100

    val json = 4101.toJson
    json.convertTo[AlertNumber.Entry] shouldBe num
  }

  they must "only return recognized values" in {
    val unrecognized = 10.toJson
    intercept[NoSuchElementException] {
      unrecognized.convertTo[AlertNumber.Entry]
    }
  }
}