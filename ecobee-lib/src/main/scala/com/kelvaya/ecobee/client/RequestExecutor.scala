package com.kelvaya.ecobee.client

import com.kelvaya.ecobee.client.service.ServiceError
import com.kelvaya.util.Realizer

import scala.concurrent.ExecutionContext
import scala.language.higherKinds

import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.headers.Authorization
import monix.eval.Task
import spray.json.JsonFormat
import akka.http.scaladsl.model.headers.OAuth2BearerToken
import akka.http.scaladsl.server.AuthorizationFailedRejection
import cats.Monad
import cats.data.OptionT
import cats.data.EitherT


/** Executes HTTP requests to the Ecobee API */
trait RequestExecutor[M[_]] {

  /** Return a new Akka `Authorization` OAuth Bearer Token HTTP header */
  def generateAuthorizationHeader(implicit io : Monad[M]): EitherT[M,RequestError,Authorization] = {
    val header = io.map(getAccessToken.value) {
      _.map(t => Right[RequestError,Authorization](Authorization(OAuth2BearerToken(t))))
       .getOrElse(Left[RequestError,Authorization](RequestError.MissingTokenError))
    }
    EitherT(header)
  }

  /** Return the Ecobee application key used for authorization against the API */
  def getAppKey: String

  /** Return the Ecobee authorization code used for authorization against the API for this client */
  def getAuthCode: OptionT[M,String]

  /** Return the current access token used for authorization against the API */
  def getAccessToken: OptionT[M,String]

  /** Return the refresh token used to generate new authorization tokens for the API */
  def getRefreshToken: OptionT[M,String]


  /** Return the results of executing an HTTP request.
    *
    * @define S S
    *
    * @note The results will be either an error or the return payload.  This is encapsulated
    * as an `Either` of [[com.kelvaya.ecobee.client.service.ServiceError ServiceError]] or an object of type $S.
    *
    * @param req The HTTP Request to execute
    *
    * @tparam T The `Realizer` type that will contain the result of the request execution
    * @tparam S The return payload type (must be in the typeclass of `JsonFormat` used to deserialize it)
    *
    */
  def executeRequest[S : JsonFormat](req : EitherT[M,RequestError,Task[HttpRequest]]) : EitherT[M,ServiceError,S]
}

sealed trait RequestError
object RequestError {
  val MissingTokenError : RequestError = new RequestError {}
}
