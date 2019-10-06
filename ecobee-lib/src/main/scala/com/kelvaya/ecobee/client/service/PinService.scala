package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.client._
import com.kelvaya.ecobee.client.Querystrings._
import com.kelvaya.ecobee.client.RequestExecutor
import com.kelvaya.ecobee.config.Settings

import scala.language.higherKinds

import spray.json.DefaultJsonProtocol
import spray.json.DefaultJsonProtocol._
import akka.http.scaladsl.model.Uri
import cats.Monad
import cats.data.EitherT


object PinRequest {
  val Endpoint = Uri.Path("/authorize")
}

class PinRequest[M[_] : Monad](implicit exec: RequestExecutor[M], settings: Settings) extends RequestNoEntity[M] {
  val uri = PinRequest.Endpoint
  val query = this.containerClass.pure(ResponseType.EcobeePIN :: ClientId :: Scope.SmartWrite :: Nil)
}


// ---------------------


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


object PinService {
  implicit class PinServiceImpl[M[_] : Monad](o : PinService.type) extends EcobeeJsonService[M,PinRequest[M],PinResponse] {
    def execute(implicit e : RequestExecutor[M], s : Settings): EitherT[M, ServiceError, PinResponse] = this.execute(new PinRequest)
  }
}
