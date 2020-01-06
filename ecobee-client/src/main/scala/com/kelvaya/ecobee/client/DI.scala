package com.kelvaya.ecobee.client




/** Factory for creating new dependency injections for the application execution.
  *
  * See [[DI]] for further details.
  */
object DI {


  /** Use the given dependencies for application execution */
  def apply(options : Dependencies) : DI = new DI(options)


  /** Dependencies that can be chosen to be used during application execution.
    *
    * @param settings The [[com.kelvaya.ecobee.client.ClientSettings ClientSettings]] to use for global application settings
    * @param executor The [[com.kelvaya.ecobee.client.RequestExecutor RequestExecutor]] used to execute API requests
    */
  case class Dependencies(settings : Option[ClientSettings] = None, executor : Option[RequestExecutor] = None)
}


/** Dependency Injection settings
  *
  * Sets the dependencies used by the application, including the [[com.kelvaya.ecobee.client.ClientSettings ClientSettings]],
  * and [[com.kelvaya.ecobee.client.RequestExecutor RequestExecutor]].
  *
  * @param di [[DI$.Dependencies]] to use in the application
  *
  * @example
{{{
final class MyApp extends Application {

  // Initialize dependencies using all defaults
  val deps = DI()
  import deps.Implicits._
}

final class MyOverrideApp extends Application {
  val overrides = DI.Dependencies(settings=new MySettings)

  // Initialize dependencies using custom settings
  val deps = DI(overrides)

  // Import the created dependencies except for the executor, which we create on our own (useful for unit testing)
  import deps.Implicits.{SettingsImplicit}

  implicit val Executor = new MyOwnExecutor
}

}}}
  *
  * @note To setup Dependency Injection, call an `apply` method of [[DI$]] and then import the implicit values from the created instance.
  * If no dependencies are explicitly requested, it will use the following defaults: [[com.kelvaya.ecobee.client.ClientSettings$.LiveService ClientSettings.LiveService]],
  * and [[com.kelvaya.ecobee.client.RequestExecutorImpl RequestExecutorImpl]]
  *
  */
class DI (di : DI.Dependencies) {

  /** Application settings */
  lazy val settings = di.settings.getOrElse(ClientSettings.Live).settings

  /** RequestExecutor used to execute all API requests */
  lazy val executor = di.executor.map(zio.UIO(_)).getOrElse(RequestExecutorImpl.create)

  /** Exposes all dependencies implicitly
    *
    *  To use, import wanted dependencies.  For all of them, use a wildcard: `import Implicits._`
    */
  object Implicits {
    implicit lazy val SettingsImplicit = settings
    implicit lazy val ExecutorImplicit = executor
  }
}

