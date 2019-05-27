package com.kelvaya.ecobee.client



/** Any object that can be passed in or returned by the Ecobee API */
trait ApiObject {

  /** [[ApiObject]] that can be used as a parameter to send to the Ecobee API to modify existing values */
  def asWriteable : WriteableApiObject
}


/** An [[ApiObject]] used for POST modification requests. */
trait WriteableApiObject extends ApiObject {
  final def asWriteable = this
}