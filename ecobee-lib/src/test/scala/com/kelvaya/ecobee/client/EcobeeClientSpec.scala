package com.kelvaya.ecobee.client

import com.kelvaya.ecobee.client.service.PinResponse
import com.kelvaya.ecobee.client.service.PinRequest
import com.kelvaya.ecobee.client.service.ServiceError
import com.kelvaya.ecobee.config.Settings
import com.kelvaya.ecobee.test.BaseTestSpec

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpRequest

import cats._
import cats.data.EitherT

import monix.eval.Task

import spray.json.JsonFormat


class EcobeeClientSpec extends BaseTestSpec {

  import deps.Implicits._

  "The API client" must "be able to return the PIN for the user to register the client with Ecobee" in {

    val resp = PinResponse(Pin, PinExpiration, AuthCode, PinScope.SmartWrite, PinInterval)


  }

  it must "be able to store a returned authorization key from a PIN request" in (pending)

  it must "be able to store the access and refresh tokens from a token request" in (pending)

}
