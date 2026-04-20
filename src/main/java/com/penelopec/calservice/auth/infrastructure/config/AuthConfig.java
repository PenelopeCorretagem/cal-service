package com.penelopec.calservice.auth.infrastructure.config;

import com.penelopec.calservice.auth.application.port.in.AuthenticateUseCase;
import com.penelopec.calservice.auth.application.port.in.ValidateTokenUseCase;
import com.penelopec.calservice.auth.application.service.AuthenticateService;
import com.penelopec.calservice.auth.application.service.ValidateTokenService;
import com.penelopec.calservice.auth.domain.gateway.AuthGateway;
import com.penelopec.calservice.auth.infrastructure.web.adapter.AuthServiceAdapter;
import com.penelopec.calservice.shared.http.config.RestClientBuilderFactory;
import com.penelopec.calservice.shared.http.executor.RestExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AuthConfig {

  @Bean
  public RestClient authRestClient(
      @Value("${auth-service.base-url:http://localhost:9000/api}") String baseUrl,
      RestClientBuilderFactory restClientBuilderFactory) {
    return restClientBuilderFactory
      .builder(baseUrl)
      .build();
  }

  @Bean
  public AuthGateway authGateway(RestClient authRestClient, RestExecutor restExecutor) {
    return new AuthServiceAdapter(authRestClient, restExecutor);
  }

  @Bean
  public AuthenticateUseCase authenticateUseCase(AuthGateway authGateway) {
    return new AuthenticateService(authGateway);
  }

  @Bean
  public ValidateTokenUseCase validateTokenUseCase(AuthGateway authGateway) {
    return new ValidateTokenService(authGateway);
  }
}
