package com.kelvaya.ecobee.client

import com.kelvaya.ecobee.test.TestConstants

import cats.Id
import cats.Monad

object TestStorage extends TestConstants {
  def apply() = new TestStorage(Tokens(Some(AuthCode), Some(AccessToken), Some(RefreshToken)))
}

class TestStorage private (store : Tokens) extends TokenStorage[Id] {
  type Self = TestStorage

  def getTokens(): Id[Tokens] = implicitly[Monad[Id]].pure(store)
  def storeTokens(tokens: Tokens): Id[Self] = implicitly[Monad[Id]].pure(new TestStorage(tokens))
}
