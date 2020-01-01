package com.kelvaya.ecobee.client

import com.typesafe.config.ConfigFactory
import com.typesafe.config.Config
import akka.http.scaladsl.model.Uri


trait ClientSettings {
  val EcobeeServerRoot : Uri
  val EcobeeAppKey : String
  val H2DbThreadPoolSize : Int
  val JdbcConnection : String
  val JdbcUsername : String
  val JdbcPassword : String
}

object ClientSettings extends ConfigSettings(ConfigFactory.load())

abstract class ConfigSettings(config: Config) extends ClientSettings {
  val EcobeeServerRoot = Uri(config.getString("ecobee.server.uri"))
  val EcobeeAppKey = config.getString("ecobee.server.app-key")
  lazy val H2DbThreadPoolSize = config.getInt("ecobee.client.db.h2.thread-pool-size")
  lazy val JdbcConnection = config.getString("ecobee.client.db.connection")
  lazy val JdbcUsername = config.getString("ecobee.client.db.username")
  lazy val JdbcPassword = config.getString("ecobee.client.db.password")
}
