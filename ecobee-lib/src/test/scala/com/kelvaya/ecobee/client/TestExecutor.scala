package com.kelvaya.ecobee.client

import com.kelvaya.ecobee.test.TestConstants

import scala.language.higherKinds

import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.headers.Authorization
import akka.http.scaladsl.model.headers.OAuth2BearerToken
import akka.http.scaladsl.model.HttpEntity
import spray.json.JsonFormat
import spray.json.JsObject
import akka.http.scaladsl.model.StatusCodes
import com.google.inject.Provider
import com.kelvaya.ecobee.config.Settings
import com.kelvaya.ecobee.client.service.ServiceError
import com.kelvaya.util.Realizer


/** May be overkill?  At least, the executeRequest method may be overkill.  Do we ever really care to have actual
 *  responses in the tests?
 * @author mike
 *
 */
class TestExecutor(responses : Map[HttpRequest, JsObject])(implicit settings : Settings) extends RequestExecutor with TestConstants {

  def generateAuthorizationHeader = Authorization(OAuth2BearerToken(MockAuthToken))
  def getAppKey = TestAppKey
  def getAccessToken: Option[String] = Some(AccessToken)
  def getAuthCode: Option[String] = Some(AuthCode)
  def getRefreshToken: Option[String] = Some(RefreshToken)

  def executeRequest[T[_], S](req: HttpRequest)(implicit realizer: Realizer[T], formatter : JsonFormat[S]): T[Either[ServiceError,S]] = {
    val fixedReq = req.withUri(settings.EcobeeServerRoot)
    val resp = responses.get(fixedReq).map { json => Right(formatter.read(json))}
    realizer.pure(resp.getOrElse(Left(ServiceError("not_supported", "HTTP method not supported for this request.", req.uri.toString))))
  }
}