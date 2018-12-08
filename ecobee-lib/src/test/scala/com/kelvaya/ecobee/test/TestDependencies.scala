package com.kelvaya.ecobee.test

import com.kelvaya.ecobee.config.Settings

import scala.reflect.ManifestFactory.classType

import com.google.inject.AbstractModule
import com.google.inject.Guice

import akka.actor.ActorSystem
import net.codingwell.scalaguice.ScalaModule

object TestDependencies {
  def injector()(implicit sys: ActorSystem) = Guice.createInjector(new TestDependencies)
}

class TestDependencies()(implicit sys: ActorSystem) extends AbstractModule with ScalaModule {
  override def configure() = {
    bind[Settings].toInstance(TestSettings)
    bind[ActorSystem].toInstance(sys)
  }
}