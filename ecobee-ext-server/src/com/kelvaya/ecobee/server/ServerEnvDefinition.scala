package com.kelvaya.ecobee.server

import com.kelvaya.ecobee.client.ClientEnvDefinition
import zio.ZEnv

trait ServerEnvDefinition extends ClientEnvDefinition {
  type ServerEnv = ClientRuntimeEnv with ServerSettings with ZEnv 
}