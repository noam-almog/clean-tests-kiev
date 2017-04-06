package com.wix.example.service

import java.util.UUID

import com.wix.e2e.http.matchers.RequestMatchers._
import com.wix.e2e.http.server.WebServerFactory._
import com.wix.example.drivers.AsyncHttpClientProvider
import com.wix.example.mailer.{EmailRequest, ExternalMailService}
import com.wix.example.users.{SiteDao, UsersDao}
import org.specs2.matcher.ThrownExpectations
import org.specs2.mock.Mockito
import org.specs2.mutable.SpecWithJUnit
import org.specs2.specification.Scope


class UserMailerServiceTest extends SpecWithJUnit  {

  trait ctx extends Scope with Mockito with ThrownExpectations {

    val siteDao = mock[SiteDao]
    val siteId = UUID.randomUUID().toString

    val usersDao = mock[UsersDao]
    val userId = UUID.randomUUID().toString
    val userEmail = "someEmail@example.com"


    val subject = "someSubject"
    val body = "someBody"
    val mailServer = aStubWebServer.build.start()
    val externalMailService = new ExternalMailService(AsyncHttpClientProvider.httpClient, s"http://localhost:${mailServer.baseUri.port}")

    val userMailerService: UserMailerService = new DefaultUserMailerService(siteDao, usersDao, externalMailService)
  }


  "UserMailerService" should {

    "should throw an exception if site does not exists" in new ctx {
      siteDao.ownerFor(siteId) returns None

      userMailerService.sendMailTo(siteId, subject, body) must throwA[SiteDoesNotExist](siteId.toString)

      mailServer.recordedRequests must beEmpty
    }

    "fail for user id not found" in new ctx {
      siteDao.ownerFor(siteId) returns Some(userId)
      usersDao.userFor(userId) returns None

      userMailerService.sendMailTo(siteId, subject, body) must throwA[UserDoesNotExist](userId.toString)

      mailServer.recordedRequests must beEmpty
    }

    "find the owner email by site id and send an email to him" in new ctx {
      siteDao.ownerFor(siteId) returns Some(userId)
      usersDao.userFor(userId) returns Some(UserData(userId, userEmail))

      userMailerService.sendMailTo(siteId, subject, body)

      mailServer must
        receivedAnyRequestThat(must =
          haveBodyWith(entity =
            EmailRequest(userEmail, subject, body)))
    }
  }
}
