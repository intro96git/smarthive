package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.client._
import com.kelvaya.ecobee.client.Querystrings._
import com.kelvaya.ecobee.client.ClientSettings

import spray.json.DefaultJsonProtocol
import spray.json.DefaultJsonProtocol._

import zio.UIO
import zio.ZIO


/** Contains constants used by [[PinRequest]] */
object PinRequest {
  val Endpoint = Uri("/authorize")
}

/** Initial registration request against the Ecobee API.
  *
  * Returns a new PIN which can be used to request a new set of tokens for subsequent API request.
  * To use the PIN, call [[InitialTokensService]].
  *
  * @note This should not be created directly.  Instead, use the service object, [[PinService$]]
  *
  * @param account The ID of the account for which the token request will be made
  * @param settings (implicit) The application global settings (from dependency injection, `DI`)
  *
  * @see [[com.kelvaya.ecobee.client.DI]]
  */
class PinRequest(implicit settings: ClientSettings.Service[Any]) extends RequestNoEntity {
  val uri = PinRequest.Endpoint
  val query = UIO(ResponseType.EcobeePIN :: ClientId :: Scope.SmartWrite :: Nil)
  val queryBody = zio.UIO.succeed(None)
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
  * @param settings (implicit) The application global settings (from dependency injection, `DI`)
  *
  * @example
{{{
  val pinResponse = PinService.execute(account)
}}}
  */
object PinService {
  implicit class PinServiceImpl(o : PinService.type) extends EcobeeJsonAuthService[PinRequest,PinResponse] {
    def execute(implicit s : ClientSettings.Service[Any]): ZIO[ClientEnv,ServiceError,PinResponse] = 
      this.execute(new PinRequest)
  }
}
