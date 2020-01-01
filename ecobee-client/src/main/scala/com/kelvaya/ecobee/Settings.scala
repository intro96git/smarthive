package com.kelvaya.ecobee

import com.typesafe.config.Config


/** Settings module for all Ecobee libraries */
trait Settings {
  val settings : Settings.Service[Any]
}

/** Service definition for [[Settings]] module */
object Settings {

  /** Base-service definition for [[Settings]] module.
    *
    * @note The base module has no members
    */ 
  trait Service[R]
}


/** [[Settings]] module back by TypeSafe's `Config` */
trait ConfigSettings extends Settings {
  val settings : ConfigSettings.Service[Any]
}

/** Service definition for [[ConfigSettings]] */
object ConfigSettings {
  
  /** [[Settings]] service back by TypeSafe's `Config` */
  trait Service[R] extends Settings.Service[R] {
    val config : Config
  }
}