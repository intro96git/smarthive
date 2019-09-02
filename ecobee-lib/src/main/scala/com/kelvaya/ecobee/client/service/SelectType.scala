package com.kelvaya.ecobee.client.service

import spray.json._


object SelectType {

  /** Returns a [[SelectType]] from the selectionType and selectionMatch strings.
    *
    * These two strings normally come from the JSON representation of a `SelectionType`.
    */
  def create(selectionType : String, selectionMatch : String) : SelectType = {
    selectionType match {
      case "thermostats"   ⇒ Thermostats
      case "registered"    ⇒ new Registered(selectionMatch.split(",") : _*)
      case "managementSet" ⇒ new ManagementSet(selectionMatch)
    }
  }

  /** Return thermostats registered to the current user.
    *
    * This is only usable with Smart thermostats registered to a user.
    */
  case object Thermostats extends SelectType {
    val id = "thermostats"
    val selectionMatch = ""
  }


  /** Select only the given thermostats, [[#thermostats]].
    *
    *  There is a limit of 25 identifiers per request.
    */
  case class Registered(thermostats : String*) extends SelectType {
    val id = "registered"
    val selectionMatch = thermostats.mkString(",")
  }



  /** Selects all thermostats for a given management set defined by the Management/Utility account.
    *
    * @note This is not yet fully implemented
    */
  case class ManagementSet(set : String) extends SelectType {
    val id = "managementSet"
    val selectionMatch = set
  }
}


/** The main type of data to return when querying the Ecobee API.
  *
  * This is used within a [[Select]] instance.
  */
sealed trait SelectType extends Enumeration {

  /** The identifier of the selection type as understood by the Ecobee API.
    *
    * This will be used as the "selectionType" in the JSON payload.
    */
  val id : String


  /** The match string as understood by the Ecobee API.
    *
    * This will be used as the "selectionMatch" in the JSON payload.
    */
  val selectionMatch : String
}
