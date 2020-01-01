package com.kelvaya.ecobee.client

import akka.actor.ActorSystem


/** Factory for creating new dependency injections for the application execution.
  *
  * See [[DI]] for further details.
  */
object DI {

  /** Create default dependencies using the given Actor system */
  def apply(actorSystem : ActorSystem) = new DI(Dependencies(actorSystem))

  /** Use the given dependencies for application execution */
  def apply(options : Dependencies) : DI = new DI(options)


  /** Dependencies that can be chosen to be used during application execution.
    *
    * @param actorSystem The `ActorSystem` to use
    * @param settings The [[com.kelvaya.ecobee.client.ClientSettings ClientSettings]] to use for global application settings
    * @param executor The [[com.kelvaya.ecobee.client.RequestExecutor RequestExecutor]] used to execute API requests
    */
  case class Dependencies(actorSystem : ActorSystem, settings : Option[ClientSettings] = None, executor : Option[RequestExecutor] = None)
}


/** Dependency Injection settings
  *
  * Sets the dependencies used by the application, including the [[com.kelvaya.ecobee.client.ClientSettings ClientSettings]], `ActorSystem`,
  * `LoggingBus`, and [[com.kelvaya.ecobee.client.RequestExecutor RequestExecutor]].
  *
  * @param di [[DI$.Dependencies]] to use in the application
  *
  * @example
{{{
final class MyApp extends Application {

  // Initialize dependencies using all defaults
  val deps = DI(ActorSystem("my-actor-sys"))
  import deps.Implicits._
}

final class MyOverrideApp extends Application {
  val overrides = DI.Dependencies(ActorSystem("my-overrides"),settings=new MySettings)

  // Initialize dependencies using custom settings
  val deps = DI(overrides)

  // Import the created dependencies except for the executor, which we create on our own (useful for unit testing)
  import deps.Implicits.{SettingsImplicit,ActorSystemImplicit,LoggingBusImplicit}

  implicit val Executor = new MyOwnExecutor
}

}}}
  *
  * @note To setup Dependency Injection, call an `apply` method of [[DI$]] and then import the implicit values from the created instance.
  * If no dependencies are explicitly requested, it will use the following defaults: [[com.kelvaya.ecobee.client.ClientSettings$]],
  * the `LoggingBus` directly off of the given `ActorSystem`, and [[com.kelvaya.ecobee.client.RequestExecutorImpl RequestExecutorImpl]]
  *
  */
class DI (di : DI.Dependencies) {

  /** Application settings */
  lazy val settings = di.settings.getOrElse(ClientSettings)

  /** Akka Actor system */
  lazy val actorSys = di.actorSystem

  /** Akka logging */
  lazy val loggingBus = di.actorSystem.eventStream

  /** RequestExecutor used to execute all API requests */
  lazy val executor = di.executor.getOrElse(new RequestExecutorImpl()(actorSys))

  /** Exposes all dependencies implicitly
    *
    *  To use, import wanted dependencies.  For all of them, use a wildcard: `import Implicits._`
    */
  object Implicits {
    implicit lazy val SettingsImplicit = settings
    implicit lazy val LoggingBusImplicit = loggingBus
    implicit lazy val ExecutorImplicit = executor
    implicit lazy val ActorSysImplicit = actorSys
  }
}

