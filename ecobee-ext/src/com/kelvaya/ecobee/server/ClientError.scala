package com.kelvaya.ecobee.server
import com.kelvaya.ecobee.client.ServiceError


/** Error returned when using the [[ApiClient]] */
sealed trait ClientError extends RuntimeException with Product

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
}