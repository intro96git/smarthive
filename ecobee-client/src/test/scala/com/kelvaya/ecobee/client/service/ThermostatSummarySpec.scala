package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.client.Status
import com.kelvaya.ecobee.test.client.BaseTestSpec

import com.twitter.finagle.http.Fields
import com.twitter.finagle.http.Message
import com.twitter.finagle.http.Method

import spray.json._
import com.typesafe.scalalogging.Logger

class ThermostatSummarySpec extends BaseTestSpec {

  import deps.Implicits._
  implicit val log = Logger[ThermostatSummarySpec]

  lazy val store = this.createStorage()

  "The thermostat summary service" must "serialize requests correctly" in {

    "ThermostatSummaryService.execute(account, SelectType.Registered, true)" should compile

    val thermReq = new ThermostatSummaryRequest(account, SelectType.Registered, true)
    "ThermostatSummaryService.execute(thermReq)" should compile

    val expectedSelectionQs = """{ "selection" : {"selectionType":"registered","includeEquipmentStatus":true} }""".parseJson

    val req = this.runtime.unsafeRun(thermReq.createRequest.provide(store))
    req.method shouldBe Method.Get
    req.contentString shouldBe ""
    req.uri should startWith("/1/thermostatSummary?")
    req.contentType shouldBe Some(Message.ContentTypeJson)
    req.headerMap.get(Fields.Authorization) shouldBe 'defined
    req.headerMap(Fields.Authorization) shouldBe "Bearer " + AccessToken

    val qsSelection = req.params.get("body")
    qsSelection.value.parseJson shouldBe expectedSelectionQs

    req.params.size shouldBe 2
  }


  it must "deserialize revision list responses correctly" in {

    val csvResponse = Seq(
      RevisionListItem("id1", Some("name1"), true, "therm1123", "alert1123", "runtime1123", "interval1123"),
      RevisionListItem("id2", None, true, "therm2345", "alert2345", "runtime2345", "interval2345")
    )

    val deserialized = ThermostatSummaryResponse(csvResponse, thermostatCount=2, None, Status(200, "OK"))
    val serialized = """{
      "revisionList" : [
        "id1:name1:true:therm1123:alert1123:runtime1123:interval1123",
        "id2::true:therm2345:alert2345:runtime2345:interval2345"
      ],
      "thermostatCount" : 2,
      "status" : {
        "code" : 200,
        "message" : "OK"
      }
      }""".parseJson

    serialized.convertTo[ThermostatSummaryResponse] shouldBe deserialized
    deserialized.toJson shouldBe serialized
  }

  it must "deserialize equipment status list responses correctly" in {
    val csvResponse = Seq(
      RevisionListItem("id1", Some("name1"), true, "therm1123", "alert1123", "runtime1123", "interval1123"),
    )
    val equipResponse = Seq(
      EquipmentStatusListItem("id1", Seq(Equipment.AC1, Equipment.Fan))
    )

    val deserialized = ThermostatSummaryResponse(csvResponse, thermostatCount=1, Some(equipResponse), Status(200, "OK"))
    val serialized = """{
    "revisionList" : [
      "id1:name1:true:therm1123:alert1123:runtime1123:interval1123"
    ],
    "thermostatCount" : 1,
    "status" : {
      "code" : 200,
      "message" : "OK"
    },
    "statusList" : [
      "id1:compCool1,fan"
    ]
    }""".parseJson

    serialized.convertTo[ThermostatSummaryResponse] shouldBe deserialized
    deserialized.toJson shouldBe serialized
  }

  it must "refuse to parse poorly-formed responses" in {

    val serialized1 = """{
      "revisionList" : [
        "id1:name1:true:therm1123:alert1123:runtime1123:interval1123",
        "id2:name2:ERROR:therm2345:alert2345:runtime2345:interval2345"
      ],
      "thermostatCount" : 2,
      "status" : {
        "code" : 200,
        "message" : "OK"
      }
      }""".parseJson

    val serialized2 = """{
      "thermostatCount" : 2,
      "status" : {
        "code" : 200,
        "message" : "OK"
      }
      }""".parseJson
    val serialized3 = """{
      "revisionList" : [
        "id1:name1:true:therm1123:alert1123:runtime1123:interval1123",
        "id2:name2:true:therm2345:alert2345:runtime2345:interval2345"
      ],
      "status" : {
        "code" : 200,
        "message" : "OK"
      }
      }""".parseJson
    val serialized4 = """{
      "revisionList" : [
        "id1:name1:true:therm1123:alert1123:runtime1123:interval1123",
        "id2:name2:true:therm2345:alert2345:runtime2345:interval2345"
      ],
      "thermostatCount" : 2
      }""".parseJson

    val serialized5 = """{
      "revisionList" : [
        "id1:name1:true:therm1123:alert1123:runtime1123:interval1123",
        "id2:name2:false:therm2345:alert2345:runtime2345:interval2345"
      ],
      "thermostatCount" : "2",
      "status" : {
        "code" : 200,
        "message" : "OK"
      }
      }""".parseJson

    val serialized6 = """{
      "revisionList" : [
        "id1:name1:true:therm1123:alert1123:runtime1123:interval1123",
        "id2:name2:false:therm2345:alert2345:runtime2345:interval2345"
      ],
      "thermostatCount" : 2,
      "status" : {
        "code" : "200",
        "message" : "OK"
      }
      }""".parseJson

    val serialized9 = """{
    "revisionList" : [
      "id1:name1:true:therm1123:alert1123:runtime1123:interval1123",
      "id2:name2:false:therm2345:alert2345:runtime2345:interval2345"
    ],
    "thermostatCount" : 2,
      "status" : {
        "code" : 200,
        "message" : 40
      }
    }""".parseJson

    val serialized10 = """{
    "revisionList" : [
      "id1:name1:true:therm1123:alert1123:runtime1123:interval1123",
      "id2:name2:false:therm2345:alert2345:runtime2345:interval2345"
    ],
    "thermostatCount" : 2,
    "status" : {
        "code" : 200,
        "message" : ""
      },
    "statusList" : "helloworld"
    }""".parseJson

    val serialized11 = """{
    "revisionList" : [
      "id1:name1:true:therm1123:alert1123:runtime1123:interval1123",
      "id2:name2:false:therm2345:alert2345:runtime2345:interval2345"
    ],
    "thermostatCount" : 2,
    "status" : {
        "code" : 200,
        "message" : ""
      },
    "statusList" : [ "" ]
    }""".parseJson

    val serialized12 = """{
    "revisionList" : [
      "id1:name1:true:therm1123:alert1123:runtime1123:interval1123",
      "id2:name2:false:therm2345:alert2345:runtime2345:interval2345"
    ],
    "thermostatCount" : 2,
    "status" : {
        "code" : 200,
        "message" : ""
      },
    "statusList" : [ "abc" ]
    }""".parseJson

    val serialized13 = """{
    "revisionList" : [
      "id1:name1:true:therm1123:alert1123:runtime1123:interval1123",
      "id2:name2:false:therm2345:alert2345:runtime2345:interval2345"
    ],
    "thermostatCount" : 2,
    "status" : {
        "code" : 200,
        "message" : ""
      },
    "statusList" : [ ":compCool1" ]
    }""".parseJson


    intercept[DeserializationException] { serialized1.convertTo[ThermostatSummaryResponse] }
    intercept[DeserializationException] { serialized2.convertTo[ThermostatSummaryResponse] }
    intercept[DeserializationException] { serialized3.convertTo[ThermostatSummaryResponse] }
    intercept[DeserializationException] { serialized4.convertTo[ThermostatSummaryResponse] }
    intercept[DeserializationException] { serialized5.convertTo[ThermostatSummaryResponse] }
    intercept[DeserializationException] { serialized6.convertTo[ThermostatSummaryResponse] }
    intercept[DeserializationException] { serialized9.convertTo[ThermostatSummaryResponse] }
    intercept[DeserializationException] { serialized10.convertTo[ThermostatSummaryResponse] }
    intercept[DeserializationException] { serialized11.convertTo[ThermostatSummaryResponse] }
    intercept[DeserializationException] { serialized12.convertTo[ThermostatSummaryResponse] }
    intercept[DeserializationException] { serialized13.convertTo[ThermostatSummaryResponse] }
  }
}
