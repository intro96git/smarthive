package com.kelvaya.ecobee.client

import spray.json.DefaultJsonProtocol
import spray.json.RootJsonFormat



/** Any object that can be passed in or returned by the Ecobee API */
trait ApiObject {

  /** [[ApiObject]] that can be used as a parameter to send to the Ecobee API to modify existing values */
  def asWriteable : WriteableApiObject
}


/** Any object that is used by the Ecobee API but cannot modify data */
trait ReadonlyApiObject extends ApiObject {
  final def asWriteable = throw new IllegalArgumentException("This object cannot be used to modify data.")
}


/** An [[ApiObject]] used for POST modification requests. */
trait WriteableApiObject extends ApiObject {
  final def asWriteable = this
}
