package com.kelvaya.ecobee.client

import spray.json.DefaultJsonProtocol

object RemoteSensor {
  implicit val RemoteSensorFormat = DefaultJsonProtocol.jsonFormat0(RemoteSensor.apply)
}

case class RemoteSensor()