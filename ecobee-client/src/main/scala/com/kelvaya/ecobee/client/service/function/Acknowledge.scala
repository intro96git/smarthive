package com.kelvaya.ecobee.client.service.function

import com.kelvaya.util.jsonenum.JsonStringEnum
import spray.json._
import spray.json.DefaultJsonProtocol._


/** Allows an alert to be acknowledged
 *
 * @param thermostatIdentifier The thermostat identifier to acknowledge the alert for.
 * @param ackRef The acknowledge ref of alert
 * @param ackType The type of acknowledgement.
 * @param remindMeLater Whether to remind at a later date, if this is a defer acknowledgement.
 */
case class Acknowledge(thermostatIdentifier : String, ackRef : String, ackType : Acknowledge.Type, remindMeLater : Boolean) extends EcobeeFunction[Acknowledge] {
  val name = "acknowledge"
  val params = this
  protected val writer = Acknowledge.Format
}


object Acknowledge {
  import com.kelvaya.util.SprayImplicits._

  type `Type` = `Type`.Entry
  object `Type` extends JsonStringEnum {
    val Accept = Val("accept")
    val Decline = Val("decline")
    val Defer = Val("defer")
    val Unacknowledged = Val("unacknowledged")
  }

  private lazy val Format = DefaultJsonProtocol.jsonFormat(Acknowledge.apply _, "thermostatIdentifier", "ackRef", "ackType", "remindMeLater")
}