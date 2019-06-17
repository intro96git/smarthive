package com.kelvaya.ecobee.client

import spray.json._
import spray.json.DefaultJsonProtocol._

object SecuritySettings {
  implicit val SecuritySettingsFormat = DefaultJsonProtocol.jsonFormat6(SecuritySettings.apply)
}

/** The security settings which a thermostat may have.
  *
  * This is specifically for utilities, or other situations which require implicit authorization.  See Ecobee docs for
  * an explanation: https://www.ecobee.com/home/developer/api/documentation/v1/auth/auth-intro.shtml
  *
  * @param userAccessCode The 4-digit user access code for the thermostat.
  * @param allUserAccess The flag for determing whether there are any restrictions on the thermostat regarding access control.
  * @param programAccess The flag for determing whether there are any restrictions on the thermostat regarding access control to the Thermostat.Program.
  * @param detailsAccess The flag for determing whether there are any restrictions on the thermostat regarding access control to the Thermostat system and settings.
  * @param quickSaveAccess The flag for determing whether there are any restrictions on the thermostat regarding access control to the Thermostat quick save functionality.
  * @param vacationAccess The flag for determing whether there are any restrictions on the thermostat regarding access control to the Thermostat vacation functionality.
  */
case class SecuritySettings(userAccessCode : Option[String], allUserAccess : Option[Boolean], programAccess : Option[Boolean],
                            detailsAccess : Option[Boolean], quickSaveAccess : Option[Boolean], vacationAccess : Option[Boolean])
                            extends WriteableApiObject