package com.kelvaya.ecobee.client

import com.kelvaya.util.Realizer

import scala.language.higherKinds
import cats.Monad
import scala.concurrent.Future
import cats.effect.IO

class FileTokenStorage extends TokenStorage[Future] {
  type Self = FileTokenStorage

  def getTokens() : Future[Tokens] = ???
  def storeTokens(tokens: Tokens): Future[FileTokenStorage] = {
    ???
  }
}