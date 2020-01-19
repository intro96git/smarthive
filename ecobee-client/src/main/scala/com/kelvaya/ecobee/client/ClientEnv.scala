package com.kelvaya.ecobee.client

import com.kelvaya.ecobee.client.tokens._
import doobie.h2.H2Transactor

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



  /** Returns an environment that can be used in production systems.
    *
    * It uses use the following: [[com.kelvaya.ecobee.client.ClientSettings$.LiveService ClientSettings.LiveService]],
    * ,[[com.kelvaya.ecobee.client.RequestExecutorImpl RequestExecutorImpl]], and the given Doobie `Transactor`.
    * 
    * @param xa The Doobie Transactor used to execute queries against the H2 database
    */
  def createEnv(xaTask : zio.IO[TokenStorageError,H2Transactor[zio.Task]]) : ClientEnv = new RequestExecutorImpl with ClientSettings.Live with H2DbTokenStorage {
    val transactor = xaTask
  }


  /** Returns an environment backed by a file-based token storage system.
    * This should not be used in production systems.
    *
    * @param fileStore The file containing all of the tokens
    */
  def createEnvUsingFileStore(fileStore : better.files.File) : zio.IO[TokenStorageError,ClientEnv] = {
    BasicFileTokenStorage.connectToDirectory(fileStore).map { ts =>
      new RequestExecutorImpl with ClientSettings.Live with BasicFileTokenStorage {
        val file = fileStore
        val tokensRef = ts
      }
    }
  }
}