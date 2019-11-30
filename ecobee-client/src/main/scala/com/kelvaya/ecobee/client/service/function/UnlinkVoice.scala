package com.kelvaya.ecobee.client.service.function

import spray.json._
import spray.json.DefaultJsonProtocol._


/** Disable voice assistant for the selected thermostat
  *
  * @param engineName The name of the engine to unlink.
  */
case class UnlinkVoice(engineName : String) extends EcobeeFunction[UnlinkVoice] {
  val name = "unlinkVoiceEngine"
  val params = this
  val writer = UnlinkVoice.Format
}

object UnlinkVoice {
  private lazy val Format = DefaultJsonProtocol.jsonFormat(UnlinkVoice.apply _, "engineName")
}