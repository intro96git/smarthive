package com.kelvaya.ecobee.server

import com.kelvaya.ecobee.client.ClientEnvDefinition
import zio.ZEnv

/** Defines the [[ServerEnv]] type used as the environment for ZIO effectful types */
trait ServerEnvDefinition extends ClientEnvDefinition {
  
  /** The type used as the environment for ZIO effectful types throughout the server */
  type ServerEnv = ServerSettings with ZEnv 
}