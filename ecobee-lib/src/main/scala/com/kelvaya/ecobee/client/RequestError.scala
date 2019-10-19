package com.kelvaya.ecobee.client
import com.kelvaya.ecobee.client.tokens.TokenStorageError
import spray.json.SerializationException


/** Errors that can be encountered while constructing a [[Request]] */
sealed trait RequestError extends RuntimeException
object RequestError {

  /** Attempt to read or write [[com.kelvaya.ecobee.client.tokens.Tokens Tokens]] to a [[com.kelvaya.ecobee.client.tokens.TokenStorage TokenStorage]] failed */
  final case class TokenAccessError(e : TokenStorageError) extends RequestError

  /** Attempt to serialize an HTTP entity to JSON failed */
  final case class SerializationError(e : SerializationException) extends RequestError
}