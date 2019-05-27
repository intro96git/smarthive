package com.kelvaya.ecobee.client

import com.kelvaya.util.SprayImplicits
import com.kelvaya.util.Time.FullDate

import spray.json._
import spray.json.DefaultJsonProtocol._

object Electricity extends SprayImplicits {

  implicit val TierFormat = DefaultJsonProtocol.jsonFormat3(Tier)
  implicit val DeviceFormat = DefaultJsonProtocol.jsonFormat5(Device)
  implicit val ElectricityFormat = DefaultJsonProtocol.jsonFormat1(Electricity.apply)

  /** Energy Recording Device
    * @param name The name of the device
    * @param tiers The list of Electricity Tiers containing the break down of daily electricity consumption of the device for the day, broken down per pricing tier.
    * @param lastUpdate The last time the reading was updated.
    * @param cost The last three daily electricity cost reads from the device in cents with a three decimal place precision.
    * @param consumption The last three daily electricity consumption reads from the device in KWh with a three decimal place precision.
    */
  case class Device(name : Option[String], tiers : Option[Seq[Tier]], lastUpdate : Option[FullDate],
      cost : Option[Seq[String]], consumption : Option[Seq[String]])
  extends ReadonlyApiObject

  /** Electricity pricing tier
    *
    * @param name The tier name as defined by the Utility. May be an empty string if the tier is undefined or the usage falls outside the defined tiers.
    * @param consumption The last daily consumption reading collected. The reading format and precision is to three decimal places in kWh.
    * @param cost The daily cumulative tier cost in dollars if defined by the Utility
    */
  case class Tier(name : Option[String], consumption : Option[String], cost : Option[String]) extends ReadonlyApiObject

}

/** Electricity usage measurements for a thermostat
  *
  * @param devices Readings from an electricity tier
  */
case class Electricity(devices : Option[Seq[Electricity.Device]]) extends ReadonlyApiObject