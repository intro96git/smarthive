package com.kelvaya.ecobee.client

/** Contains token to be used in an `Authorization` OAuth2 header */
final class OAuthHeader(val auth : String) extends AnyVal

/** Factory for [[OAuthHeader]] */
object OAuthHeader {

  /** Put given token into an [[OAuthHeader]] */
  def apply(token : String) = new OAuthHeader(s"Bearer ${token}")
}