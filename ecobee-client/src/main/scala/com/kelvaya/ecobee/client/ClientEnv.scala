package com.kelvaya.ecobee.client

import com.kelvaya.ecobee.client.tokens.TokenStorage

/** Defines the [[ClientEnv]] type used as the environment for ZIO effectful types */
trait ClientEnvDefinition {

  /** `ZIO` Runtime environment for the Ecobee API client.
    * 
    * Includes [[RequestExecutor]] and [[ClientSettings]]
    */
  type ClientRuntimeEnv = RequestExecutor with TokenStorage
  
  
  /** `ZIO` effectful type environment for the Ecobee API client.
    * 
    * Includes [[RequestExecutor]], [[ClientSettings]], and [[com.kelvaya.ecobee.client.tokens.TokenStorage TokenStorage]]
    */
  type ClientEnv = ClientRuntimeEnv with ClientSettings
}