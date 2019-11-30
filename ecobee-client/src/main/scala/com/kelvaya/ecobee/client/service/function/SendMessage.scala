package com.kelvaya.ecobee.client.service.function

import spray.json._
import spray.json.DefaultJsonProtocol._

/** Send an alert message to the [[Thermostat]].
  *
  *  @param text The message text to send. Text will be truncated to 500 characters if longer.
  */
case class SendMessage(text : String) extends EcobeeFunction[SendMessage] {
  val name = "sendMessage"
  val params = this
  val writer = SendMessage.Format
}

object SendMessage {
  private lazy val Format = DefaultJsonProtocol.jsonFormat(SendMessage.apply _, "text")
}