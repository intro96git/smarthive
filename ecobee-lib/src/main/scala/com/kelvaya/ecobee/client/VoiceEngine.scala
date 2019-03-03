package com.kelvaya.ecobee.client

import spray.json._
import spray.json.DefaultJsonProtocol._

/** Ecobee4 voice engine.
  *
  *  @see Audio
  */
case class VoiceEngine(name : String, enabled : Boolean)

object VoiceEngine {
  implicit val VoiceEngineFormat = DefaultJsonProtocol.jsonFormat2(VoiceEngine.apply)
}