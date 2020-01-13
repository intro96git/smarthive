package com.kelvaya.ecobee.server


/** Authorization status as returned by [[ApiClient#authorize]] 
  *
  * All valid values are enumerated in [[AuthStatus$]] 
  */
sealed trait AuthStatus

/** Enumeration of [[AuthStatus]] */
object AuthStatus {

  /** Account is already fully authorized */
  object AlreadyAuthorized extends AuthStatus
  
  /** Account PIN has been created.  Awaiting user authorization. */
  object WaitingForAuthorization extends AuthStatus
  
  /** Account has not been authorized by user nor has PIN be created */
  object NeedsAuthorization extends AuthStatus

  /** Account PIN authorization period expired. */
  object RegistrationExpired extends AuthStatus

  /** Authorization succeeded.  Refresh token will be valid for [[#expiresMin]] minutes */
  final case class Succeeded(expiresMin : Int) extends AuthStatus
}