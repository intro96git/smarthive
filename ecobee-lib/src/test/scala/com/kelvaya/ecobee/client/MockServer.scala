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


class MockReqExecutor(responses : Map[HttpRequest, JsObject])(implicit settings : Settings) extends RequestExecutor with TestConstants {
  def generateAuthorizationHeader = Authorization(OAuth2BearerToken(MockAuthToken))
  def getAppKey = ""

  def executeRequest[T[_], S](req: HttpRequest)(implicit realizer: Realizer[T], formatter : JsonFormat[S]): T[Either[HttpResponse,S]] = {
    val fixedReq = req.withUri(settings.EcobeeServerRoot)
    val resp = responses.get(fixedReq).map { json => Right(formatter.read(json))}
    realizer.pure(resp.getOrElse(Left(HttpResponse(StatusCodes.NotFound))))
  }
}