package com.wix.example.service

import com.wix.example.mailer.{EmailRequest, ExternalMailService}
import com.wix.example.users.{SiteDao, UsersDao}

trait UserMailerService {
  def sendMailTo(siteId: String, subject: String, body: String)
}

class DefaultUserMailerService(siteDao: SiteDao,
                               usersDao: UsersDao,
                               externalMailService: ExternalMailService)
  extends UserMailerService {

  def sendMailTo(siteId: String, subject: String, body: String) = {
    val userId = siteDao.ownerFor(siteId).getOrElse( throw new SiteDoesNotExist(siteId) )
    val userData = usersDao.userFor(userId).getOrElse( throw new UserDoesNotExist(userId) )
    externalMailService.sendMailTo(EmailRequest(userData.userEmail, subject, body))
  }
}

