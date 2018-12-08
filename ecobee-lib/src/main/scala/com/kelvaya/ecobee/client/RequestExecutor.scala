package com.kelvaya.ecobee.client

import scala.language.higherKinds

import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.headers.Authorization
import spray.json.JsonFormat


trait RequestExecutor {
  def generateAuthorizationHeader: Authorization
  def getAppKey: String
  def getAuthCode: Option[String]
  def getAccessToken: Option[String]
  def getRefreshToken: Option[String]
  def executeRequest[T[_] : Realizer,S : JsonFormat](req : HttpRequest) : T[Either[HttpResponse,S]]
}
