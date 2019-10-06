package com.kelvaya.ecobee.config

import akka.actor.ActorSystem
import com.kelvaya.ecobee.client.RequestExecutor
import com.kelvaya.ecobee.client.RequestExecutorImpl

import scala.language.higherKinds
import cats.Monad

/** Factory for creating new dependency injections for the application execution.
  *
  * See [[DI]] for further details.
  */
object DI {

  /** Create default dependencies using Monix's `Task` as the Monad to contain operational results */
  def apply(actorSystem : ActorSystem) = new DI[monix.eval.Task](Dependencies(actorSystem))

  /** Use the given dependencies for application execution */
  def apply[A[_] : Monad](options : Dependencies[A]) : DI[A] = new DI[A](options)


  /** Dependencies that can be chosen to be used during application execution.
    *
    * @param actorSystem The `ActorSystem` to use
    * @param settings The [[com.kelvaya.ecobee.config.Settings Settings]] to use for global application settings
    * @param executor The [[com.kelvaya.ecobee.client.RequestExecutor RequestExecutor]] used to execute API requests
    */
  case class Dependencies[A[_]](actorSystem : ActorSystem, settings : Option[Settings] = None, executor : Option[RequestExecutor[A]] = None)
}


/** Dependency Injection settings
  *
  * Sets the dependencies used by the application, including the [[com.kelvaya.ecobee.config.Settings Settings]], `ActorSystem`,
  * `LoggingBus`, [[com.kelvaya.ecobee.client.RequestExecutor RequestExecutor]], and the Monad type that will be used
  * to contain the results of the `RequestExecutor` (and other library calls).
  *
  * @param di [[DI$.Dependencies]] to use in the application
  * @tparam A The monad container type to hold results throughout the use of the library
  *
  * @example
{{{
final class MyApp extends Application {

  // Initialize dependencies using all defaults
  val deps = DI(ActorSystem("my-actor-sys"))
  import deps.Implicits._
}

final class MyOverrideApp extends Application {
  val overrides = DI.Dependencies[Future](ActorSystem("my-overrides"),settings=new MySettings)

  // Initialize dependencies using custom settings and using Scala's Future as the container Monad
  val deps = DI(overrides)

  // Import the created dependencies except for the executor, which we create on our own (useful for unit testing)
  import deps.Implicits.{SettingsImplicit,ActorSystemImplicit,LoggingBusImplicit}

  implicit val Executor = new MyOwnExecutor
}

}}}
  *
  * @note To setup Dependency Injection, call an `apply` method of [[DI$]] and then import the implicit values from the created instance.
  * If no dependencies are explicitly requested, it will use the following defaults: [[com.kelvaya.ecobee.config.Settings$]],
  * the `LoggingBus` directly off of the given `ActorSystem`, and [[com.kelvaya.ecobee.client.RequestExecutorImpl RequestExecutorImpl[Task]]]
  *
  */
class DI[A[_] : Monad] (di : DI.Dependencies[A]) {

  /** Application settings */
  lazy val settings = di.settings.getOrElse(Settings)

  /** Akka Actor system */
  lazy val actorSys = di.actorSystem

  /** Akka logging */
  lazy val loggingBus = di.actorSystem.eventStream

  /** RequestExecutor used to execute all API requests */
  lazy val executor = di.executor.getOrElse(new RequestExecutorImpl[A])

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

