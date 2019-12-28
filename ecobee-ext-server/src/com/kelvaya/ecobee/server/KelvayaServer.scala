package com.kelvaya.ecobee.server

import io.finch._

import com.twitter.finagle.Http
import com.twitter.server.TwitterServer
import com.twitter.util.Await

import zio.DefaultRuntime
import zio.Task
import zio.interop.catz._



object KelvayaServer extends TwitterServer with Endpoint.Module[Task] {

  implicit private lazy val _rt = new DefaultRuntime { }
  private lazy val _endpoints = get("wtf") { Ok("hello world") }
  private lazy val _service = Bootstrap.configure().serve[Text.Plain](_endpoints).toService
  
  def main() : Unit = {
    val server = Http.serve(":7777", _service)

    onExit { val _ = server.close() }

    val _ = Await.ready(this.adminHttpServer)
  }
}