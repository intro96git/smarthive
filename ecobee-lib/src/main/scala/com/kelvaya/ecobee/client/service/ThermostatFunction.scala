package com.kelvaya.ecobee.client.service

import spray.json._
import spray.json.DefaultJsonProtocol._
import com.kelvaya.ecobee.client.service.function.EcobeeFunction
import scala.language.implicitConversions

/** API function used to update a [[Thermostat]].
  *
  * These allow for more complicated update operations than simple thermostat property modifications.
  *
  * @note These are manually created through the [[ThermostatFunction#apply]] method or by directly creating
  * one of the implementations of [[com.kelvaya.ecobee.client.service.function.EcobeeFunction EcobeeFunction]].
  *
  * @param type The function type (name)
  * @param params The parameters of the function
  *
  * @see [[com.kelvaya.ecobee.client.service.function]]
  */
case class ThermostatFunction private (`type` : String, params : JsObject)

object ThermostatFunction {
  implicit val Formatter = DefaultJsonProtocol.jsonFormat2(ThermostatFunction.apply)

  def apply[T : RootJsonWriter](`type` : String, params: T) : ThermostatFunction = {
    val p = implicitly[RootJsonWriter[T]].write(params).asJsObject
    ThermostatFunction(`type`, p)
  }
}