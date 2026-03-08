package com.penelopec.calservice.infrastructure.config;

import com.penelopec.calservice.infrastructure.config.properties.CalcomProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class CalClientConfig {

  private final CalcomProperties prop;

  public CalClientConfig(CalcomProperties prop) {
    this.prop = prop;
  }

  @Bean
  public RestClient calRestClient() {
    return RestClient.builder()
      .baseUrl(prop.api().baseUrl())
      .defaultHeader("Authorization", "Bearer " + prop.api().key())
      .defaultHeader("cal-api-version", prop.api().versionV2())
      .build();
  }
}