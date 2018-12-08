package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.client.Querystrings
import com.kelvaya.ecobee.client.Request
import com.kelvaya.ecobee.client.RequestExecutor
import com.kelvaya.ecobee.config.Settings

import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.model.HttpResponse
import com.kelvaya.ecobee.client.Realizer
import com.kelvaya.ecobee.client.Client

import scala.language.higherKinds
import com.kelvaya.ecobee.client.PinScope
import com.kelvaya.ecobee.client.TokenType
import spray.json.DefaultJsonProtocol
import spray.json.JsonFormat
import com.kelvaya.ecobee.client.PostRequest

class InitialTokensRequest(implicit e : RequestExecutor, s : Settings) extends PostRequest {
  import Querystrings._

  val entity: Option[String] = None
  val query: List[Querystring] = {
    val qs = GrantType.Pin :: ClientId :: Nil
    this.getAuthCodeQs map { _ :: qs } getOrElse qs
  }
  val uri = Uri.Path("/token")
}


object InitialTokensResponse extends DefaultJsonProtocol {
  implicit val format = DefaultJsonProtocol.jsonFormat5(InitialTokensResponse.apply)
}
case class InitialTokensResponse(access_token : String, token_type : TokenType, expires_in : Int, refresh_token : String, scope : PinScope)


object InitialTokensService extends EcobeeJsonService[InitialTokensRequest,InitialTokensResponse] {
  def execute[R[_]](implicit r: Realizer[R], c: Client, e : RequestExecutor, s : Settings) : R[Either[HttpResponse, InitialTokensResponse]] =
    this.execute(new InitialTokensRequest)
}