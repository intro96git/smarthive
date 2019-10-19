package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.client.AccountID
import com.kelvaya.ecobee.client.Querystrings
import com.kelvaya.ecobee.client.Querystrings.GrantType
import com.kelvaya.ecobee.config.Settings
import com.kelvaya.ecobee.client.tokens.TokenStorage



/** Requests a new set of tokens for the Ecobee API.
  *
  * Returns a new set of access tokens for subsequent API request by using the "refresh" token.  These new tokens ''must be stored locally'', otherwise a new
  * initial authorization request will be required to be executed again (which will also require the end-user to re-authorize
  * the application using the Ecobee Web Portal)
  *
  * @note This should not be created directly.  Instead, use the service object, [[RefreshTokensService$]]
  *
  * @param account The ID of the account for which the token request will be made
  * @param s (implicit) The application global settings (from dependency injection, `DI`)
  *
  * @see [[com.kelvaya.ecobee.config.DI]]
  */
class RefreshTokensRequest(override val account: AccountID)(implicit s : Settings) extends TokensRequest(account) {
  final def authTokenQS : TokenStorage.IO[Option[Querystrings.Entry]] = this.getRefreshTokenQS.map(Some(_))
  final def grantTypeQS : Querystrings.Entry = GrantType.RefreshToken
}

// ---------------------

/** Service for requesting new access tokens against the Ecobee API.
  *
  * @param account The ID of the account for which the token request will be made
  * @param s (implicit) The application global settings (from dependency injection, `DI`)
  *
  * @example
{{{
  val tokenResponse = RefreshTokensService.execute(account)
}}}
  *
  * @see [[RefreshTokensRequest]]
  */
object RefreshTokensService {
  implicit class RefreshTokensServiceImpl(o : RefreshTokensService.type) extends TokensService[RefreshTokensRequest] {
    def newTokenRequest(account: AccountID)(implicit s : Settings) = new RefreshTokensRequest(account)
  }
}
