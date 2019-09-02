package com.kelvaya.ecobee.client.service.function

import spray.json._
import spray.json.DefaultJsonProtocol._

/** Update the name of an ecobee3 remote sensor.
 *
 *  @param name The updated name to give the sensor. Has a max length of 32, but shorter is recommended.
 *  @param deviceId The deviceId for the sensor, typically this indicates the enclosure and corresponds to the [[com.kelvaya.ecobee.client.RemoteSensor#id RemoteSensor ID]] field. For example: rs:100
 *  @param sensorId The identifier for the sensor within the enclosure. Corresponds to the [[com.kelvaya.ecobee.client.SensorCapability RemoteSensorCapability ID]]. For example: 1
 *
 *  @see [[UpdateSensorFunction]]
 */
case class UpdateSensor(sensorName : String, deviceId : String, sensorId : String) extends EcobeeFunction[UpdateSensorFunction] {
  val name = "updateSensor"
  val params = UpdateSensorFunction(sensorName, deviceId, sensorId)
  val writer = UpdateSensorFunction.Format
}

/** Update the name of an ecobee3 remote sensor.
  *
  * @note To use as a [[ThermostatFunction]], you must instead create an instance of [[UpdateSensor]].
  *
  * @param name The updated name to give the sensor. Has a max length of 32, but shorter is recommended.
  * @param deviceId The deviceId for the sensor, typically this indicates the enclosure and corresponds to the [[com.kelvaya.ecobee.client.RemoteSensor#id RemoteSensor ID]] field. For example: rs:100
  * @param sensorId The identifier for the sensor within the enclosure. Corresponds to the [[com.kelvaya.ecobee.client.SensorCapability RemoteSensorCapability ID]]. For example: 1
  */
case class UpdateSensorFunction(name : String, deviceId : String, sensorId : String)
object UpdateSensorFunction {
  lazy val Format = DefaultJsonProtocol.jsonFormat3(UpdateSensorFunction.apply)
}