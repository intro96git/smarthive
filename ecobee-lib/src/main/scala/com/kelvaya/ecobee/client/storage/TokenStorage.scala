package com.kelvaya.ecobee.client.storage

import com.kelvaya.ecobee.client.AccountID

import scala.language.higherKinds

/** Implementations of `TokenStorage` store and retrieve Ecobee API tokens to be used in requests by the [[Client]]
  *
  * @tparam F The tagless-final-style type holding all return values of the storage class operations
  */
trait TokenStorage[F[_]] {

  /** Returns the currently stored [[Tokens]] used for authorizing against the Ecobee API */
  def getTokens(account : AccountID) : F[Either[TokenStorageError,Tokens]]

  /** Returns a `TokenStorage` with all tokens updated to the given value */
  def storeTokens(account : AccountID, tokens : Tokens) : F[Either[TokenStorageError,Unit]]


  /** Shut down the storage connection.
    *
    * @note Access to the same instance after calling this method results in
    * undefined behavior.
    */
  def close() : F[Either[TokenStorageError,Unit]]
}



/** Error encountered while accessing the [[TokenStorage]] */
sealed trait TokenStorageError extends RuntimeException
object TokenStorageError {
  /** Cannot establish connection to Token storage */
  final val ConnectionError : TokenStorageError = new TokenStorageError {}

  /** Cannot find the desired token in storage */
  final val MissingTokenError : TokenStorageError = new TokenStorageError {}

  /** Given account is not a valid account in the token storage */
  final val InvalidAccountError : TokenStorageError = new TokenStorageError {}
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
