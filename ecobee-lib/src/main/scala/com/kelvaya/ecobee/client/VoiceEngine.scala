package com.kelvaya.ecobee.client

import spray.json._
import spray.json.DefaultJsonProtocol._

/** Ecobee4 voice engine.
  *
  *  @see Audio
  */
case class VoiceEngine(name : Option[String], enabled : Option[Boolean]) extends ReadonlyApiObject

object VoiceEngine {
  implicit val VoiceEngineFormat = DefaultJsonProtocol.jsonFormat2(VoiceEngine.apply)
}