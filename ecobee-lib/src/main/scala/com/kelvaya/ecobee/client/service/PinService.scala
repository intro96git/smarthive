package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.client._
import com.kelvaya.ecobee.client.Querystrings._
import com.kelvaya.ecobee.client.RequestExecutor
import com.kelvaya.ecobee.config.Settings
import com.kelvaya.ecobee.client.storage.TokenStorage

import scala.language.higherKinds

import spray.json.DefaultJsonProtocol
import spray.json.DefaultJsonProtocol._

import akka.http.scaladsl.model.Uri

import cats.Monad

import monix.eval.Coeval

/** Contains constants used by [[PinRequest]] */
object PinRequest {
  val Endpoint = Uri.Path("/authorize")
}

/** Initial registration request against the Ecobee API.
  *
  * Returns a new PIN which can be used to request a new set of tokens for subsequent API request.
  * To use the PIN, call [[InitialTokensService]].
  *
  * @note This should not be created directly.  Instead, use the service object, [[PinRequestService$]]
  *
  * @param account The ID of the account for which the token request will be made
  * @param tokenStore The store of all API tokens
  * @param settings (implicit) The application global settings (from dependency injection, `DI`)
  *
  * @tparam F The monad type that will hold the request
  *
  * @see [[com.kelvaya.ecobee.config.DI]]
  */
class PinRequest[F[_] : Monad](override val account: AccountID, override val tokenStore : Coeval[TokenStorage[F]])(implicit settings: Settings)
extends RequestNoEntity[F](account, tokenStore) {
  val uri = PinRequest.Endpoint
  val query = Coeval.pure(this.tokenIO.pure(ResponseType.EcobeePIN :: ClientId :: Scope.SmartWrite :: Nil))
}


// ---------------------


/** Contains implicit values for JSON serialization of [[PinResponse]] */
object PinResponse {
  implicit val format = DefaultJsonProtocol.jsonFormat5(PinResponse.apply)
}

/** Response from an API [[PinRequest]]
  *
  * @param ecobeePin The PIN that the user must enter on their Ecobee web portal
  * @param expires_in Number of minutes before PIN expires
  * @param code The returned authorization token
  * @param scope The scope of the original request
  * @param interval The minimum amount of seconds which must pass between polling attempts for a token
  */
case class PinResponse(ecobeePin : String, expires_in : Int, code : String, scope : PinScope, interval : Int)


// ---------------------


/** Service for initial PIN request against the Ecobee API.
  *
  * @param account The ID of the account for which the token request will be made
  * @param tokenStore The store of all API tokens
  * @param settings (implicit) The application global settings (from dependency injection, `DI`)
  * @tparam F The monad containing the request
  * @tparam M The container type that will hold results (from dependency injection, `DI`)
  *
  * @example
{{{
  val pinResponse = PinService.execute(account, tokenStore)
}}}
  */
  object PinService {
  implicit class PinServiceImpl[F[_] : Monad,M[_]](o : PinService.type) extends EcobeeJsonService[F,M,PinRequest[F],PinResponse] {
    def execute(account: AccountID, tokenStore: Coeval[TokenStorage[F]])(implicit e : RequestExecutor[F,M], s : Settings): M[Either[ServiceError, PinResponse]] = 
      this.execute(new PinRequest(account, tokenStore))
  }
}
