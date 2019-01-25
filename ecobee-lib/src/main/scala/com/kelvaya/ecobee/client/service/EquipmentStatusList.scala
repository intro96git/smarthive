package com.kelvaya.ecobee.client.service

import akka.event.Logging
import akka.event.LoggingBus

object EquipmentStatusList {

  /** Return new [[EquipmentStatusList]] from the given [[CSV]].
    *
    * @note The CSV must contain exactly 2 values to be accepted.
    */
  def apply(csv : CSV)(implicit logBus : LoggingBus) : EquipmentStatusList = {
    val lov = csv.value.split(CSV.Delimter)
    if (lov.size != 2) throw new IllegalArgumentException(s"Equipment status lists expect 2 values.  ${csv} contains ${lov.size}.")
    EquipmentStatusList(thermoId         = lov(0),
                        equipment        = parseEquipment(lov(1))
    )
  }

  private def parseEquipment(equip : String)(implicit logBus : LoggingBus) = {
    val (good,bad) = equip.split(",").partition(s => Equipment.values.exists(_.toString == s))

    if (bad.size > 0) {
      val log = Logging(logBus, this.getClass)
      val errorMsg = "Unrecognized equipment returned by the Ecobee API: %s".format(bad.mkString(","))
      log.warning(errorMsg)
    }

    (good map Equipment.withName).toIterable
  }
}

/*
 * Thermostat Identifier   String   The thermostat identifier.
Equipment Status   String   If no equipment is currently running no data is returned. Possible values are: heatPump, heatPump2, heatPump3, compCool1, compCool2, auxHeat1, auxHeat2, auxHeat3, fan, humidifier, dehumidifier, ventilator, economizer, compHotWater, auxHotWater.
 */

/** Status of equipment as reported by Ecobee API for a specific thermostat
  *
  * This is normally instantiated from [[CSV]] data returned by the Ecobee API that is sent
  * to the [[EquipmentStatusList$#apply]] method.
  *
  * @param thermoId The thermostat identifier.
  * @param equipment Equipment currently running
  */
case class EquipmentStatusList(
    thermoId :  String,
    equipment : Iterable[Equipment.Value]
)