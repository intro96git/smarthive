package com.kelvaya.ecobee.client

import spray.json.DefaultJsonProtocol._
import spray.json._

object Technician {
  implicit val TechnicianFormat = DefaultJsonProtocol.jsonFormat10(Technician.apply)
}

/** The technician associated with the [[Thermostat]]
  *
  * @param contractorRef Unique identifier for this contractor.
  * @param name The company name of the technician.
  * @param phone The technician's contact phone number.
  * @param streetAddress The technician's street address.
  * @param city The technician's city.
  * @param provinceState The technician's State or Province.
  * @param country The technician's country.
  * @param postalCode The technician's ZIP or Postal Code.
  * @param email The technician's email address.
  * @param web The technician's web site.
  *
  */
case class Technician(
    contractorRef : String,
    name :          String,
    phone :         String,
    streetAddress : String,
    city :          String,
    provinceState : String,
    country :       String,
    postalCode :    String,
    email :         String,
    web :           String
)