package com.penelopec.calservice.eventtype.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "monolith")
public record MonolithProperties(Api api) {

  public record Api(String baseUrl, String token, String estatesPath) {
  }
}
