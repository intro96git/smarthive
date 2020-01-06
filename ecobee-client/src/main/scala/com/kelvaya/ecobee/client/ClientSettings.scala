package com.kelvaya.ecobee.client

import com.kelvaya.ecobee.ConfigSettings

import com.typesafe.config.ConfigFactory




/** Ecobee client library settings module
  *
  * @note The default implementation can be found at [[ClientSettings$.Live]]  
  *
  * @see [[ClientSettings$.Service]] 
  */
trait ClientSettings extends ConfigSettings {
  val settings : ClientSettings.Service[Any]
}

/** [[ClientSettings]] service and default definitions */
object ClientSettings {

  /** Client library settings */
  trait Service[R] extends ConfigSettings.Service[R] {
    lazy val EcobeeServerRoot = {
      val url = new java.net.URL(config.getString("ecobee.server.uri"))
      if (url.getPort > 0) url
      else {
        val port = url.getProtocol match {
          case "http"  => 80
          case "https" => 443
          case p       => throw new com.typesafe.config.ConfigException.Generic(s"Unrecognized HTTP protocol: $p")
        }
        new java.net.URL(url.getProtocol, url.getHost, port, url.getFile)
      }
    }
    lazy val EcobeeAppKey = config.getString("ecobee.server.app-key")
    lazy val EcobeeApiVersion = config.getString("ecobee.server.api-version")
    lazy val H2DbThreadPoolSize = config.getInt("ecobee.client.db.h2.thread-pool-size")
    lazy val JdbcConnection = config.getString("ecobee.client.db.connection")
    lazy val JdbcUsername = config.getString("ecobee.client.db.username")
    lazy val JdbcPassword = config.getString("ecobee.client.db.password")
  }

  /** Default implementation for [[ClientSettings.Service]].
    * This is backed by the default loader of the Typesafe Config library
    */
  class LiveService extends Service[Any] {
    val config = ConfigFactory.load()
  }

  /** Default implementation for [[ClientSettings]], using the [[LiveService]] for the service. */
  trait Live extends ClientSettings {
    val settings = new LiveService
  }

  object Live extends Live
}