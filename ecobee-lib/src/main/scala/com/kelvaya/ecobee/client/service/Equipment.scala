package com.kelvaya.ecobee.client.service

import spray.json._
import spray.json.DefaultJsonProtocol._

object Equipment extends Enumeration {
  val HeatPump = Value("heatPump")
  val HeatPump2 = Value("heatPump2")
  val HeatPump3 = Value("heatPump3")
  val AC1 = Value("compCool1")
  val AC2 = Value("compCool2")
  val AuxHeat1 = Value("auxHeat1")
  val AuxHeat2 = Value("auxHeat1")
  val AuxHeat3 = Value("auxHeat1")
  val Fan = Value("fan")
  val Humidifier = Value("humidifier")
  val Dehumidifier = Value("dehumidifier")
  val Ventilator = Value("ventilator")
  val Economizer = Value("economizer")
  val HotWater = Value("compHotWater")
  val AuxHotWater = Value("auxHotWater")

  implicit object EquipmentFormat extends RootJsonFormat[Equipment.Value] {
    def write(obj : Equipment.Value) : JsValue = obj.toString().toJson
    def read(json : JsValue) : Equipment.Value = {
      try Equipment.withName(json.toString)
      catch {
        case _ : NoSuchElementException ⇒
          throw new NoSuchElementException(s"${json} is not the name of a recognized Ecobee equipment type.")
      }
    }
  }

}
