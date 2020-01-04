package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.client.AccountID
import com.kelvaya.ecobee.client.Querystrings
import com.kelvaya.ecobee.client.Querystrings.GrantType
import com.kelvaya.ecobee.client.ClientSettings
import com.kelvaya.ecobee.client.tokens.Tokens
import com.kelvaya.ecobee.client.tokens.TokenStorage
import com.kelvaya.ecobee.client.tokens.TokenStorageError



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
  * @see [[com.kelvaya.ecobee.client.DI]]
  */
class RefreshTokensRequest(override val account: AccountID)(implicit s : ClientSettings.Service[Any]) extends TokensRequest(account) {
  final def authTokenQS : TokenStorage.IO[Option[Querystrings.Entry]] = this.getRefreshTokenQS.map(Some(_))
  final def grantTypeQS : Querystrings.Entry = GrantType.RefreshToken

  /** Returns the refresh token querystring parameter used during token refreshes */
  private def getRefreshTokenQS: TokenStorage.IO[Querystrings.Entry] = {
    for {
      ts  <-  zio.ZIO.environment[TokenStorage]
      tok <-  ts.tokenStorage.getTokens(account)
      qs  <-  zio.IO.fromEither { tok match {
                case Tokens(_, _, Some(token)) => Right((("refresh_token", token)))
                case _                         => Left(TokenStorageError.MissingTokenError)
              }}
    } yield qs
  }

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
    def newTokenRequest(account: AccountID)(implicit s : ClientSettings.Service[Any]) = new RefreshTokensRequest(account)
  }
}
