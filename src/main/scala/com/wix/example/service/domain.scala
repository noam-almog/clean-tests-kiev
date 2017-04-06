package com.wix.example.service

case class UserData(userId: String, userEmail: String)
case class SiteData(siteId: String, userId: String, url: String)

class UserDoesNotExist(userId: String) extends RuntimeException(s"Cannot locate user data for $userId")

class SiteDoesNotExist(siteId: String) extends RuntimeException(s"Cannot locate site for $siteId")
