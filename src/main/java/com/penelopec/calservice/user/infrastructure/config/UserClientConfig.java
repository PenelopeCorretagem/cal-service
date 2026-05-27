package com.penelopec.calservice.user.infrastructure.config;

import com.penelopec.calservice.shared.http.config.RestClientBuilderFactory;
import com.penelopec.calservice.shared.http.executor.RestExecutor;
import com.penelopec.calservice.user.domain.gateway.UserGateway;
import com.penelopec.calservice.user.infrastructure.web.adapter.UserServiceAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class UserClientConfig {

  @Bean
  public RestClient userRestClient(
      @Value("${user-service.base-url:http://localhost:8081/api}") String baseUrl,
      RestClientBuilderFactory restClientBuilderFactory) {
    return restClientBuilderFactory
      .builder(baseUrl)
      .build();
  }

  @Bean
  public UserGateway userGateway(RestClient userRestClient, RestExecutor restExecutor) {
    return new UserServiceAdapter(userRestClient, restExecutor);
  }
}
