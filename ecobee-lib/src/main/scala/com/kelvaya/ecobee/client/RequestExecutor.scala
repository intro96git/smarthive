package com.kelvaya.ecobee.client

import com.kelvaya.ecobee.client.service.ServiceError

import scala.language.higherKinds

import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.headers.Authorization
import akka.http.scaladsl.model.headers.OAuth2BearerToken

import monix.eval.Task

import spray.json.JsonFormat

import cats.Monad



/** Executes HTTP requests to the Ecobee API */
trait RequestExecutor[M[_]] {

  type EitherM[E,A] = M[Either[E,A]]

  /** Return a new Akka `Authorization` OAuth Bearer Token HTTP header */
  def generateAuthorizationHeader(account : AccountID)(implicit io : Monad[M]): EitherM[RequestError,Authorization] = {
    io.map(getAccessToken(account)) { 
      _.flatMap(t => Right(Authorization(OAuth2BearerToken(t))))
    }
  }

  /** Return the Ecobee application key used for authorization against the API */
  def getAppKey: String

  /** Return the Ecobee authorization code used for authorization against the API for this client */
  def getAuthCode(account : AccountID): EitherM[RequestError,String]

  /** Return the current access token used for authorization against the API */
  def getAccessToken(account : AccountID): EitherM[RequestError,String]

  /** Return the refresh token used to generate new authorization tokens for the API */
  def getRefreshToken(account : AccountID): EitherM[RequestError,String]


  /** Return the results of executing an HTTP request.
    *
    * @define S S
    *
    * @note The results will be either an error or the return payload.  This is encapsulated
    * as an `Either` of [[com.kelvaya.ecobee.client.service.ServiceError ServiceError]] or an object of type $S.
    *
    * @usecase def executeRequest[S](req : EitherM[RequestError,Task[HttpRequest]]) : EitherM[ServiceError,S]
    *
    * @param req The HTTP Request to execute
    *
    * @tparam S The return payload type (must be in the typeclass of `JsonFormat` used to deserialize it)
    *
    */
  def executeRequest[S : JsonFormat](req : EitherM[RequestError,Task[HttpRequest]]) : EitherM[ServiceError,S]
}
