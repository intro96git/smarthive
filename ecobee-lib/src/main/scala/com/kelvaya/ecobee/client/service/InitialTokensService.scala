package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.client.Querystrings
import com.kelvaya.ecobee.client.Querystrings.GrantType
import com.kelvaya.ecobee.client.RequestExecutor
import com.kelvaya.ecobee.config.Settings

import scala.language.higherKinds

import cats.Monad
import cats.data.OptionT

/** Initial authorization request against the Ecobee API.
  *
  * Returns a new set of tokens for subsequent API request.  These tokens ''must be stored locally'', otherwise a new
  * initial authorization request will be required to be executed again (which will also require the end-user to re-authorize
  * the application using the Ecobee Web Portal)
  *
  * @note This should not be created directly.  Instead, use the service object, [[InitialTokensService$]]
  *
  * @param e (implicit) The executor responsible for sending the request to the Ecobee API (from dependency injection, `DI`)
  * @param s (implicit) The application global settings (from dependency injection, `DI`)
  *
  * @tparam M The monad container type that will hold results  (from dependency injection, `DI`)
  *
  * @see [[com.kelvaya.ecobee.config.DI]]
  */
class InitialTokensRequest[M[_] : Monad](implicit e : RequestExecutor[M], s : Settings) extends TokensRequest[M] {
  final def authTokenQs : OptionT[M,Querystrings.Entry] = this.getAuthCodeQs
  final def grantTypeQs : Querystrings.Entry = GrantType.Pin
}


// ---------------------


/** Service for initial authorization request against the Ecobee API.
  *
  * @tparam M The monad container type that will hold results (from dependency injection, `DI`)
  *
  * @example
{{{
  val tokenResponse = InitialTokensSerivce.execute
}}}
  */
object InitialTokensService {

  /** Implicitly converts the object into a [[TokensService]] for the [[InitialTokensRequest]] request
    *
    * This allows the syntax, `InitialTokensService.execute`, to work instead of having to create both
    * an `InitialTokensRequest` and pass it explicitly to a new `InitialTokensServiceImpl`.
    */
  implicit class InitialTokensServiceImpl[M[_] : Monad](o : InitialTokensService.type) extends TokensService[M,InitialTokensRequest[M]] {
    def newTokenRequest(implicit e : RequestExecutor[M], s : Settings) = new InitialTokensRequest
  }
}