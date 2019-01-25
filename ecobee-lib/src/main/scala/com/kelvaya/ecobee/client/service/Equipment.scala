package com.kelvaya.ecobee.client.service

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
}
