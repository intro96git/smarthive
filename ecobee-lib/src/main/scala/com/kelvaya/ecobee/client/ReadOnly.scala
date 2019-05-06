package com.kelvaya.ecobee.client

import scala.language.implicitConversions

import spray.json._


/** A read-only property of the API.  Writing to this field in a POST request will cause the runtime error, [[CannotSetReadOnlyParamterException]] */
sealed abstract class ReadOnly[T : JsonFormat](val property : Option[T])

object ReadOnly {
  class Value[T : JsonFormat](t : T) extends ReadOnly[T](Some(t))
  class Unset[T : JsonFormat] extends ReadOnly[T](None)

  def apply[T : JsonFormat](propValue : T) = toValue(propValue)

  implicit def toValue[T : JsonFormat](t : T) = new Value(t)

  implicit def getValueFormat[P : JsonFormat] = new JsonFormat[Value[P]] {
    def read(json: JsValue): Value[P] = new Value(implicitly[JsonFormat[P]].read(json))
    def write(obj: Value[P]): JsValue = implicitly[JsonFormat[P]].write(obj.property.get)
  }

  implicit def getBlankFormat[P : JsonFormat] = new JsonFormat[Unset[P]] {
    def read(json: JsValue): Unset[P] = new Unset[P]
    def write(obj: Unset[P]): JsValue = JsNull
  }
}



/** Thrown when a [[ReadOnly]] property is set in a [[Request]] */
class CannotSetReadOnlyParameterException(val prop : String, msg : Option[String] = None)
extends RuntimeException(msg.getOrElse(s"The read-only property, '$prop', cannot be set."))