package com.kelvaya.ecobee.client.service.function


/** Sets all of the user configurable settings back to the factory default values */
case class ResetPreferences() extends EcobeeFunction[ResetPreferences] {
  val name = "resetPreferences"
  lazy val params = this
  lazy val writer = ResetPreferences.Format
}

object ResetPreferences {
  private lazy val Format = spray.json.DefaultJsonProtocol.jsonFormat0(() => ResetPreferences.apply)
}
