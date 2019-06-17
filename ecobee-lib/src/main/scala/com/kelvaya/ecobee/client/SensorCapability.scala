package com.kelvaya.ecobee.client

import spray.json._
import spray.json.DefaultJsonProtocol._
import com.kelvaya.util.enum.JsonStringEnum
import com.kelvaya.util.SprayImplicits

/** Specific capability of a [[RemoteSensor]] connected to the [[Thermostat]].
  *
  * @param id The unique sensor capability identifier
  * @param type The type of sensor capability
  * @param value The data value for this capability. Unknown values are returned as "unknown".    *
  */
case class SensorCapability(id : String, `type` : SensorCapability.Type, value : SensorValue) extends ReadonlyApiObject


object SensorCapability extends SprayImplicits {
  type Type = Type.Entry
  object Type extends JsonStringEnum {
    val ADC = Val("adc")
    val CO2 = Val("co2")
    val DryContact = Val("dryContact")
    val Humidity = Val("humidity")
    val Temperature = Val("temperature")
    val Occupancy = Val("occupancy")
    val Unknown = Val("unknown")
  }


  implicit object SensorCapabilityFormat extends RootJsonFormat[SensorCapability] {
    def read(json: JsValue): SensorCapability = json match {
      case o : JsObject => parse(o)
      case _ => deserializationError(s"$json is not a valid SensorCapability")
    }

    private def parse(o : JsObject) : SensorCapability = {
      val id = find[String](o, "id")

      val cap = {
        val stpe = find[String](o, "type")

        Type.entries.find(_.entry == stpe) map { t =>
          val v = find[String](o, "value")

          try SensorCapability(id, t, SensorValue.fromJson(v, t))
          catch {
            case _ : IllegalArgumentException => deserializationError(s"$v is not a valid value for the $v capability")
            case t : Throwable => deserializationError(t.getMessage, t, List("type","value"))
          }
        }
      }

      cap getOrElse deserializationError(s"$o is not a valid Sensor Capability")
    }

    def write(obj: SensorCapability): JsValue = serializationError("SensorCapability is a read-only object and cannot be serialized.")
  }
}
