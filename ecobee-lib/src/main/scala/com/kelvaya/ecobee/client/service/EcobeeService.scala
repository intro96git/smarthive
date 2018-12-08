package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.client.Request
import com.kelvaya.ecobee.client.RequestExecutor
import com.kelvaya.ecobee.config.Settings
import akka.http.scaladsl.model.HttpResponse
import com.kelvaya.ecobee.client.Realizer

import scala.language.higherKinds
import com.kelvaya.ecobee.client.Client
import spray.json.JsonFormat
import spray.json.DefaultJsonProtocol
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.HttpRequest



trait EcobeeRequest[T <: Request] {
  def createRequest(implicit authorizer: RequestExecutor, settings: Settings): T
}

// ---------------------

object EcobeeResponse {
  implicit object HttpResponse extends EcobeeResponse[HttpResponse]
}
trait EcobeeResponse[T]

// ---------------------

abstract class EcobeeService[T <: Request, S] {
  def execute[R[_] : Realizer](req: T)(implicit client : Client) : R[Either[HttpResponse,S]]
}

// ---------------------

abstract class EcobeeJsonService[T <: Request, S : JsonFormat] extends EcobeeService[T,S] {
  final def execute[R[_] : Realizer](req: T)(implicit client : Client) : R[Either[HttpResponse,S]] = client.executeRequest(req.createRequest)
}

// ---------------------

abstract class EcobeeHttpResponseService[T <: Request,R[_]] extends EcobeeService[T, HttpResponse] {
  final def execute[R[_] : Realizer](req: T)(implicit client : Client) : R[Either[HttpResponse,HttpResponse]] = ???
}