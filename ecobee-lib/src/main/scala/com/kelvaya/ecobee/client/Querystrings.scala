package com.kelvaya.ecobee.client

import com.kelvaya.ecobee.config.Settings

object Querystrings {
  type Querystring = Tuple2[String, String]

  val JsonFormat: Querystring = (("format", "json"))

  object ResponseType {
    val EcobeePIN: Querystring = (("response_type", "ecobeePin"))
  }

  object Scope {
    val SmartWrite: Querystring = (("scope", PinScope.SmartWrite.toString()))
    val SmartRead: Querystring = (("scope", PinScope.SmartRead.toString()))
  }

  object GrantType {
    val Pin : Querystring = (("grant_type", "ecobeePin"))
  }

  private var _clientId: Option[Querystring] = None
  def ClientId(implicit settings: Settings) = _clientId.getOrElse {
    _clientId = Some(("client_id", settings.EcobeeAppKey))
    _clientId.get
  }
}