package com.kelvaya.ecobee.test.server

import com.kelvaya.ecobee.client.AccountID
import com.kelvaya.ecobee.test.client.TestConstants

trait ServerTestConstants extends TestConstants {
  val Account = new AccountID("test")
}