package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.test.client.BaseTestSpec
import EquipmentStatusListItem._
import spray.json._

class EquipmentStatusListItemSpec extends BaseTestSpec {

  import deps.Implicits._

  "Equipment Status lists" must "require 2 attributes, but allow a equipment list" in {
    val serialized1 = "abc"
    val serialized2 = "abc:"
    val serialized3 = "abc:compCool1:true"
    val serialized4 = "abc:compCool1"
    val serialized5 = ":abc"
    val serialized6 = ":"
    val serialized7 = "abc:compCool1,compCool2"
    val serialized8 = "abc:compCool1,compCool2,err"

    intercept[IllegalArgumentException] { fromCSV(CSV(serialized1)) }
    intercept[IllegalArgumentException] { fromCSV(CSV(serialized5)) }
    intercept[IllegalArgumentException] { fromCSV(CSV(serialized6)) }

    intercept[DeserializationException] { serialized1.toJson.convertTo[EquipmentStatusListItem] }
    intercept[DeserializationException] { serialized5.toJson.convertTo[EquipmentStatusListItem] }
    intercept[DeserializationException] { serialized6.toJson.convertTo[EquipmentStatusListItem] }

    val item2 = serialized2.toJson.convertTo[EquipmentStatusListItem]
    val item3 = serialized3.toJson.convertTo[EquipmentStatusListItem]
    val item4 = serialized4.toJson.convertTo[EquipmentStatusListItem]
    val item7 = serialized7.toJson.convertTo[EquipmentStatusListItem]
    val item8 = serialized8.toJson.convertTo[EquipmentStatusListItem]

    item2 shouldBe EquipmentStatusListItem("abc", None)
    item3 shouldBe EquipmentStatusListItem("abc", None)
    item4 shouldBe EquipmentStatusListItem("abc", Seq(Equipment.AC1))
    item7 shouldBe EquipmentStatusListItem("abc", Seq(Equipment.AC1,Equipment.AC2))
    item8 shouldBe EquipmentStatusListItem("abc", Seq(Equipment.AC1,Equipment.AC2))

    item2.toJson shouldBe serialized2.toJson
    item3.toJson should not be serialized3.toJson
    item4.toJson shouldBe serialized4.toJson
    item7.toJson shouldBe serialized7.toJson
    item8.toJson should not be serialized8.toJson
  }

}