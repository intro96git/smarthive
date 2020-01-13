package com.kelvaya.ecobee.client

import scala.language.implicitConversions

import spray.json.DefaultJsonProtocol._
import spray.json.SerializationException

import com.kelvaya.ecobee.client.tokens.TokenStorageError

import com.twitter.finagle.http.{Status => HttpStatus}

/** Error returned by Ecobee servers 
  *
  * @see [[RequestError]]
  * @see [[AuthError]]
  * @see [[ApiError]] 
  */
sealed trait ServiceError extends RuntimeException with Product {
  override def getMessage(): String = toString()
  override def toString(): String = scala.runtime.ScalaRunTime._toString(this)
}

// ############################################################################


/** Errors that can be encountered while constructing a [[Request]] */
sealed trait RequestError extends ServiceError
object RequestError {

  /** Attempt to read or write [[com.kelvaya.ecobee.client.tokens.Tokens Tokens]] to a [[com.kelvaya.ecobee.client.tokens.TokenStorage TokenStorage]] failed */
  final case class TokenAccessError(e : TokenStorageError) extends RequestError

  /** Attempt to serialize an HTTP entity to JSON failed */
  final case class SerializationError(e : SerializationException) extends RequestError
}


// ############################################################################

/** Error returned during normal API request
  * 
  * @param status Description of the error
  */
final case class ApiError(status : Status) extends ServiceError

/** Implicit conversions for JSON serialization of [[ServiceError]] types */
object ApiError {
  implicit val ApiFormat = jsonFormat1(ApiError.apply)
}

// ############################################################################

/** JSON serialization implicits and enumerated error codes used by [[AuthError]] class */
object AuthError {
  implicit val AuthErrorFormat = jsonFormat3(AuthError.apply)

  type ErrorCode = ErrorCodes.ErrorCode

  /** All valid error codes */
  object ErrorCodes extends Enumeration {

    /** Ecobee service error and associated HTTP status code */
    case class ErrorCode(error : String, code : HttpStatus) extends super.Val(error)

    /** Authorization has been denied by the user. This is only used in the Authorization Code authorization browser redirect. */
    val AccessDenied = new ErrorCode("access_denied", HttpStatus.Found)

    /** The request is malformed. Check parameters. */
    val InvalidRequest = new ErrorCode("invalid_request", HttpStatus.BadRequest)

    /** Authentication error, invalid authentication method, lack of credentials, etc.*/
    val InvalidClient = new ErrorCode("invalid_client", HttpStatus.Unauthorized)
    
    /** The authorization grant, token or credentials are expired or invalid. */
    val InvalidGrant = new ErrorCode("invalid_grant", HttpStatus.BadRequest)

    /** The authenticated client is not authorized to use this authorization grant type. */
    val UnauthorizedClient = new ErrorCode("unauthorized_client", HttpStatus.BadRequest)

    /** The authorization grant type is not supported by the authorization server. */
    val UnsupportedGrant = new ErrorCode("unsupported_grant_type", HttpStatus.BadRequest)

    /** The requested scope is invalid, unknown, malformed, or exceeds the scope granted by the resource owner. */
    val InvalidScope = new ErrorCode("invalid_scope", HttpStatus.BadRequest)

    /** HTTP method not supported for this request. */
    val NotSupported = new ErrorCode("not_supported", HttpStatus.BadRequest)

    /** Account is temporarily locked. */
    val AccountLocked = new ErrorCode("account_locked", HttpStatus.Unauthorized)

    /** Account is disabled. */
    val AccountDisabled = new ErrorCode("account_disabled", HttpStatus.Unauthorized)

    /** Waiting for user to authorize application. */
    val AuthorizationPending = new ErrorCode("authorization_pending", HttpStatus.Unauthorized)

    /** The authorization has expired waiting for user to authorize. */
    val AuthorizationExpired = new ErrorCode("authorization_expired", HttpStatus.Unauthorized)

    /** Slow down polling to the requested interval. */
    val SlowDown = new ErrorCode("slow_down", HttpStatus.Unauthorized)

    implicit def toErrorCode(v : ErrorCodes.Value) = v match {
      case x : ErrorCode ⇒ x
      case _             ⇒ throw new IllegalArgumentException(s"$v is not a valid Error Code")
    }
  }

}

/** Authorization error returned by Ecobee servers
  *
  *  All error messages returned from the Ecobee API are of this format.  Expected errors are enumerated
  *  in the companion class.
  */
final case class AuthError(error : String, error_description : String, error_uri : String) extends ServiceError {
  import AuthError._

  /** Returns the HTTP status code for this service error */
  def statusCode = ErrorCodes.withName(error).code
}
