package com.kelvaya.ecobee.client.service;

import com.kelvaya.ecobee.test.client.BaseTestSpec

import RevisionListItem._
import spray.json._

class RevisionListSpec extends BaseTestSpec {

  "Revision lists" must "require all 7 attributes, but allow a blank name" in {
    val serialized1 = "abc"
    val serialized2 = "abc:abc:abc:abc:abc:abc:abc"
    val serialized3 = "abc:abc:true:abc:abc:abc:abc"
    val serialized4 = "abc:abc:true:abc:abc:abc:abc:abc"
    val serialized5 = "abc:abc::abc:abc:abc:abc"
    val serialized6 = "abc::true:abc:abc:abc:abc"
    val serialized7 = "abc::false:abc:abc:abc:abc"


    intercept[IllegalArgumentException] { fromCSV(CSV(serialized1)) }
    intercept[IllegalArgumentException] { fromCSV(CSV(serialized2)) }
    intercept[IllegalArgumentException] { fromCSV(CSV(serialized5)) }

    intercept[DeserializationException] { serialized1.toJson.convertTo[RevisionListItem] }
    intercept[DeserializationException] { serialized2.toJson.convertTo[RevisionListItem] }
    intercept[DeserializationException] { serialized5.toJson.convertTo[RevisionListItem] }

    val rl3 = serialized3.toJson.convertTo[RevisionListItem]
    val rl4 = serialized4.toJson.convertTo[RevisionListItem]
    val rl6 = serialized6.toJson.convertTo[RevisionListItem]
    val rl7 = serialized7.toJson.convertTo[RevisionListItem]


    rl3 shouldBe RevisionListItem("abc",Some("abc"),true,"abc","abc","abc","abc")
    rl4 shouldBe RevisionListItem("abc",Some("abc"),true,"abc","abc","abc","abc:abc")
    rl6 shouldBe RevisionListItem("abc",None,true,"abc","abc","abc","abc")
    rl7 shouldBe RevisionListItem("abc",None,false,"abc","abc","abc","abc")

    rl3.toJson shouldBe serialized3.toJson
    rl4.toJson shouldBe serialized4.toJson
    rl6.toJson shouldBe serialized6.toJson
    rl7.toJson shouldBe serialized7.toJson
  }
}

