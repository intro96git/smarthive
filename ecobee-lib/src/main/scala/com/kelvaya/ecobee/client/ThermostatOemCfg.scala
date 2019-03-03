package com.kelvaya.ecobee.client

import spray.json.DefaultJsonProtocol

object ThermostatOemCfg {
  implicit val ThermostatOemCfgFormat = DefaultJsonProtocol.jsonFormat0(ThermostatOemCfg.apply)
}

case class ThermostatOemCfg()