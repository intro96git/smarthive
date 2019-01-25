package com.kelvaya.ecobee.client


/** Returned on paged content responses, or can be used to request specific pages */
case class Page(page : Option[Int], totalPages : Option[Int], pageSize : Option[Int], total : Option[Int])