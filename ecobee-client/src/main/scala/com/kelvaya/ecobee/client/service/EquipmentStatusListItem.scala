package com.kelvaya.ecobee.client.service

import spray.json._

import com.typesafe.scalalogging.Logger


object EquipmentStatusListItem {

  private lazy val _log = Logger[EquipmentStatusListItem]


  /** Return new [[EquipmentStatusListItem]] from the given [[CSV]].
    *
    * @note The CSV must contain exactly 2 values to be accepted.
    */
  def fromCSV(csv : CSV) : EquipmentStatusListItem = {
    val lov = csv.value.split(CSV.Delimiter, 2)
    if (lov.size != 2) throw new IllegalArgumentException(s"Equipment status lists expect 2 values.  ${csv} contains ${lov.size}.")
    EquipmentStatusListItem(
      thermoId  = if (lov(0).size > 0) lov(0) else throw new IllegalArgumentException("Bad equipment status item; thermostats cannot have a blank ID"),
      equipment = parseEquipment(lov(1))
    )
  }


  private def parseEquipment(equip : String) = {
    val (good,bad) = equip.split(",").partition(s => Equipment.values.exists(_.toString == s))

    if (bad.size > 0) {
      val errorMsg = "Unrecognized equipment returned by the Ecobee API: %s".format(bad.mkString(","))
      _log.warn(errorMsg)
    }

    (good map Equipment.withName).toIterable
  }


  /** JSON serialization for [[EquipmentStatusListItem]] */
  implicit val EquipStatusListItemFormatter = new RootJsonFormat[EquipmentStatusListItem] {
    def read(json: JsValue): EquipmentStatusListItem = {
      json match {
        case j : JsString => {
          try fromCSV(CSV(j.value))
          catch {
            case e : Throwable => deserializationError(s"${json} is not a valid Equipment Status entry", e)
          }
        }
        case _ => deserializationError(s"${json} is not a valid Equipment Status entry")
      }
    }

    def write(obj: EquipmentStatusListItem): JsValue = {
      val csvVals = Seq(obj.thermoId, obj.equipment.mkString(","))
      CSV(csvVals).toJson
    }
  }
}



/** Status of equipment as reported by Ecobee API for a specific thermostat
  *
  * This is normally instantiated from [[CSV]] data returned by the Ecobee API that is sent
  * to the [[EquipmentStatusListItem$#fromCSV]] method.
  *
  * @param thermoId The thermostat identifier.
  * @param equipment Equipment currently running
  */
case class EquipmentStatusListItem(
    thermoId :  String,
    equipment : Iterable[Equipment.Value]
)
