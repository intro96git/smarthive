package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.client.AccountID
import com.kelvaya.ecobee.client.Querystrings
import com.kelvaya.ecobee.client.Querystrings.GrantType
import com.kelvaya.ecobee.client.ClientSettings
import com.kelvaya.ecobee.client.tokens.TokenStorage



/** Initial authorization request against the Ecobee API.
  *
  * Returns a new set of tokens for subsequent API request.  These tokens ''must be stored locally'', otherwise a new
  * initial authorization request will be required to be executed again (which will also require the end-user to re-authorize
  * the application using the Ecobee Web Portal)
  *
  * @note This should not be created directly.  Instead, use the service object, [[InitialTokensService$]]
  *
  * @param account The ID of the account for which the token request will be made
  * @param s (implicit) The application global settings (from dependency injection, `DI`)
  *
  * @see [[com.kelvaya.ecobee.client.DI]]
  */
class InitialTokensRequest(override protected val account: AccountID)(implicit s : ClientSettings.Service[Any]) extends TokensRequest(account) {
  final def authTokenQS : TokenStorage.IO[Option[Querystrings.Entry]] = this.getAuthCodeQS
  final def grantTypeQS : Querystrings.Entry = GrantType.Pin
}


// ---------------------


/** Service for initial authorization request against the Ecobee API.
  *
  * @param account The ID of the account for which the token request will be made
  * @param s (implicit) The application global settings (from dependency injection, `DI`)
  *
  * @example
{{{
  val tokenResponse = InitialTokensService.execute(account)
}}}
  * @see [[InitialTokensRequest]]
  */
object InitialTokensService {

  /** Implicitly converts the object into a [[TokensService]] for the [[InitialTokensRequest]] request
    *
    * This allows the syntax, `InitialTokensService.execute`, to work instead of having to create both
    * an `InitialTokensRequest` and pass it explicitly to a new `InitialTokensServiceImpl`.
    */
  implicit class InitialTokensServiceImpl(o : InitialTokensService.type) extends TokensService[InitialTokensRequest] {
    def newTokenRequest(account: AccountID)(implicit s : ClientSettings.Service[Any]) = new InitialTokensRequest(account)
  }
}