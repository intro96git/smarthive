package com.kelvaya.ecobee.client.tokens

import com.kelvaya.ecobee.client.ClientSettings
import com.kelvaya.ecobee.test.client._

import better.files.File

import doobie.implicits._
import doobie.util.transactor.Transactor

import zio.Task
import zio.UIO
import zio.interop.catz._

import org.scalatest.compatible.Assertion

class H2DbTokenStorageSpec extends BaseTestSpec with TokenStorageBehavior with ZioTest {

  "Connection parsing" must "support enabling or disabling autocreation" in {
    val enabled = "jdbc:h2:/data/sample;IFEXISTS=TRUE"
    val missing = "jdbc:h2:/data/sample"
    val bad = "jdbc:h2:/data/sample;IFEXISTS=xx"
    val middle = "jdbc:h2:/data/sample;IFEXISTS=TRUE;OTHER=123"

    for {
      a <- H2DbTokenStorage.parseConnectionString(enabled, true)
      _ <- a shouldBe missing
      b <- H2DbTokenStorage.parseConnectionString(enabled, false) 
      _ <- b shouldBe enabled
 
      c <- H2DbTokenStorage.parseConnectionString(missing, true) 
      _ <- c shouldBe missing
      d <- H2DbTokenStorage.parseConnectionString(missing, false)
      _ <- d shouldBe enabled
 
      f <- H2DbTokenStorage.parseConnectionString(middle, true)
      _ <- f shouldBe missing + ";OTHER=123"
      g <- H2DbTokenStorage.parseConnectionString(middle, false)
      _ <- g  shouldBe middle
 
      h <- H2DbTokenStorage.parseConnectionString(bad, true).flip
      _ <- h shouldBe TokenStorageError.ConnectionError
      i <- H2DbTokenStorage.parseConnectionString(bad, false).flip 
      _ <- i shouldBe TokenStorageError.ConnectionError
    } yield succeed
  }


  "The H2 database token storage driver" must "be initializable" in {
    val test = createTempDb { implicit s =>
      
      val t1 =
        for {
          init <- H2DbTokenStorage.initAndConnect.use(_ => Task(true))
          a    <- init shouldBe true
        }
        yield a

      val t2 = H2DbTokenStorage.createConn(false).use { xa => 
        for {
          cnt <- sql"select count(*) from token".query[Int].unique.transact(xa)
          a   <- cnt shouldBe 0
        } yield a
      }

      // t1 should succeed the first time, but should be an exception the second time
      // NB: The "mapError" will change the Exception error type into a Assertion "error" type 
      // (to keep the types correct for "leftOrFailException"). 
      t1 *> t1.mapError(_ => succeed).flip.mapError(_ => fail("InitAndConnect should fail the 2nd time")) *> t2
      
    }

    run(test)
  }


  it must behave like storage(usingStore)



  it must "return an error if the DB does not exist" in {
    val test = createTempDb { implicit s =>
      for {
        qry  <- H2DbTokenStorage.connect.use { xa =>
                  val store = new H2DbTokenStorage { val transactor = UIO(xa) }
                  val queryRun = for {
                    _ <- store.tokenStorage.getTokens(account)
                  } yield fail("Should not have succeeded")
                  
                  queryRun.flip.map(e => e shouldBe TokenStorageError.ConnectionError)
                }
      } yield qry
    }

    run(test)
  }


  it must "properly store the tokens in an H2 database" in {
    val test = createTempDb { implicit s =>

      // create test database and alter record
      val setup = withTestStore { s =>
        s.storeTokens(account, Tokens(Some("mytest"), None, None)).flatMap(_ => succeed)
      }


      // verify directly through H2 Doobie library that we have records
      val verify = {
        import doobie.h2._

        val database = s.JdbcConnection
        val user = s.JdbcUsername
        val pwd = s.JdbcPassword
        val query = sql"select authToken, accessToken, refreshToken from token where account = $account"
          .query[(Option[String],Option[String],Option[String])]
          .unique

        val db = for {
          connWait  <- doobie.util.ExecutionContexts.fixedThreadPool[Task](1)
          queryPool <- cats.effect.Blocker[Task]
          xa        <- H2Transactor.newH2Transactor[Task](database,user,pwd,connWait,queryPool)
        } yield xa

        db.use { xa =>
          for {
            result <- query.transact(xa)
            assrt  <- result shouldBe ((Some("mytest"),None,None))
          } yield assrt
        }
      }
      
      setup *> verify
    }

    run(test)
  }



  // Provides an auto-created-and-deleted H2DbTokenStorage to the given function
  // Is used as the function for the TokenStorageBehavior tests.
  private def usingStore(actions : TokenStorage.Service[Any] => Task[Assertion]) : Task[Assertion] = {
    createTempDb { implicit settings =>
      withTestStore { store => actions(store) }
    }
  }


  private def cleanup(f : File) = UIO(f.delete())

  private def createTempDb(fn : ClientSettings.Service[Any] => Task[Assertion]) : Task[Assertion] = {
    Task(File.newTemporaryDirectory(prefix=s"h2dts")).bracket(cleanup)  { dir => 
      val settings = new H2DbTokenStorageTestSettings(s"jdbc:h2:${dir.toString}/test.db")
      fn(settings.settings)
    }
  }


  private def withTestStore[S](fn : TokenStorage.Service[Any] => Task[S])(implicit s : ClientSettings.Service[Any]) = {
    H2DbTokenStorage.initAndConnect.use { xa =>
      _createDb(xa) *> {
        val store =  new H2DbTokenStorage { val transactor = zio.IO.succeed(xa) }
        fn(store.tokenStorage)
      }
    }    
  }

  private def _createDb(xa : Transactor[Task]) = { 
    val sql = 
      sql"insert into token (account, authToken, accessToken, refreshToken) values ($account, $AuthCode, $AccessToken, $RefreshToken)"
      .update

    sql.run.transact(xa)
  }
}


class H2DbTokenStorageTestSettings(jdbcConnection : String) extends ClientSettings {
  val settings = new TestClientSettings.TestClientService {
    override lazy val JdbcConnection = jdbcConnection
    override lazy val JdbcUsername: String = ""
    override lazy val JdbcPassword: String = ""
  }
}