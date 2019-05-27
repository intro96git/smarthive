package com.kelvaya.ecobee.client

import spray.json._
import spray.json.DefaultJsonProtocol._
import com.kelvaya.util.enum.JsonStringEnum
import com.kelvaya.util.SprayImplicits

object Device extends SprayImplicits {
  implicit val OutputFormat = DefaultJsonProtocol.jsonFormat8(Output)
  implicit val DeviceFormat = DefaultJsonProtocol.jsonFormat4(Device.apply)


  /** Relay connected to a thermostat
    *
    * @param name The name of the output
    * @param zone The thermostat zone the output is associated with
    * @param outputId The unique output identifier number.
    * @param type The type of output
    * @param sendUpdate Whether to send an update message.
    * @param activeClosed If true, when this output is activated it will close the relay. Otherwise, activating the relay will open the relay.
    * @param activationTime Time to activate relay - in seconds.
    * @param deactivationTime Time to deactivate relay - in seconds.
    */
  case class Output(name : Option[String], zone : Option[Int], outputId : Option[Int], `type` : Option[OutputType.Entry],
      sendUpdate : Option[Boolean], activateClosed : Option[Boolean], activationTime : Option[Int],
      deactivationTime : Option[Int]) extends ReadonlyApiObject


  /** A type of [[Output]] */
  object OutputType extends JsonStringEnum {
    val Compressor1 = Val("compressor1")
    val Compressor2 = Val("compressor2")
    val Dehumidifier = Val("dehumidifier")
    val Economizer = Val("economizer")
    val Fan = Val("fan")
    val Heat1 = Val("heat1")
    val Heat2 = Val("heat2")
    val Heat3 = Val("heat3")
    val PumpReversal = Val("heatPumpReversal")
    val Humidifier = Val("humidifer")
    val None = Val("none")
    val Occupancy = Val("occupancy")
    val UserDefined = Val("userDefined")
    val Ventilator = Val("ventilator")
    val ZoneCool = Val("zoneCool")
    val ZoneFan = Val("zoneFan")
    val ZoneHeat = Val("zoneHeat")
  }
}


/** Device attached to the thermostat.
  *
  * @param deviceId A unique ID for the device
  * @param name The user supplied device name
  * @param sensors The list of Sensor Objects associated with the device
  * @param outputs Ths list of Output Objects associated with the device
  */
case class Device(deviceId : Option[Int], name : Option[String], sensors : Option[Seq[Sensor]], outputs : Option[Seq[Device.Output]])
extends ReadonlyApiObject
