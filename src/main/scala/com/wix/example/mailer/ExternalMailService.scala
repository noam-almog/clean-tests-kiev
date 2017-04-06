package com.wix.example.mailer

import java.nio.charset.Charset

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.asynchttpclient.{AsyncCompletionHandler, AsyncHttpClient, Response}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future, Promise}

class ExternalMailService(httpClient: AsyncHttpClient, baseUrl: String) {
  def sendMailTo(emailRequest: EmailRequest): Unit = {
    Await.result( issueApiCall(emailRequest), 3.seconds )
  }

  private def issueApiCall(emailRequest: EmailRequest): Future[Unit] = {
    val p = httpClient.preparePost(s"$baseUrl/mailer")
    p.setBody(objectMapper.writeValueAsBytes(emailRequest: EmailRequest))
    val promise = Promise[Unit]()
    p.execute(new AsyncCompletionHandler[Unit]() {
      def onCompleted(response: Response) {
        if (response.getStatusCode == 200)
              promise success response.getResponseBody(Charset.forName("UTF-8"))
        else throw new RuntimeException
      }
      override def onThrowable(t: Throwable) = promise failure t
    })
    promise.future
  }

  private val objectMapper = new ObjectMapper().registerModules(new DefaultScalaModule)
}


case class EmailRequest(to: String, subject: String, body: String)