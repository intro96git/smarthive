package com.kelvaya.ecobee.server

import com.kelvaya.ecobee.client.ClientSettings


/** Ecobee server settings module
  *
  * @note The default implementation can be found at [[ServerSettings$.Live]]  
  *
  * @see [[ServerSettings$.Service]] 
  */
trait ServerSettings extends ClientSettings {
  val settings : ServerSettings.Service[Any]
}


/** [[ServerSettings]] service and default definitions */
object ServerSettings {
  
  /** Server settings */
  trait Service[R] extends ClientSettings.Service[R] {
    val ListenPort : String
  }

  /** Default implementation for [[ServerSettings]], using the [[LiveService]] for the service. */
  trait Live extends ServerSettings {
    val settings = new LiveService
  }
  
  /** Default implementation for [[ServerSettings]], using the [[LiveService]] for the service. */
  object Live extends Live

  /** Default implementation for [[Service]].
    * This is extends the default client library settings, `ClientSettings.LiveService`
    */
  class LiveService extends ClientSettings.LiveService with Service[Any] {
    val ListenPort = ":%d".format(this.config.getInt("ecobee-ext.port"))
  }
}