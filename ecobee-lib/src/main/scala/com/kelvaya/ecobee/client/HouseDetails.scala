package com.kelvaya.ecobee.client

import spray.json.DefaultJsonProtocol

object HouseDetails {
  implicit val HouseDetailsFormat = DefaultJsonProtocol.jsonFormat0(HouseDetails.apply)
}

case class HouseDetails()