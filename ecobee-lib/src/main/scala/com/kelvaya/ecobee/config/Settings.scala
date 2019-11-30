package com.kelvaya.ecobee.config

import com.typesafe.config.ConfigFactory
import com.typesafe.config.Config
import akka.http.scaladsl.model.Uri

object Settings extends Settings(ConfigFactory.load())

abstract class Settings(config: Config) {
  val EcobeeServerRoot = Uri(config.getString("ecobee.server.uri"))
  val EcobeeAppKey = config.getString("ecobee.server.app-key")
  lazy val H2DbThreadPoolSize = config.getInt("ecobee.client.db.h2.thread-pool-size")
  lazy val JdbcConnection = config.getString("ecobee.client.db.connection")
  lazy val JdbcUsername = config.getString("ecobee.client.db.username")
  lazy val JdbcPassword = config.getString("ecobee.client.db.password")
}
