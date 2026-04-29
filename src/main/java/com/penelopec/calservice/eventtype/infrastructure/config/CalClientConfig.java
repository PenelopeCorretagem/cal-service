package com.penelopec.calservice.eventtype.infrastructure.config;

import com.penelopec.calservice.eventtype.infrastructure.config.properties.CalcomProperties;
import com.penelopec.calservice.shared.http.config.RestClientBuilderFactory;
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
  public RestClient calRestClient(RestClientBuilderFactory restClientBuilderFactory) {
    return restClientBuilderFactory
      .builder(prop.api().baseUrl())
      .defaultHeader("Authorization", "Bearer " + prop.api().key())
      .defaultHeader("cal-api-version", prop.api().versionV1())
      .build();
  }
}