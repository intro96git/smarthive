package com.kelvaya.ecobee.client

import spray.json.JsonReader

/** Value returned by a [[Sensor]]
  *
  *  The value returned is based upon the [[SensorCapability]] that is producing the value.  To get the value,
  *  one must call the [[#value]] method.
  */
sealed abstract class SensorValue {
  
  /** Type of the value returned by the sensor */
  type T
  
  /** Value returned by a [[Sensor]] */
  val value : T
}


object SensorValue {

  def fromJson(json : String, capabilityType : SensorCapability.Type) : SensorValue = capabilityType match {
    case SensorCapability.Type.ADC         ⇒ stringValue(json)
    case SensorCapability.Type.CO2         ⇒ stringValue(json)
    case SensorCapability.Type.DryContact  ⇒ stringValue(json)
    case SensorCapability.Type.Humidity    ⇒ intValue(json)
    case SensorCapability.Type.Occupancy   ⇒ booleanValue(json)
    case SensorCapability.Type.Temperature ⇒ temperatureValue(json)
    case SensorCapability.Type.Unknown ⇒ {
      if (json == SensorCapability.Type.Unknown.toString()) stringValue(json)
      else throw new IllegalArgumentException(s"${json} must be 'unknown' when the capability is unknown.")
    }
    case _ ⇒ throw new IllegalArgumentException(s"${capabilityType.toString} is an unrecognized sensor capability.")
  }
  
  
  /** [[SensorValue]] holding a [[Temperature]] */
  case class TemperatureValue(value : Temperature) extends SensorValue { type T = Temperature }
  private def temperatureValue(json : String) = {
    val parsedJson : Int = 
      try (Integer.parseInt(json))
      catch {
        case _ : Throwable => 
          throw new IllegalArgumentException(s"${json} is not a valid temperature and cannot be used as a sensor value")
      }
    
    TemperatureValue(Temperature(parsedJson))
  }
  
  /** [[SensorValue]] holding a `Boolean` */
  case class BooleanValue(value : Boolean) extends SensorValue { type T = Boolean }
  private def booleanValue(json : String) = {
    val value : Boolean = 
      if (json != "true" && json != "false") 
          throw new IllegalArgumentException(s"${json} is not a valid boolean and cannot be used as a sensor value")
      else (json == "true")
    
    BooleanValue(value)
  }
  
  /** [[SensorValue]] holding an `Int` */
  case class IntValue(value : Int) extends SensorValue { type T = Int }
  private def intValue(json : String) = {
    val value : Int = 
      try (Integer.parseInt(json))
      catch {
        case _ : Throwable => 
          throw new IllegalArgumentException(s"${json} is not a valid integer and cannot be used as a sensor value")
      }
    IntValue(value)
  }
  
  /** [[SensorValue]] holding a `String` */
  case class StringValue(value : String) extends SensorValue { type T = String }
  private def stringValue(json : String) = StringValue(json)
}