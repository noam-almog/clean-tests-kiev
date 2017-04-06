package com.wix.example.drivers

import java.util.concurrent.Executors

import org.asynchttpclient.{AsyncHttpClient, DefaultAsyncHttpClient, DefaultAsyncHttpClientConfig}

object AsyncHttpClientProvider {
  private val threadFactory = Executors.defaultThreadFactory()

  val httpClient: AsyncHttpClient =
    new DefaultAsyncHttpClient(
      new DefaultAsyncHttpClientConfig.Builder()
                                      .setConnectTimeout( 3000 )
                                      .setRequestTimeout( 3000 )
                                      .setThreadFactory( threadFactory )
                                      .build )
}
