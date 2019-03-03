package com.kelvaya.ecobee.client

import spray.json.DefaultJsonProtocol
import spray.json.DefaultJsonProtocol._

object Utility {
  implicit val UtilityFormat = DefaultJsonProtocol.jsonFormat4(Utility.apply)
}


/** The Utility associated with the [[Thermostat]]
  *
  *  @param name The Utility company name.
  * @param phone The Utility company contact phone number.
  * @param email The Utility company email address.
  * @param web The Utility company web site.
  *
  */
case class Utility(name : String, phone : String, email : String, web : String)
