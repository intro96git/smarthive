package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.client.AccountID
import com.kelvaya.ecobee.config.Settings

import scala.language.higherKinds

import com.kelvaya.ecobee.client.Querystrings
import com.kelvaya.ecobee.client.Querystrings.GrantType
import com.kelvaya.ecobee.client.storage.TokenStorage

import cats.Monad
import monix.eval.Coeval


/** Requests a new set of tokens for the Ecobee API.
  *
  * Returns a new set of access tokens for subsequent API request by using the "refresh" token.  These new tokens ''must be stored locally'', otherwise a new
  * initial authorization request will be required to be executed again (which will also require the end-user to re-authorize
  * the application using the Ecobee Web Portal)
  *
  * @note This should not be created directly.  Instead, use the service object, [[RefreshTokensService$]]
  *
  * @param account The ID of the account for which the token request will be made
  * @param tokenStore The store of all API tokens
  * @param s (implicit) The application global settings (from dependency injection, `DI`)
  *
  * @tparam M The monad type that will hold the request
  *
  * @see [[com.kelvaya.ecobee.config.DI]]
  */
class RefreshTokensRequest[M[_]:Monad](override val account: AccountID, override val tokenStore: Coeval[TokenStorage[M]])(implicit s : Settings) 
extends TokensRequest[M](account, tokenStore) {
  final def authTokenQS : Coeval[M[Option[Querystrings.Entry]]] = this.getRefreshTokenQS.map { QS => this.tokenIO.map(QS) { Some(_) } }
  final def grantTypeQS : Querystrings.Entry = GrantType.RefreshToken
}

// ---------------------

/** Service for requesting new access tokens against the Ecobee API.
  *
  * @param account The ID of the account for which the token request will be made
  * @param tokenStore The store of all API tokens
  * @param s (implicit) The application global settings (from dependency injection, `DI`)
  * @tparam M The monad container type that will hold results (from dependency injection, `DI`)
  *
  * @example
{{{
  val tokenResponse = RefreshTokensService.execute(account, tokenStore)
}}}
  *
  * @see [[RefreshTokensRequest]]
  */
object RefreshTokensService {
  implicit class RefreshTokensServiceImpl[F[_]:Monad,M[_]](o : RefreshTokensService.type) extends TokensService[F,M,RefreshTokensRequest[F]] {
    def newTokenRequest(account: AccountID, tokenStore : Coeval[TokenStorage[F]])(implicit s : Settings) = new RefreshTokensRequest[F](account, tokenStore)
  }
}
