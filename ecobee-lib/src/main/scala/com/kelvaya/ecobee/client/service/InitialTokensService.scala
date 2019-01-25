package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.client.Querystrings
import com.kelvaya.ecobee.client.Request
import com.kelvaya.ecobee.client.RequestExecutor
import com.kelvaya.ecobee.config.Settings

import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.model.HttpResponse
import com.kelvaya.util.Realizer
import com.kelvaya.ecobee.client.Client

import scala.language.higherKinds
import com.kelvaya.ecobee.client.PinScope
import com.kelvaya.ecobee.client.TokenType
import spray.json.DefaultJsonProtocol
import spray.json.JsonFormat
import com.kelvaya.ecobee.client.PostRequest
import com.kelvaya.ecobee.client.Querystrings.GrantType

class InitialTokensRequest(implicit e : RequestExecutor, s : Settings) extends TokensRequest {
  final def authTokenQs : Option[Querystrings.Entry] = this.getAuthCodeQs
  final def grantTypeQs : Querystrings.Entry = GrantType.Pin
}


// ---------------------


object InitialTokensService extends TokensService[InitialTokensRequest] {
  def newTokenRequest(implicit e : RequestExecutor, s : Settings) = new InitialTokensRequest
}