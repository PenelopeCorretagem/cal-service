package com.penelopec.calservice.eventtype.infrastructure.config;

import com.penelopec.calservice.eventtype.infrastructure.config.properties.MonolithProperties;
import com.penelopec.calservice.shared.http.config.RestClientBuilderFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(MonolithProperties.class)
public class MonolithClientConfig {

  @Bean
  public RestClient monolithRestClient(MonolithProperties properties,
                                       RestClientBuilderFactory restClientBuilderFactory) {
    RestClient.Builder builder = restClientBuilderFactory.builder(properties.api().baseUrl());

    String token = properties.api().token();
    if (token != null && !token.isBlank()) {
      builder.defaultHeader("Authorization", "Bearer " + token);
    }

    return builder.build();
  }
}
