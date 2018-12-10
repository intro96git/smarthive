package com.kelvaya.ecobee.client.service

import scala.language.implicitConversions

import akka.http.scaladsl.model.StatusCode
import akka.http.scaladsl.model.StatusCodes
import spray.json.DefaultJsonProtocol._


/** Error returned by Ecobee servers */
object ServiceError {
  implicit val format = jsonFormat3(ServiceError.apply)

  type ErrorCode = ErrorCodes.ErrorCode

  /** All valid error codes */
  object ErrorCodes extends Enumeration {

    /** Ecobee service error and associated HTTP status code */
    case class ErrorCode(error : String, code : StatusCode) extends super.Val(error)

    val AccessDenied = new ErrorCode("access_denied", StatusCodes.Found)
    val InvalidRequest = new ErrorCode("invalid_request", StatusCodes.BadRequest)
    val InvalidClient = new ErrorCode("invalid_client", StatusCodes.Unauthorized)
    val InvalidGrant = new ErrorCode("invalid_grant", StatusCodes.BadRequest)
    val UnauthorizedClient = new ErrorCode("unauthorized_client", StatusCodes.BadRequest)
    val UnsupportedGrant = new ErrorCode("unsupported_grant_type", StatusCodes.BadRequest)
    val InvalidScope = new ErrorCode("invalid_scope", StatusCodes.BadRequest)
    val NotSupported = new ErrorCode("not_supported", StatusCodes.BadRequest)
    val AccountLocked = new ErrorCode("account_locked", StatusCodes.Unauthorized)
    val AccountDisabled = new ErrorCode("account_disabled", StatusCodes.Unauthorized)
    val AuthorizationPending = new ErrorCode("authorization_pending", StatusCodes.Unauthorized)
    val AuthorizationExpired = new ErrorCode("authorization_expired", StatusCodes.Unauthorized)
    val SlowDown = new ErrorCode("slow_down", StatusCodes.Unauthorized)

    implicit def toErrorCode(v : ErrorCodes.Value) = v match {
      case x : ErrorCode ⇒ x
      case _             ⇒ throw new IllegalArgumentException(s"$v is not a valid Error Code")
    }
  }

}

/** Error returned by Ecobee servers
  *
  *  All error messages returned from the Ecobee API are of this format.  Expected errors are enumerated
  *  in the companion class.
  *
  *  @see [[ServiceError$]]
  */
case class ServiceError(error : String, error_description : String, error_uri : String) {
  import ServiceError._

  /** Returns the HTTP status code for this service error */
  def statusCode = ErrorCodes.withName(error).code
}
