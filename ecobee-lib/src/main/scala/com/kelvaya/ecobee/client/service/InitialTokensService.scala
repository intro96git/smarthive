package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.client.AccountID
import com.kelvaya.ecobee.client.Querystrings
import com.kelvaya.ecobee.client.Querystrings.GrantType
import com.kelvaya.ecobee.config.Settings
import com.kelvaya.ecobee.client.storage.TokenStorage

import scala.language.higherKinds

import cats.Monad
import monix.eval.Coeval

/** Initial authorization request against the Ecobee API.
  *
  * Returns a new set of tokens for subsequent API request.  These tokens ''must be stored locally'', otherwise a new
  * initial authorization request will be required to be executed again (which will also require the end-user to re-authorize
  * the application using the Ecobee Web Portal)
  *
  * @note This should not be created directly.  Instead, use the service object, [[InitialTokensService$]]
  *
  * @param account The ID of the account for which the token request will be made
  * @param tokenStore The store of all API tokens
  * @param s (implicit) The application global settings (from dependency injection, `DI`)
  *
  * @tparam M The monad type that will hold the request
  *
  * @see [[com.kelvaya.ecobee.config.DI]]
  */
class InitialTokensRequest[M[_] : Monad](override protected val account: AccountID, override protected val tokenStore : Coeval[TokenStorage[M]])(implicit s : Settings)
extends TokensRequest[M](account, tokenStore) {
  final def authTokenQS : Coeval[M[Option[Querystrings.Entry]]] = this.getAuthCodeQS
  final def grantTypeQS : Querystrings.Entry = GrantType.Pin
}


// ---------------------


/** Service for initial authorization request against the Ecobee API.
  *
  * @param account The ID of the account for which the token request will be made
  * @param tokenStore The store of all API tokens
  * @param s (implicit) The application global settings (from dependency injection, `DI`)
  * @tparam M The monad container type that will hold results (from dependency injection, `DI`)
  *
  * @example
{{{
  val tokenResponse = InitialTokensService.execute(account, tokenStore)
}}}
  * @see [[InitialTokensRequest]]
  */
object InitialTokensService {

  /** Implicitly converts the object into a [[TokensService]] for the [[InitialTokensRequest]] request
    *
    * This allows the syntax, `InitialTokensService.execute`, to work instead of having to create both
    * an `InitialTokensRequest` and pass it explicitly to a new `InitialTokensServiceImpl`.
    */
  implicit class InitialTokensServiceImpl[F[_] : Monad,M[_]](o : InitialTokensService.type) extends TokensService[F,M,InitialTokensRequest[F]] {
    def newTokenRequest(account: AccountID, tokenStore : Coeval[TokenStorage[F]])(implicit s : Settings) = new InitialTokensRequest[F](account, tokenStore)
  }
}