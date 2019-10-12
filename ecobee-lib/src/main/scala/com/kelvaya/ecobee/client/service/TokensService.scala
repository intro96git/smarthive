package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.client.AccountID
import com.kelvaya.ecobee.client.ParameterlessApi
import com.kelvaya.ecobee.client.PinScope
import com.kelvaya.ecobee.client.PostRequest
import com.kelvaya.ecobee.client.Request
import com.kelvaya.ecobee.client.RequestExecutor
import com.kelvaya.ecobee.client.TokenType
import com.kelvaya.ecobee.config.Settings

import scala.language.higherKinds

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.Uri
import spray.json.DefaultJsonProtocol
import cats.Monad

abstract class TokensRequest[M[_] : Monad](override val account: AccountID)(implicit e : RequestExecutor[M], s : Settings) 
extends Request[M,ParameterlessApi](account) with PostRequest[M,ParameterlessApi] {
  import com.kelvaya.ecobee.client.Querystrings._

  val entity = None
  val uri = Uri.Path("/token")
  val query: M[List[Entry]] =
    async.map(this.authTokenQs) { tks => this.grantTypeQs :: ClientId :: (tks map { _ :: Nil } getOrElse Nil) }

  protected def grantTypeQs : Entry
  protected def authTokenQs : M[Option[Entry]]
}


// ---------------------


object TokensResponse extends DefaultJsonProtocol {
  implicit val format = DefaultJsonProtocol.jsonFormat5(TokensResponse.apply)
}
case class TokensResponse(access_token : String, token_type : TokenType, expires_in : Int, refresh_token : String, scope : PinScope)


// ---------------------


abstract class TokensService[M[_] : Monad, T <: TokensRequest[M]] extends EcobeeJsonService[M, T,TokensResponse] {

  def execute(account: AccountID)(implicit e : RequestExecutor[M], s : Settings) : M[Either[ServiceError, TokensResponse]] =
    this.execute(this.newTokenRequest(account))

  protected def newTokenRequest(account: AccountID)(implicit e : RequestExecutor[M], s : Settings) : T
}
