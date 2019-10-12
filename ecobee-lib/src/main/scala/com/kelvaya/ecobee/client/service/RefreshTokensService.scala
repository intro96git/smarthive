package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.client.AccountID
import com.kelvaya.ecobee.client.RequestExecutor
import com.kelvaya.ecobee.config.Settings

import scala.language.higherKinds

import com.kelvaya.ecobee.client.Querystrings
import com.kelvaya.ecobee.client.Querystrings.GrantType

import cats.Monad

class RefreshTokensRequest[M[_]:Monad](override val account: AccountID)(implicit e : RequestExecutor[M], s : Settings) extends TokensRequest[M](account) {
  final def authTokenQs : M[Option[Querystrings.Entry]] = async.map(this.getRefreshTokenQs) { Some(_) }
  final def grantTypeQs : Querystrings.Entry = GrantType.RefreshToken
}

// ---------------------

object RefreshTokensService {
  implicit class RefreshTokensServiceImpl[M[_]:Monad](o : RefreshTokensService.type) extends TokensService[M,RefreshTokensRequest[M]] {
    def newTokenRequest(account: AccountID)(implicit e : RequestExecutor[M], s : Settings) = new RefreshTokensRequest(account)
  }
}
