package com.kelvaya.ecobee.client.service

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
import cats.data.EitherT
import cats.Monad
import cats.data.OptionT

abstract class TokensRequest[M[_] : Monad](implicit e : RequestExecutor[M], s : Settings) extends PostRequest[M,ParameterlessApi] {
  import com.kelvaya.ecobee.client.Querystrings._

  val entity = None
  val uri = Uri.Path("/token")
  val query: M[List[Entry]] =
    this.containerClass.map(this.authTokenQs.value) { tks => this.grantTypeQs :: ClientId :: (tks map { _ :: Nil } getOrElse Nil) }

  protected def grantTypeQs : Entry
  protected def authTokenQs : OptionT[M,Entry]
}


// ---------------------


object TokensResponse extends DefaultJsonProtocol {
  implicit val format = DefaultJsonProtocol.jsonFormat5(TokensResponse.apply)
}
case class TokensResponse(access_token : String, token_type : TokenType, expires_in : Int, refresh_token : String, scope : PinScope)


// ---------------------


abstract class TokensService[M[_] : Monad, T <: TokensRequest[M]] extends EcobeeJsonService[M, T,TokensResponse] {

  def execute(implicit e : RequestExecutor[M], s : Settings) : EitherT[M, ServiceError, TokensResponse] =
    this.execute(this.newTokenRequest)

  protected def newTokenRequest(implicit e : RequestExecutor[M], s : Settings) : T
}
