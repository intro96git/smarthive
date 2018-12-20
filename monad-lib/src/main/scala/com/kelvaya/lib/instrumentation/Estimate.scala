package com.kelvaya.lib.instrumentation

trait Estimate[-T <: Estimatable] {
  val step : String
  val time : Long

  def computeEstimate(op : T) : Long
}


trait LinearEstimate[-T <: Estimatable] extends Estimate[T] {
  override val step : String
  override val time : Long
  val size : Long

  def computeEstimate(op : T) = {
    val diff = op.estimatedSize - size
    val ratio = time / size.toFloat
    (diff * ratio).round
  }
}


trait Estimatable {
  def estimatedSize : Long
}