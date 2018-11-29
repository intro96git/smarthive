package com.kelvaya.ecobee.test

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import com.kelvaya.ecobee.client.AuthorizationFactory
import com.kelvaya.ecobee.client.MockAuthorizationFactory
import com.kelvaya.ecobee.config.Settings
import scala.reflect.ManifestFactory.classType
import com.google.inject.Guice
import akka.actor.ActorSystem

object TestDependencies {
  def injector()(implicit sys: ActorSystem) = Guice.createInjector(new TestDependencies)
}

class TestDependencies()(implicit sys: ActorSystem) extends AbstractModule with ScalaModule {
  override def configure() = {
    bind[AuthorizationFactory].toInstance(MockAuthorizationFactory)
    bind[Settings].toInstance(TestSettings)
    bind[ActorSystem].toInstance(sys)
  }
}