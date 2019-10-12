package com.kelvaya.ecobee.client
import com.kelvaya.ecobee.client.storage.TokenStorageError

sealed trait RequestError extends RuntimeException
object RequestError {
  final case class TokenAccessError(e : TokenStorageError) extends RequestError
}