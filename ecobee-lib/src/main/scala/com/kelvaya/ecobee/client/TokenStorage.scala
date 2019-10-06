package com.kelvaya.ecobee.client

import scala.language.higherKinds

/** Implementations of `TokenStorage` store and retrieve Ecobee API tokens to be used in requests by the [[Client]]
  *
  * @tparam T The container holding all return values of the storage class operations
  */
trait TokenStorage[F[_]] {

  /** Must be a self-type to assist the Scala compiler in the definition of the `storeTokens` method. */
  type Self <: TokenStorage[F]

  /** Returns the currently stored [[Tokens]] used for authorizing against the Ecobee API */
  def getTokens() : F[Tokens]

  /** Returns a `TokenStorage` with all tokens updated to the given value */
  def storeTokens(tokens : Tokens) : F[Self]
}



/** Tokens used for authorization against the Ecobee API.
  *
  *  Used by [[TokenStorage]]
  *
  *  @param authorizationToken Used to get a new set of access tokens and refresh tokens in the [[com.kelvaya.ecobee.client.service.PinService PinService]]
  *  @param accessToken Authorization token used in almost all requests of the Ecobee API
  *  @param refreshToken Token used to get a new access token
  */
final case class Tokens(authorizationToken : Option[String], accessToken : Option[String], refreshToken : Option[String])
