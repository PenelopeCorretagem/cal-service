package com.penelopec.calservice.shared.http.config;

import com.penelopec.calservice.shared.http.log.LoggingInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class RestClientBuilderFactory {

  private final LoggingInterceptor loggingInterceptor;

  public RestClientBuilderFactory(LoggingInterceptor loggingInterceptor) {
    this.loggingInterceptor = loggingInterceptor;
  }

  public RestClient.Builder builder(String baseUrl) {
    return RestClient.builder()
      .baseUrl(baseUrl)
      .requestInterceptors(interceptors -> interceptors.add(loggingInterceptor));
  }
}