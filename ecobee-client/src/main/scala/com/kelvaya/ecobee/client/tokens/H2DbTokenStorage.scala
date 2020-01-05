package com.kelvaya.ecobee.client.tokens

import com.kelvaya.ecobee.client.AccountID
import com.kelvaya.ecobee.client.ClientSettings

import doobie._
import doobie.h2.H2Transactor
import doobie.implicits._

import cats.effect.Blocker
import cats.effect.Resource

import zio.IO
import zio.Task
import zio.interop.catz._

import com.typesafe.scalalogging.Logger


/** [[TokenStorage]] backed by an H2 database.
  *
  * @note This storage is not suited for large sites that need high-availability or distributed storage.
  */
trait H2DbTokenStorage extends TokenStorage {
  
  /** The Doobie `Transactor` for executing queries against H2 */
  val transactor : Transactor[Task]

  val tokenStorage = new TokenStorage.Service[Any] {

    def getTokens(account: AccountID): IO[TokenStorageError,Tokens] = {
      val sql = getSql(account).query[Tokens].option.transact(transactor)
      
      sql
        .mapError { error =>
            H2DbTokenStorage.log.warn(s"Error returned by DB: $error")
            TokenStorageError.ConnectionError
        }
        .flatMap { optTokens => 
          IO.fromEither(
            optTokens match {
              case None    => Left(TokenStorageError.InvalidAccountError)
              case Some(t) => Right(t)
            }
          )
        }
    }

    def storeTokens(account: AccountID, tokens: Tokens): IO[TokenStorageError,Unit] = {
      storeSql(account, tokens).update.run.transact(transactor)
        .mapError { e => 
          H2DbTokenStorage.log.warn(s"Unexpected database error: $e")
          TokenStorageError.ConnectionError
        }
        .map(_ => ())
    }

    private def getSql(account : AccountID) = sql"select authToken, accessToken, refreshToken from token where account = ${account.id}"

    private def storeSql(account : AccountID, tokens : Tokens) = 
      sql"""merge into token (account, authToken, accessToken, refreshToken) key (account)
        values (${account.id},${tokens.authorizationToken},${tokens.accessToken},${tokens.refreshToken})"""
  }
}


/** Factory for [[H2DbTokenStorage]]
  *
  * Call `H2DbTokenStorage.connect` to create a new instance or `H2DbTokenStorage.initDb` to create a new instance
  * that points to a new database.
  * 
  */
object H2DbTokenStorage {

  private val log = Logger[H2DbTokenStorage]

  /** [[TokenStorage]] backed by an H2 database.
    *
    * @note This storage is not suited for large sites that need high-availability or distributed storage.
    *
    * @param xa The Doobie `Transactor` for executing queries against H2
    */
  class Live (private[tokens] val xa : Transactor[Task]) extends H2DbTokenStorage {
    val transactor: Transactor[zio.Task] = xa
  }


  /** Returns a handle to a configured [[H2DbTokenStorage]] 
    *
    * @param settings (implicit) The application global settings
    */
  def connect(implicit settings : ClientSettings.Service[Any]) : Either[DbError,Resource[Task,H2DbTokenStorage]] = 
    createConn(false).map(_.map(new Live(_)))

  /** Returns a handle to a configured [[H2DbTokenStorage]]
    *
    * @param settings (implicit) The application global settings
    */
  def initDb(implicit settings : ClientSettings.Service[Any]) : Either[DbError,Resource[Task,H2DbTokenStorage]] = {
    val create = sql"""
    create table token (
      id IDENTITY, 
      account VARCHAR(255) UNIQUE, 
      authToken VARCHAR(32), 
      accessToken VARCHAR(32), 
      refreshToken VARCHAR(32)
    )
    """
      .update
      .run

    createConn(true).map { _.flatMap { xa => 
      Resource.liftF { create.transact(xa).map(_ => new Live(xa)) }
    }}
  }


  private[tokens] def createConn(createIfMissing : Boolean)(implicit s : ClientSettings.Service[Any]) : Either[DbError,Resource[Task,H2Transactor[Task]]] = {
    parseConnectionString(s.JdbcConnection, createIfMissing) map { connString => 
      for {
        connWaitPool <- doobie.util.ExecutionContexts.fixedThreadPool[Task](s.H2DbThreadPoolSize)
        queryThread  <- Blocker[Task]
        xa           <- H2Transactor.newH2Transactor[Task](
          connString,
          s.JdbcUsername,                  
          s.JdbcPassword,                  
          connWaitPool,
          queryThread                                      
        )
      } yield xa
    }
  }

  private val CreateFlag = ";IFEXISTS="
  private val ValidCreateFlag = s"${CreateFlag}TRUE"
  private[tokens] def parseConnectionString(conn : String, createIfMissing : Boolean) : Either[DbError,String] = {
    val flagPos = conn.indexOf(CreateFlag)
    val flagEnabled = (flagPos > -1)
    lazy val syntaxPos = conn.indexOf(ValidCreateFlag)
    lazy val validFlag = (syntaxPos > -1)

    if (!flagEnabled && createIfMissing)          // No flag and we want autocreation
      Right(conn)                        
    else if (flagEnabled && !validFlag)           // Flag found, but not valid
      Left(DbError.InvalidConnection(s"Cannot parse connection string, ${conn}; 'IFEXISTS' flag is invalid"))                               
    else if (flagEnabled && createIfMissing)      // Flag found but we want to create if missing
      Right(conn.take(syntaxPos) + conn.substring(syntaxPos + ValidCreateFlag.size))
    else if (flagEnabled)                         // Flag found and we want don't want autocreation
      Right(conn)
    else                                          // Flag not found and we do not want autocreation
      Right(s"$conn$ValidCreateFlag")
  }

  sealed trait DbError extends RuntimeException
  object DbError {
    case class InvalidConnection(msg : String) extends DbError
  }
}