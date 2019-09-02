package com.kelvaya.ecobee.client.service.function

import spray.json.RootJsonWriter
import com.kelvaya.ecobee.client.service.ThermostatFunction

/** API function used to update a [[Thermostat]].
  *
  * These allow for more complicated update operations than simple thermostat property modifications.
  *
  * @see [[com.kelvaya.ecobee.client.service.ThermostatFunction ThermostatFunction]]
  */
trait EcobeeFunction[T <: Product] {

  /** The function name */
  val name : String

  /** The parameters of the function */
  val params : T

  protected val writer : RootJsonWriter[T]
}

object EcobeeFunction {
  import scala.language.implicitConversions
  implicit def toThermostatFunction[T <: Product](f : EcobeeFunction[T]) = ThermostatFunction(f.name, f.params)(f.writer)
}
