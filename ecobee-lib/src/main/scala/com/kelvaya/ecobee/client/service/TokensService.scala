package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.client.AccountID
import com.kelvaya.ecobee.client.ParameterlessApi
import com.kelvaya.ecobee.client.PinScope
import com.kelvaya.ecobee.client.PostRequest
import com.kelvaya.ecobee.client.Request
import com.kelvaya.ecobee.client.RequestExecutor
import com.kelvaya.ecobee.client.TokenType
import com.kelvaya.ecobee.client.tokens.TokenStorage
import com.kelvaya.ecobee.client.tokens.TokenStorageError
import com.kelvaya.ecobee.config.Settings

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.Uri

import spray.json.DefaultJsonProtocol

import zio.IO
import zio.ZIO


/** Request for a new set of [[com.kelvaya.ecobee.client.tokens.Tokens tokens]] 
  * 
  * @param account The ID of the account for which the token request will be made
  */ 
abstract class TokensRequest(override protected val account: AccountID)(implicit s : Settings) 
extends Request[ParameterlessApi](account) with PostRequest[ParameterlessApi] {
  import com.kelvaya.ecobee.client.Querystrings._

  val entity = None
  val uri = Uri.Path("/token")
  val query: ZIO[TokenStorage,TokenStorageError, List[Entry]] = this.authTokenQS.map { qs =>
    val list = qs.map(_ :: Nil).getOrElse(List.empty[Entry])
    this.grantTypeQS :: ClientId :: list
  }

  /** The Querystring required to be on the request to request this type of token */
  protected def grantTypeQS : Entry

  /** The Querystring required to authorize the request for a new token */
  protected def authTokenQS : TokenStorage.IO[Option[Entry]]
}


// ---------------------


/** Implicits for JSON serialization of [[TokensResponse]] */
object TokensResponse extends DefaultJsonProtocol {
  implicit val format = DefaultJsonProtocol.jsonFormat5(TokensResponse.apply)
}

/** Response from a [[TokensRequest]]
  * 
  * @param access_token The new access token used to authorize subsequent API requests
  * @param token_type The type of token returned.  Currently supports only "Bearer"
  * @param expires_in The time, in seconds, until the access token expires
  * @param refresh_token The new refresh token that must be used to request future access tokens
  * @param scope The scope of API requests where this token will be valid
  */
case class TokensResponse(access_token : String, token_type : TokenType, expires_in : Int, refresh_token : String, scope : PinScope)


// ---------------------


/** Service to handle requests for news set of [[com.kelvaya.ecobee.client.tokens.Tokens tokens]]  */ 
abstract class TokensService[T <: TokensRequest] extends EcobeeJsonService[T,TokensResponse] {

  /** Return the new tokens by executing the given request
    * 
    * @param account The ID of the account for which the token request will be made
    * @param e (implicit) The executor that will execute the request against the API
    * @param s (implicit) Global application settings
    */
  def execute(account: AccountID)(implicit e : RequestExecutor, s : Settings) : IO[ServiceError, TokensResponse] =
    this.execute(this.newTokenRequest(account))

  /** Create the tokens request for this service
    * 
    * @param account The ID of the account for which the token request will be made
    * @param s (implicit) Global application settings
    */
  protected def newTokenRequest(account: AccountID)(implicit s : Settings) : T
}
