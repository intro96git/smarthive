package com.kelvaya.ecobee.client

import com.kelvaya.ecobee.client.service.ServiceError
import com.kelvaya.ecobee.config.Settings
import com.kelvaya.ecobee.test.TestConstants
import com.kelvaya.util.Realizer

import scala.language.higherKinds

import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.headers.Authorization
import akka.http.scaladsl.model.headers.OAuth2BearerToken
import monix.eval.Task
import spray.json.JsObject
import spray.json.JsonFormat


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

  def executeRequest[T[_], S](taskReq: Task[HttpRequest])(implicit realizer: Realizer[T], formatter : JsonFormat[S]): T[Either[ServiceError,S]] = {
    import monix.execution.Scheduler.Implicits.global
    val req = taskReq.runSyncUnsafe(scala.concurrent.duration.Duration("1 second"))
    val fixedReq = req.withUri(settings.EcobeeServerRoot)
    val resp = responses.get(fixedReq).map { json => Right(formatter.read(json))}
    realizer.pure(resp.getOrElse(Left(ServiceError("not_supported", "HTTP method not supported for this request.", req.uri.toString))))
  }
}