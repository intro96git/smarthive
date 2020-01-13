package com.kelvaya.ecobee.server

/** PIN Registration, as returned by [[ApiClient#register]]
  *
  * @param pin The PIN used to authorize the application.  Must be used by the end-user on the Ecobee site.
  * @param expiration The time, in minutes, before the PIN expires and must be re-registered
  * @param maxPollInterval The minimum amount of time, in seconds, that the API client must wait before polling the servers for the authorization check.
  */ 
final case class Registration(pin : String, expiration : Int, minPollInterval : Int)