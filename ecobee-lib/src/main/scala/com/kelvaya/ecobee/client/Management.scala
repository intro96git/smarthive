package com.kelvaya.ecobee.client

import spray.json.DefaultJsonProtocol
import spray.json.DefaultJsonProtocol._

object Management {
  implicit val ManagementFormat = DefaultJsonProtocol.jsonFormat8(Management.apply)
}

/** The management company to which the [[Thermostat]] belongs.
  *
  * @param administrativeContact The administrative contact name.
  * @param billingContact The billing contact name.
  * @param name The company name.
  * @param phone The phone number.
  * @param email The contact email address.
  * @param web The company web site.
  * @param showAlertIdt Whether to show management alerts on the thermostat.
  * @param showAlertWeb Whether to show management alerts in the web portal.
  */
case class Management(administrativeContact : String, billingContact : String, name : String, phone : String,
                      email : String, web : String, showAlertIdt : Boolean, showAlertWeb : Boolean)