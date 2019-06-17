package com.kelvaya.ecobee.client

import spray.json._
import spray.json.DefaultJsonProtocol._
import com.kelvaya.util.enum.JsonStringEnum
import com.kelvaya.util.SprayImplicits

object RemoteSensor extends SprayImplicits {
  type Type = Type.Entry
  object Type extends JsonStringEnum {
    val Thermostat = Val("thermostat")
    val Ecobee3 = Val("ecobee3_remote_sensor")
    val Monitor = Val("monitor_sensor")
    val Control = Val("control_sensor")
  }

  implicit val RemoteSensorFormat = DefaultJsonProtocol.jsonFormat6(RemoteSensor.apply)
}

/** Sensor connected to a [[Thermostat]]
  *
  * @param id The unique sensor identifier
  * @param name The sensor name
  * @param type The type of sensor
  * @param code The alphanumeric sensor code
  * @param inUse Whether the remote sensor is currently in use by a comfort setting.
  * @param capability The list capabilities for the sensor.
  *
  * @see com.kelvaya.ecobee.client.Climate.RemoteSensor
  */
case class RemoteSensor(id : String, name : String, `type` : RemoteSensor.Type,
                        code : String, inUse : Boolean, capability : Seq[SensorCapability])
  extends ReadonlyApiObject