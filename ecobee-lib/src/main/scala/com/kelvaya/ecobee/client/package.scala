package com.kelvaya.ecobee

import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat
import spray.json._
import akka.http.scaladsl.model.HttpResponse

package object client {
  type ServiceResponse[T] = Either[HttpResponse,T]

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

  type TokenType = TokenType.Value
  object TokenType extends Enumeration {
    val Bearer = Value("Bearer")
  }
  implicit val TokenTypeFormatter : RootJsonFormat[TokenType] = new RootJsonFormat[TokenType] {
    // Members declared in spray.json.JsonReader
    def read(json: JsValue): TokenType = json match {
      case JsString(s) => TokenType.withName(s)
      case _ => throw new spray.json.DeserializationException("$json is not a recognized TokenType")
    }

    // Members declared in spray.json.JsonWriter
    def write(obj: TokenType): JsValue = JsString(obj.toString)
  }
}