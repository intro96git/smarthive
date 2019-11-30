package com.kelvaya.ecobee.client.service


import spray.json.JsString
import spray.json.JsValue
import spray.json.RootJsonFormat

object CSV {
  val Delimiter = ":"

  def apply(vals : Iterable[String]) : CSV = CSV(vals.mkString(Delimiter.toString))


  /** JSON serializer for [[CSV]] */
  implicit object CSVFormat extends RootJsonFormat[CSV] {
    def read(json: JsValue): CSV = json match {
      case s : JsString => new CSV(s.value)
      case _ => throw new spray.json.DeserializationException(s"$json is not a valid colon-separated value")
    }

    def write(obj: CSV): JsValue = new JsString(obj.value)
  }

}


/** Colon-Separated-Values used in Ecobee API return values */
case class CSV(value : String)