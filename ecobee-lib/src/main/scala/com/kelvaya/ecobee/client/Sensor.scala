package com.kelvaya.ecobee.client

import com.kelvaya.util.enum.JsonStringEnum
import spray.json.DefaultJsonProtocol
import com.kelvaya.util.SprayImplicits

/** A sensor connected to a [[Thermostat]]
  *
  * @param name The sensor name
  * @param manufacturer The sensor manufacturer
  * @param model The sensor model
  * @param zone The thermostat zone the sensor is associated with
  * @param sensorId The unique sensor identifier
  * @param type The type of sensor
  * @param usage The sensor usage type.
  * @param numberOfBits The number of bits the adc has been configured to use.
  * @param bconstant Value of the bconstant value used in temperature sensors
  * @param thermistorSize The sensor thermistor value, ie. 10K thermistor=10000, 2.5K thermistor=2500
  * @param tempCorrection The user adjustable temperature compensation applied to the temperature reading.
  * @param gain The sensor thermistor gain value.
  * @param maxVoltage The sensor thermistor max voltage in Volts, 5=5V, 10=10V.
  * @param multiplier The multiplier value used in sensors (1000x value).
  * @param states A list of possible sensor states
  */
case class Sensor(name : String, manufacturer : String, model : String, zone : Int, sensorId : Int, `type` : Sensor.SensorType.Entry,
    usage : Sensor.SensorUsage.Entry, numberOfBits : Int, bconstant : Int, thermistorSize : Int, tempCorrection : Int, gain : Int,
    maxVoltage : Int, multiplier : Int, states : Seq[Sensor.State])


object Sensor extends SprayImplicits {

  implicit val ActionFormat = DefaultJsonProtocol.jsonFormat10(Action)
  implicit val StateFormat = DefaultJsonProtocol.jsonFormat4(State)
  implicit val SensorFormat = DefaultJsonProtocol.jsonFormat15(Sensor.apply)

  /** Configurable trigger for an [[Action]]
    *
    * @param maxValue The maximum value the sensor can generate.
    * @param minValue The minimum value the sensor can generate.
    * @param type The trigger type
    * @param actions The list of StateAction objects associated with the sensor.
    *
    */
  case class State(maxValue : Int, minValue : Int, `type` : StateType.Entry, actions : Seq[Action])

  /** An action to take when a sensor is triggered through a [[State]]
    *
    * @param type The type of action to take when triggered
    * @param sendAlert Flag to enable an alert to be generated when the state is triggered
    * @param sendUpdate Whether to send an update message.
    * @param activationDelay Delay in seconds before the action is triggered by the state change.
    * @param deactivationDelay The amount of time to wait before deactivating this state after the state has been cleared.
    * @param minActionDuration The minimum length of time to maintain action after sensor has been deactivated.
    * @param heatAdjustTemp The amount to increase/decrease current setpoint if the type is `AdjustTemp`.
    * @param coolAdjustTemp The amount to increase/decrease current setpoint if the type is `AdjustTemp`.
    * @param activateRelay The user defined relay to be activated if the type is `ActivateRelay`.
    * @param activateRelayOpen Select if relay is to be open or closed when activated if the type is `ActivateRelay`.
    */
  case class Action(`type` : ActionType.Entry, sendAlert : Boolean, sendUpdate : Boolean, activationDelay : Int,
      deactivationDelay : Int, minActionDuration : Int, heatAdjustTemp : Int, coolAdjustTemp : Int,
      activateRelay : String, activateRelayOpen : Boolean)


  /** The type of [[Sensor]]. */
  object SensorType extends JsonStringEnum {
    val ADC = Val("adc")
    val CO2 = Val("co2")
    val DryContact = Val("dryContact")
    val Humidity = Val("humidity")
    val Temperature = Val("temperature")
    val Unknown = Val("unknown")
  }


  /** The [[Sensor]] usage type. */
  object SensorUsage extends JsonStringEnum {
    val DischargeAir = Val("dischargeAir")
    val Indoor = Val("indoor")
    val Monitor = Val("monitor")
    val Outdoor = Val("outdoor")
  }


  /** The type of [[Sensor]] [[State]] trigger. */
  object StateType extends JsonStringEnum {
    val CoolHigh = Val("coolHigh")
    val CoolLow = Val("coolLow")
    val HeatHigh = Val("heatHigh")
    val HeatLow = Val("heatLow")
    val High = Val("high")
    val Low = Val("low")
    val TransitionCount = Val("transitionCount")
    val Normal = Val("normal")
  }


  /** A type of [[Action]] to take when a [[Sensor]] is triggered using a [[State]] */
  object ActionType extends JsonStringEnum {
    val ActivateRelay = Val("activateRelay")
    val AdjustTemp = Val("adjustTemp")
    val Nothing = Val("doNothing")
    val ACOff = Val("shutdownAC")
    val AuxHeatOff = Val("shutdownAuxHeat")
    val SystemOff = Val("shutdownSystem")
    val CompressionOff = Val("shutdownCompression")
    val MarkOccupied = Val("switchToOccupied")
    val MarkUnoccupied = Val("switchToUnoccupied")
    val DehumidifierOff = Val("turnOffDehumidifer")
    val HumidifierOff = Val("turnOffHumidifier")
    val ACOn = Val("turnOnCool")
    val DehumidifierOn = Val("turnOnDehumidifier")
    val FanOn = Val("turnOnFan")
    val HeatOn = Val("turnOnHeat")
    val HumidifierOn = Val("turnOnHumidifier")
  }
}