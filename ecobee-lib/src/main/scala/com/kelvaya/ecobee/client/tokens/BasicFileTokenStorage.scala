package com.kelvaya.ecobee.client.tokens

import com.kelvaya.ecobee.client.AccountID

import akka.event.Logging
import akka.event.LoggingBus

import spray.json._
import spray.json.DefaultJsonProtocol._

import zio.IO
import zio.Ref


/** [[TokenStorage]] backed by a simple JSON-based file.
  *
  * @note This storage is not suited for production-level code.
  *
  * @param file The file containing all of the tokens
  * @param tokens Tuple of list of tokens loaded from the file and closed status of storage handle
  * @param lb (implicit) Used for logging
  */
class BasicFileTokenStorage private (file : better.files.File, tokensRef : Ref[(Map[AccountID,Tokens],Boolean)])(implicit lb : LoggingBus)
extends TokenStorage {
  import BasicFileTokenStorage._

  val tokenStorage = new TokenStorage.Service[Any] {

    def close(): IO[TokenStorageError,Unit] = 
      for {
        _ <- tokensRef.update(r => (r._1,true))
      } yield (())

    def getTokens(account: AccountID): IO[TokenStorageError,Tokens] = {
      for {
        tr   <- tokensRef.get
        tok  <- {
          if (tr._2) IO.fail(TokenStorageError.StorageClosedError)
          else {
            val opt = tr._1.get(account)
            opt.map(IO.succeed).getOrElse(IO.fail(TokenStorageError.InvalidAccountError))
          }
        }
      } 
      yield tok
    }

    def storeTokens(account: AccountID, tokens: Tokens): IO[TokenStorageError,Unit] = {
      for {
        tr  <- tokensRef.get
        tok <- if (!tr._2) tokensRef.update(r => (r._1.updated(account, tokens),r._2)) else IO.fail(TokenStorageError.StorageClosedError)
        _   <- storeToDirectory(file, tok._1)
      } yield (())
    }
  }
}


/** Factory for [[BasicFileTokenStorage]]
  *
  * Call `BasicFileTokenStorage.connect(<file>)` to create a new instance.
  */
object BasicFileTokenStorage {
  import better.files._

  final case class TokenList(client : String, tokens : Tokens)
  final case class FileTokens(tokens : Seq[TokenList])

  private implicit final val tokensJsonImplicit : RootJsonFormat[Tokens] = DefaultJsonProtocol.jsonFormat3(Tokens)
  private implicit final val tokenListJsonImplicit : RootJsonFormat[TokenList] = DefaultJsonProtocol.jsonFormat2(TokenList)
  private implicit final val fileTokensJsonImplicit : RootJsonFormat[FileTokens] = DefaultJsonProtocol.jsonFormat1(FileTokens)

  private final val TokenFileMaxLength = 32

  /** Returns the [[BasicFileTokenStorage]] located at the given file */
  def connect(file : java.io.File)(implicit lb : LoggingBus) : IO[TokenStorageError,BasicFileTokenStorage] = connect(file.toScala)

  /** Returns the [[BasicFileTokenStorage]] located at the given file */
  def connect(file : better.files.File)(implicit lb : LoggingBus) : IO[TokenStorageError,BasicFileTokenStorage] =
    connectToDirectory(file)



  // ###############################################################
  // ###############################################################

  private def connectToDirectory(file : better.files.File)(implicit lb : LoggingBus) : IO[TokenStorageError,BasicFileTokenStorage] = {
    
    IO.fromEither {
      val log = Logging(lb, BasicFileTokenStorage.getClass)
      log.info(s"Opening $file for reading tokens")
      if (!file.exists || !file.isReadable) {
        log.error(s"Fatal Error: Token storage file invalid: $file.  Check permissions.")
        Left(TokenStorageError.ConnectionError)
      }
      else {
        import scala.collection.mutable.Map
        val map = Map.empty[AccountID,Tokens]
        try {
          val fTokens = {
            val f = file.contentAsString
            if (f.isEmpty) FileTokens(Seq.empty)
            else f.parseJson.convertTo[FileTokens]
          }
          fTokens.tokens.foreach { tl =>
            val found = map.put(new AccountID(tl.client),tl.tokens)
            if (found.isDefined) log.warning(s"Client defined in tokens more than once. `$found` is being overwritten by `${tl.tokens}` from file `$file`")
          }
          Right(map.toMap)
        }
        catch {
          case _ : DeserializationException => {
            log.error(s"Invalid file found for token storage: $file.  Consider removing it.")
            Left(TokenStorageError.ConnectionError)
          }
        }
      }
    }
    .flatMap(t => Ref.make((t,false)))
    .map(new BasicFileTokenStorage(file, _))
  }


  def storeToDirectory(file : better.files.File, tokens : Map[AccountID,Tokens])(implicit lb: LoggingBus) = {
    IO.fromEither {
      val log = Logging(lb, BasicFileTokenStorage.getClass)
      log.info(s"Writing out all tokens to file $file")
      if (!file.exists || !file.isRegularFile) {
        log.warning(s"DATA LOSS POSSIBLE! Cannot save tokens; token storage file invalid: $file")
        Left(TokenStorageError.ConnectionError)
      }
      else {
        val list = new FileTokens(tokens.map { case (k,v) => new TokenList(k.id, v) }.toSeq)
        val json = list.toJson.asJsObject
        file.overwrite(json.prettyPrint)
        log.info(s"Completed writing out all tokens to file $file")
        Right(())
      }
    }
  }

}