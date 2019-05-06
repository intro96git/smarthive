package com.kelvaya.ecobee.client

import spray.json.DefaultJsonProtocol

object RemoteSensor {
  implicit val RemoteSensorFormat = DefaultJsonProtocol.jsonFormat0(RemoteSensor.apply)
}



/** Sensor connected to a [[Thermostat]]
  *
  * @see com.kelvaya.ecobee.client.Climate.RemoteSensor
  */
case class RemoteSensor()