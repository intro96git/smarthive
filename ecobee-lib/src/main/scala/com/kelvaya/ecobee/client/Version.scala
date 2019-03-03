package com.kelvaya.ecobee.client

import spray.json.DefaultJsonProtocol

object Version {
  implicit val VersionFormat = DefaultJsonProtocol.jsonFormat0(Version.apply)
}

case class Version()