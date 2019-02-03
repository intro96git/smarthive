package com.kelvaya.ecobee.client.service

import spray.json._

object RevisionListItem {

  /** Return new [[RevisionList]] from the given [[CSV]].
    *
    * @note The CSV must contain exactly 7 values to be accepted.
    */
  def fromCSV(csv : CSV) : RevisionListItem = {
    val lov = csv.value.split(CSV.Delimiter)
    if (lov.size != 7) throw new IllegalArgumentException(s"Revision lists expect 7 values.  ${csv} only contains ${lov.size}.")
    RevisionListItem(
      thermoId         = lov(0),
      thermoName       = if (lov(1).isEmpty()) None else Some(lov(1)),
      connected        = lov(2).toBoolean,
      thermoRevision   = lov(3),
      alertsRevision   = lov(4),
      runtimeRevision  = lov(5),
      intervalRevision = lov(6)
    )
  }


  /** JSON Serialization of [[RevisionListItem]] */
  implicit object RevisionListItemFormatter extends RootJsonFormat[RevisionListItem] {
    def read(json: JsValue): RevisionListItem = {
      json match {
        case j : JsString => {
          try fromCSV(CSV(j.value))
          catch { case ex : Throwable => deserializationError(s"${json} is not a valid Revision List", ex) }
        }
        case _ => throw new DeserializationException(s"${json} is not a valid Revision List")
      }
    }

    def write(obj: RevisionListItem): JsValue = {
      val csvVals = Seq(
          obj.thermoId,
          obj.thermoName.getOrElse(""),
          obj.connected.toString,
          obj.thermoRevision,
          obj.alertsRevision,
          obj.runtimeRevision,
          obj.intervalRevision
      )

      CSV(csvVals).toJson
    }
  }

}



/** Data revision as reported by Ecobee API for a specific thermostat
  *
  * This is normally instantiated from [[CSV]] data returned by the Ecobee API that is sent
  * to the [[RevisionList$#apply]] method.
  *
  * @param thermoId The thermostat identifier.
  * @param thermoName The thermostat name, otherwise an empty field if one is not set.
  * @param connected Whether the thermostat is currently connected to the ecobee servers.
  * @param thermoRevision Current thermostat revision. This revision is incremented whenever the thermostat program, hvac mode, settings or configuration change. Changes to the following objects will update the thermostat revision: Settings, Program, Event, Device.
  * @param alertsRevision Current revision of the thermogstat alerts. This revision is incremented whenever a new Alert is issued or an Alert is modified (acknowledged or deferred).
  * @param runtimeRevision The current revision of the thermostat runtime settings. This revision is incremented whenever the thermostat transmits a new status message, or updates the equipment state or Remote Sensor readings. The shortest interval this revision may change is 3 minutes.
  * @param intervalRevision The current revision of the thermostat interval runtime settings. This revision is incremented whenever the thermostat transmits a new status message in the form of a Runtime object. The thermostat updates this on a 15 minute interval.
  */
case class RevisionListItem(
    thermoId :         String,
    thermoName :       Option[String],
    connected :        Boolean,
    thermoRevision :   String,
    alertsRevision :   String,
    runtimeRevision :  String,
    intervalRevision : String
)
