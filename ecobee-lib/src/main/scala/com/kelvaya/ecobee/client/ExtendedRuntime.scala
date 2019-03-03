package com.kelvaya.ecobee.client

import spray.json.DefaultJsonProtocol

object ExtendedRuntime {
  implicit def Format = DefaultJsonProtocol.jsonFormat0(ExtendedRuntime.apply)
}

case class ExtendedRuntime()