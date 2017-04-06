package com.wix.example.users

import com.wix.example.service.UserData

trait UsersDao {
  def userFor(id: String): Option[UserData]
}

trait SiteDao {
  def ownerFor(siteId: String): Option[String]
}

