package com.kelvaya.ecobee.server
import com.kelvaya.ecobee.client.ServiceError
import com.kelvaya.ecobee.client.ApiError
import com.kelvaya.ecobee.client.AuthError
import com.kelvaya.ecobee.client.RequestError.TokenAccessError
import com.kelvaya.ecobee.client.Statuses
import com.kelvaya.ecobee.client.tokens.TokenStorageError
import com.kelvaya.ecobee.client.AuthError.ErrorCodes


/** Error returned when using the [[ApiClient]] */
sealed trait ClientError extends RuntimeException with Product {
  override def getMessage(): String = toString()
  override def toString(): String = scala.runtime.ScalaRunTime._toString(this)
}

/** Enumeration of valid [[ClientError]] values */
object ClientError {

  /** Error thrown by the API client library or returned by the server 
    *
    * @param err The error as encoded by the API library 
    */
  final case class ApiServiceError(err : ServiceError) extends ClientError

  /** The request returned no thermostats (although at least one was expected) */
  final case object ThermostatNotFound extends ClientError

  /** The API client is misconfigured.  Check the server logs. */
  final case object ConfigurationError extends ClientError
  
  /** Account is currently being authenticated */
  final case object AuthorizationInProgress extends ClientError
  
  /** Account authorized period has expired.  User must re-authenticate the Extensions server. */
  final case object AuthorizationExpired extends ClientError

  /** Account cannot be authenticated.  May require new tokens or re-authentication. */
  final case object AuthenticationFailure extends ClientError
  
  /** Account tokens have expired.  Refresh of tokesn required. */
  final case object TokenExpirationFailure extends ClientError

  /** Account is explicitly unauthorized to the requested resource */
  final case object Unauthorized extends ClientError

  /** Account is unrecognized by the Extensions server */
  final case object Unauthenticated extends ClientError


  def fromServiceError(se : ServiceError) =  se match {
    case ApiError(Statuses.AuthFailed)                            => AuthenticationFailure
    case ApiError(Statuses.ExpiredToken)                          => TokenExpirationFailure
    case ApiError(Statuses.NotAuthorized)                         => Unauthorized
    case err : AuthError                                          => handleAuthError(err)
    case TokenAccessError(TokenStorageError.InvalidAccountError)  => Unauthenticated
    case _                                                        => ApiServiceError(se)
  }


  private def handleAuthError(err : AuthError) = ErrorCodes.withName(err.error) match {
    case ErrorCodes.AccessDenied          => Unauthorized
    case ErrorCodes.AuthorizationExpired  => AuthorizationExpired
    case ErrorCodes.AuthorizationPending  => AuthorizationInProgress
    case _                                => ApiServiceError(err)
  }


}