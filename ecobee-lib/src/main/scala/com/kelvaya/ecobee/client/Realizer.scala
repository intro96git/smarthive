package com.kelvaya.ecobee.client

import scala.language.higherKinds

/** Realizes a type into the container class of type ${T}.
  *
  * This can be used to abstract the type of container class away, which may be useful
  * for monadic classes such as `Future` or [[com.kelvaya.ecobee.Identity]].
  *
  * @define T T
  */
trait Realizer[T[_]] {
  def realize[S](v : T[S]) : S
  def pure[S](v : S) : T[S]
}