package com.kelvaya.ecobee.client

import scala.language.higherKinds

import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.headers.Authorization
import spray.json.JsonFormat
import com.kelvaya.ecobee.client.service.ServiceError
import com.kelvaya.util.Realizer


/** Executes HTTP requests to the Ecobee API */
trait RequestExecutor {

  /** Return a new [[Authorization]] HTTP header */
  def generateAuthorizationHeader: Authorization

  /** Return the Ecobee application key used for authorization against the API */
  def getAppKey: String

  /** Return the Ecobee authorization code used for authorization against the API for this client */
  def getAuthCode: Option[String]

  /** Return the current access token used for authorization against the API */
  def getAccessToken: Option[String]

  /** Return the refresh token used to generate new authorization tokens for the API */
  def getRefreshToken: Option[String]


  /** Return the results of executing an HTTP request.
    *
    * @note The results will be either an error or the return payload.  This is encapsulated
    * as an [[Either]] of [[ServiceError]] or an object of type ${S}.
    *
    * @param req The HTTP Request to execute
    *
    * @tparam T The `Realizer` type that will contain the result of the request execution
    * @tparam S The return payload type (must be in the typeclass of `JsonFormat` used to deserialize it)
    *
    * @define S S
    */
  def executeRequest[T[_] : Realizer,S : JsonFormat](req : HttpRequest) : T[Either[ServiceError,S]]
}
