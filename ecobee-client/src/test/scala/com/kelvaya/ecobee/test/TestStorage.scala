package com.kelvaya.ecobee.test

import com.kelvaya.ecobee.client.AccountID

import com.kelvaya.ecobee.client.tokens._
import zio.UIO


object TestStorage extends TestConstants {
  def apply(account: AccountID) = new TestStorage(Map(account -> Tokens(Some(AuthCode), Some(AccessToken), Some(RefreshToken))))
}

class TestStorage private (store : Map[AccountID,Tokens]) extends TokenStorage {
  val tokenStorage = new TokenStorage.Service[Any] {
    private var _tokens = store

    // May throw exception...fix it!!
    def getTokens(account : AccountID): UIO[Tokens] = UIO(_tokens(account))

    // This won't handle parallelism or re-use.  Fix it!
    def storeTokens(account : AccountID, tokens: Tokens): UIO[Unit] = UIO { _tokens = _tokens + ((account, tokens)) }
  }
}
