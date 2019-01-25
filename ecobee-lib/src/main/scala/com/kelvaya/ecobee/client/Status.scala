package com.kelvaya.ecobee.client

import spray.json.DefaultJsonProtocol
import spray.json.DefaultJsonProtocol._


object Status {
  implicit val StatusFormat = DefaultJsonProtocol.jsonFormat2(Status.apply)
}

/** Status returned by the Ecobee API for a request */
case class Status(code : Int, message : String)
