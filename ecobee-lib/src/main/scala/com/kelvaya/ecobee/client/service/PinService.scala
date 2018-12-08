package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.client.RequestExecutor
import com.kelvaya.ecobee.config.Settings
import com.kelvaya.ecobee.client.Querystrings._
import com.kelvaya.ecobee.client.Request
import akka.http.scaladsl.model.HttpResponse
import com.kelvaya.ecobee.client._
import com.kelvaya.ecobee.client.Realizer
import spray.json.DefaultJsonProtocol
import spray.json.DefaultJsonProtocol._

import scala.language.higherKinds

class PinRequest(implicit exec: RequestExecutor, settings: Settings) extends Request {
  val uri = settings.PinRequestUri
  val query = ResponseType.EcobeePIN :: ClientId :: Scope.SmartWrite :: Nil
  val entity = None
}



object PinResponse {
  implicit val format = DefaultJsonProtocol.jsonFormat5(PinResponse.apply)
}
case class PinResponse(ecobeePin : String, expires_in : Int, code : String, scope : PinScope, interval : Int)

object PinService extends EcobeeJsonService[PinRequest,PinResponse] {
  def execute[R[_]](implicit r: Realizer[R], c: Client, e : RequestExecutor, s : Settings): R[Either[HttpResponse, PinResponse]] = execute(new PinRequest())
}
