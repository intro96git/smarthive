package com.kelvaya.ecobee.client.service


import spray.json.JsonFormat
import spray.json.JsValue
import spray.json.JsString

object CSV {
  val Delimter = ":"

  implicit object CSVFormat extends JsonFormat[CSV] {
    def read(json: JsValue): CSV = json match {
      case s : JsString => new CSV(s.value)
      case _ => throw new spray.json.DeserializationException(s"$json is not a valid colon-separated value")
    }

    def write(obj: CSV): JsValue = new JsString(obj.value)
  }

}


/** Colon-Separated-Values used in Ecobee API return values */
case class CSV(value : String)