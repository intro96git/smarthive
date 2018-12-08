package com.kelvaya.ecobee.client

import scala.language.higherKinds

trait Realizer[T[_]] {
  def realize[S](v : T[S]) : S
  def pure[S](v : S) : T[S]
}