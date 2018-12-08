package com.kelvaya.ecobee

import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat
import spray.json._

package object client {

  type PinScope = PinScope.Value
  object PinScope extends Enumeration {
    val SmartWrite = Value("smartWrite")
    val SmartRead = Value("smartRead")
  }
  implicit val PinScopeFormatter : RootJsonFormat[PinScope] = new RootJsonFormat[PinScope] {
    // Members declared in spray.json.JsonReader
    def read(json: JsValue): PinScope = json match {
      case JsString(s) => PinScope.withName(s)
      case _ => throw new spray.json.DeserializationException("$json is not a recognized PinScope")
    }

    // Members declared in spray.json.JsonWriter
    def write(obj: PinScope): JsValue = JsString(obj.toString)
  }
}