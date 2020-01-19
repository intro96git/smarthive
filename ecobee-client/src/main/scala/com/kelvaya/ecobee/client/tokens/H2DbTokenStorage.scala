package com.kelvaya.ecobee.client.tokens

import com.kelvaya.ecobee.client.AccountID
import com.kelvaya.ecobee.client.ClientSettings

import doobie.h2.H2Transactor
import doobie.implicits._

import cats.effect.Blocker

import zio.IO
import zio.Managed
import zio.Task
import zio.UIO
import zio.interop.catz._

import com.typesafe.scalalogging.Logger


/** [[TokenStorage]] backed by an H2 database.
  *
  * @note This storage is not suited for large sites that need high-availability or distributed storage.
  */
trait H2DbTokenStorage extends TokenStorage {

  /** The Doobie `Transactor` for executing queries against H2 */
  val transactor : IO[TokenStorageError,H2Transactor[Task]]
  
  val tokenStorage = new TokenStorage.Service[Any] {

    def getTokens(account: AccountID): IO[TokenStorageError,Tokens] = transactor flatMap { xa =>
      val sql = getSql(account).query[Tokens].option.transact(xa)
      
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

    def storeTokens(account: AccountID, tokens: Tokens): IO[TokenStorageError,Unit] = transactor flatMap { xa =>
      storeSql(account, tokens).update
      .run
      .transact(xa)
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


/** Helper functions for [[H2DbTokenStorage]] transactors.
  *
  * Call `H2DbTokenStorage.connect` to create a new transactor or `H2DbTokenStorage.initAndConnect` to create a new transactor
  * that points to a new database.
  */
object H2DbTokenStorage {

  private val log = Logger[H2DbTokenStorage]


  /** Returns a handle to a configured [[H2DbTokenStorage]] 
    *
    * @param settings (implicit) The application global settings
    */
  def connect(implicit settings : ClientSettings.Service[Any]) : Managed[TokenStorageError,H2Transactor[Task]] = createConn(false)


  /** Returns a handle to a configured [[H2DbTokenStorage]] after initializing the storage
    * with the required structure.
    *
    * @note This will create a new database if necessary.
    * 
    * @param settings (implicit) The application global settings
    */
  def initAndConnect(implicit settings : ClientSettings.Service[Any]) : Managed[TokenStorageError,H2Transactor[Task]] = {
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

    createConn(true) tapM { xa =>
      create.transact(xa).catchAll { case t => 
        Logger[H2DbTokenStorage].error(s"Could not initialize H2 database; [${t.getClass.getName}] ${t.getMessage}")
        IO.fail(TokenStorageError.ConnectionError)
      }
    }
  }


  private[tokens] def createConn(createIfMissing : Boolean)(implicit s : ClientSettings.Service[Any]) : Managed[TokenStorageError,H2Transactor[Task]] = {
    val conn = parseConnectionString(s.JdbcConnection, createIfMissing) map { connString => 
      (for {
        connWaitPool <- doobie.util.ExecutionContexts.fixedThreadPool[Task](s.H2DbThreadPoolSize)
        queryThread  <- Blocker[Task]
        xa           <- H2Transactor.newH2Transactor[Task](
          connString,
          s.JdbcUsername,                  
          s.JdbcPassword,                  
          connWaitPool,
          queryThread                                      
        )
      } yield xa).toManagedZIO
    }

    val handledConn : UIO[Managed[TokenStorageError,H2Transactor[Task]]] = conn.fold(
      e => Managed.fail(e),
      s => s.catchAll { case t => 
        Logger[H2DbTokenStorage].error(s"Could not create H2 connection; [${t.getClass.getName}] ${t.getMessage}")
        Managed.fail(TokenStorageError.ConnectionError)
      }
    )

    val connect = handledConn <* UIO(Logger[H2DbTokenStorage].info(s"Connected to H2 database at ${s.JdbcConnection}"))
    Managed(connect.flatMap(_.reserve))
  }

  private val CreateFlag = ";IFEXISTS="
  private val ValidCreateFlag = s"${CreateFlag}TRUE"
  private[tokens] def parseConnectionString(conn : String, createIfMissing : Boolean) : IO[TokenStorageError,String] = {
    val flagPos = conn.indexOf(CreateFlag)
    val flagEnabled = (flagPos > -1)
    lazy val syntaxPos = conn.indexOf(ValidCreateFlag)
    lazy val validFlag = (syntaxPos > -1)

    if (!flagEnabled && createIfMissing)          // No flag and we want autocreation
      IO.succeed(conn)                        
    else if (flagEnabled && !validFlag)           // Flag found, but not valid
      IO.fail { Logger[H2DbTokenStorage].error(s"Cannot parse connection string, ${conn}; 'IFEXISTS' flag is invalid"); TokenStorageError.ConnectionError }                               
    else if (flagEnabled && createIfMissing)      // Flag found but we want to create if missing
      IO.succeed(conn.take(syntaxPos) + conn.substring(syntaxPos + ValidCreateFlag.size))
    else if (flagEnabled)                         // Flag found and we want don't want autocreation
      IO.succeed(conn)
    else                                          // Flag not found and we do not want autocreation
      IO.succeed(s"$conn$ValidCreateFlag")
  }
}