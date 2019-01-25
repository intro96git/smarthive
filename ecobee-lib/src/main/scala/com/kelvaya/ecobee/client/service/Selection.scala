package com.kelvaya.ecobee.client.service

import spray.json.DefaultJsonProtocol

object Selection {
  implicit val SelectionFormat = DefaultJsonProtocol.jsonFormat0(Selection.apply)
}
case class Selection()