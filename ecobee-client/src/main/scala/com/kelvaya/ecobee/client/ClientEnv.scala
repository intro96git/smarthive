package com.kelvaya.ecobee.client
import com.kelvaya.ecobee.config.Settings
import com.kelvaya.ecobee.client.tokens.TokenStorage

/** Defines the [[ClientEnv]] type used as the environment for ZIO effectful types */
trait ClientEnvDefinition {

  /** `ZIO` effectful type environment for the Ecobee API client.
    * 
    * Includes [[RequestExecutor]], [[Settings]], and [[TokenStorage]]
    */
  type ClientEnv = RequestExecutor with Settings with TokenStorage
}