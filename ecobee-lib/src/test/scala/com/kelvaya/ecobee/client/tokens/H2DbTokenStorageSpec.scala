package com.kelvaya.ecobee.client.tokens

import com.kelvaya.ecobee.client.tokens.H2DbTokenStorage.DbError
import com.kelvaya.ecobee.config.Settings
import com.kelvaya.ecobee.test._

import better.files.File

import doobie.implicits._

import zio.Task
import zio.UIO
import zio.interop.catz._

import org.scalatest.compatible.Assertion

class H2DbTokenStorageSpec extends BaseTestSpec with TokenStorageBehavior with ZioTest {

  import deps.Implicits.{SettingsImplicit=>_,_}

  "Connection parsing" must "support enabling or disabling autocreation" in {
    val enabled = "jdbc:h2:/data/sample;IFEXISTS=TRUE"
    val missing = "jdbc:h2:/data/sample"
    val bad = "jdbc:h2:/data/sample;IFEXISTS=xx"
    val middle = "jdbc:h2:/data/sample;IFEXISTS=TRUE;OTHER=123"

    H2DbTokenStorage.parseConnectionString(enabled, true) shouldBe Right(missing)
    H2DbTokenStorage.parseConnectionString(enabled, false) shouldBe Right(enabled)

    H2DbTokenStorage.parseConnectionString(missing, true) shouldBe Right(missing)
    H2DbTokenStorage.parseConnectionString(missing, false) shouldBe Right(enabled)

    H2DbTokenStorage.parseConnectionString(middle, true) shouldBe Right(missing + ";OTHER=123")
    H2DbTokenStorage.parseConnectionString(middle, false) shouldBe Right(middle)

    H2DbTokenStorage.parseConnectionString(bad, true) should matchPattern { case Left(DbError.InvalidConnection(_)) => }
    H2DbTokenStorage.parseConnectionString(bad, false) should matchPattern { case Left(DbError.InvalidConnection(_)) => }
  }


  "The H2 database token storage driver" must "be initializable" in {
    val test = createTempDb { implicit s =>
      
      val t1 = H2DbTokenStorage.initDb.map { db =>
        for {
          init <- db.use(_ => Task(true))
          a    <- init shouldBe true
        }
        yield a
      }

      val t2 = H2DbTokenStorage.createConn(false).map { _.use { xa => 
        for {
          cnt <- sql"select count(*) from token".query[Int].unique.transact(xa)
          a   <- cnt shouldBe 0
        } yield a
      }}

      val connOk = Task((t1.isRight && t2.isRight) shouldBe true)

      lazy val t1r = t1.right.get
      lazy val t2r = t2.right.get

      // t1 should succeed the first time, but should be an exception the second time
      // NB: The "mapError" will change the Exception error type into a Assertion "error" type 
      // (to keep the types correct for "leftOrFailException"). 
      connOk *> t1r *> t1r.mapError(_ => succeed).either.leftOrFailException *> t2r
      
    }

    run(test)
  }


  it must behave like storage(usingStore)



  it must "return an error if the DB does not exist" in {
    val test = createTempDb { implicit s =>
      val dbQueryResultTest : Either[DbError,Task[Assertion]] = H2DbTokenStorage.connect.map { db =>
        db.use { store => 
          
          val queryRun = for {
            _ <- store.tokenStorage.getTokens(account)
          } yield fail("Should not have succeeded")
          
          queryRun.flip.flatMap(e => e shouldBe TokenStorageError.ConnectionError)
        }
      }

      dbQueryResultTest.isRight shouldBe true
      dbQueryResultTest.right.get
    }
    run(test)
  }


  it must "properly store the tokens in an H2 database" in {
    val test = createTempDb { implicit s =>

      // create test database and alter record
      val setup = withTestStore { s =>
        s.storeTokens(account, Tokens(Some("mytest"), None, None)).flatMap(_ => succeed)
      }
      setup.isRight shouldBe true


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
      
      setup.right.get *> verify
    }

    run(test)
  }



  // Provides an auto-created-and-deleted H2DbTokenStorage to the given function
  // Is used as the function for the TokenStorageBehavior tests.
  private def usingStore(actions : TokenStorage.Service[Any] => Task[Assertion]) : Task[Assertion] = {
    createTempDb { implicit settings =>
      val result = withTestStore { store => actions(store) }
      result.isRight shouldBe true
      result.right.get
    }
  }


  private def cleanup(f : File) = UIO(f.delete())

  private def createTempDb(fn : Settings => Task[Assertion]) : Task[Assertion] = {
    Task(File.newTemporaryDirectory(prefix=s"h2dts")).bracket(cleanup)  { dir => 
      val settings = new H2DbTokenStorageTestSettings(s"jdbc:h2:${dir.toString}/test.db")
      fn(settings)
    }
  }


  private def withTestStore[S](fn : TokenStorage.Service[Any] => Task[S])(implicit s : Settings) = {
    val conn = H2DbTokenStorage.initDb
    conn.map(_.use { store =>
      val sql = 
        sql"insert into token (account, authToken, accessToken, refreshToken) values ($account, $AuthCode, $AccessToken, $RefreshToken)"
        .update

      sql.run.transact(store.xa).flatMap(_ => fn(store.tokenStorage))
    })
  }
}


class H2DbTokenStorageTestSettings(jdbcConnection : String) extends TestSettings {
  override lazy val JdbcConnection = jdbcConnection
}