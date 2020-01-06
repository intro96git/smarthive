package com.kelvaya.ecobee.client

/** Holds a string representing a URI */
final class Uri(val uri : String) extends AnyVal

/** Factory for [[Uri]] */
object Uri {
  def apply(uri : String) = new Uri(uri)
}