package com.kelvaya.ecobee

import com.kelvaya.ecobee.client.Realizer

final class Identity[A](a : A) {
  def get = a
  def flatMap[B](f : (A) ⇒ Identity[B]) = f(a)
  def map[B](f : A ⇒ B) : Identity[B] = new Identity(f(a))
}