package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.client.Client
import com.kelvaya.ecobee.client.ParameterlessApi
import com.kelvaya.ecobee.client.PinScope
import com.kelvaya.ecobee.client.PostRequest
import com.kelvaya.ecobee.client.RequestExecutor
import com.kelvaya.ecobee.client.TokenType
import com.kelvaya.ecobee.config.Settings
import com.kelvaya.util.Realizer

import scala.language.higherKinds

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.Uri
import spray.json.DefaultJsonProtocol

abstract class TokensRequest(implicit e : RequestExecutor, s : Settings) extends PostRequest[ParameterlessApi] {
  import com.kelvaya.ecobee.client.Querystrings._

  val entity = None
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

  def execute[R[_]](implicit r: Realizer[R], c: Client, e : RequestExecutor, s : Settings) : R[Either[ServiceError, TokensResponse]] =
    this.execute(this.newTokenRequest)

  protected def newTokenRequest(implicit e : RequestExecutor, s : Settings) : T
}