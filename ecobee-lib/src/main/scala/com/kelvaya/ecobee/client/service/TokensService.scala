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

abstract class TokensRequest(implicit e : RequestExecutor, s : Settings) extends PostRequest {
  import Querystrings._

  val entity: Option[String] = None
  val query: List[Entry] = this.grantTypeQs :: ClientId :: (this.authTokenQs map { _ :: Nil } getOrElse Nil)
  val uri = Uri.Path("/token")

  protected def grantTypeQs : Entry
  protected def authTokenQs : Option[Entry]
}


// ---------------------


object TokensResponse extends DefaultJsonProtocol {
  implicit val format = DefaultJsonProtocol.jsonFormat5(TokensResponse.apply)
}
case class TokensResponse(access_token : String, token_type : TokenType, expires_in : Int, refresh_token : String, scope : PinScope)


// ---------------------


trait TokensService[T <: TokensRequest] extends EcobeeJsonService[T,TokensResponse] {

  def execute[R[_]](implicit r: Realizer[R], c: Client, e : RequestExecutor, s : Settings) : R[Either[HttpResponse, TokensResponse]] =
    this.execute(this.newTokenRequest)

  protected def newTokenRequest(implicit e : RequestExecutor, s : Settings) : T
}