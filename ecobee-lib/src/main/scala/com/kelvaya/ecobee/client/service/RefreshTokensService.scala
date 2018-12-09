package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.client.Client
import com.kelvaya.ecobee.client.PinScope
import com.kelvaya.ecobee.client.PostRequest
import com.kelvaya.ecobee.client.Realizer
import com.kelvaya.ecobee.client.RequestExecutor
import com.kelvaya.ecobee.client.TokenType
import com.kelvaya.ecobee.config.Settings

import scala.language.higherKinds

import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.Uri
import spray.json.DefaultJsonProtocol
import com.kelvaya.ecobee.client.Querystrings
import com.kelvaya.ecobee.client.Querystrings.GrantType

class RefreshTokensRequest(implicit e : RequestExecutor, s : Settings) extends TokensRequest {
  final def authTokenQs : Option[Querystrings.Entry] = Some(this.getRefreshTokenQs)
  final def grantTypeQs : Querystrings.Entry = GrantType.RefreshToken
}

// ---------------------

object RefreshTokensService extends TokensService[RefreshTokensRequest] {
  def newTokenRequest(implicit e : RequestExecutor, s : Settings) = new RefreshTokensRequest
}