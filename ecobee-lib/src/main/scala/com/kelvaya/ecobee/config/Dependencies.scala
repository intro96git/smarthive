package com.kelvaya.ecobee.config

import com.google.inject.AbstractModule

import net.codingwell.scalaguice.InjectorExtensions.ScalaInjector
import net.codingwell.scalaguice.ScalaModule

/**
 * Instances of this class can create new instances of the given type, T, via Guice injection.
 *
 * This is done using the [[#apply]] method with no explicit arguments.  It requires the presence
 * of a implicit `ScalaInjector` instance in-scope.
 *
 * @param [T] Type that can be injected by this class
 * @define injectedType type <T>
 */
abstract class CanInject[T: Manifest] {

  /**
   * Returns a new instance of $injectedType using Guice injection.
   *
   * @param injector The `ScalaInjector` that performs the injection
   */
  def apply()(implicit injector: ScalaInjector) = injector.instance[T]
}

object Dependencies {

}

class Dependencies extends AbstractModule with ScalaModule {
  override def configure() = {
  }
}