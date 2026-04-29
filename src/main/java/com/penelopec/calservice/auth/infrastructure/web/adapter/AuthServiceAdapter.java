package com.penelopec.calservice.auth.infrastructure.web.adapter;

import com.penelopec.calservice.auth.application.output.LoginOutput;
import com.penelopec.calservice.auth.application.output.ValidateTokenOutput;
import com.penelopec.calservice.auth.domain.gateway.AuthGateway;
import com.penelopec.calservice.shared.error.core.CoreError;
import com.penelopec.calservice.shared.error.core.GatewayException;
import com.penelopec.calservice.shared.http.exception.RemoteServiceException;
import com.penelopec.calservice.shared.http.executor.RestExecutor;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.Map;

public class AuthServiceAdapter implements AuthGateway {

  private final RestClient restClient;
  private final RestExecutor restExecutor;

  public AuthServiceAdapter(RestClient authRestClient, RestExecutor restExecutor) {
    this.restClient = authRestClient;
    this.restExecutor = restExecutor;
  }

  @Override
  public LoginOutput login(String email, String password) {
    try {
      return restExecutor.execute("AUTH_SERVICE", () ->
          restClient.post()
              .uri("/v1/auth/login")
              .contentType(MediaType.APPLICATION_JSON)
              .body(Map.of("email", email, "password", password))
              .retrieve()
              .body(LoginOutput.class)
      );
    } catch (RemoteServiceException e) {
      throw new GatewayException(CoreError.AUTH_GATEWAY_FAILED, e);
    }
  }

  @Override
  public ValidateTokenOutput validateToken(String token) {
    try {
      return restExecutor.execute("AUTH_SERVICE", () ->
          restClient.post()
              .uri("/v1/auth/validate-access-token")
              .contentType(MediaType.APPLICATION_JSON)
              .body(Map.of("token", token))
              .retrieve()
              .body(ValidateTokenOutput.class)
      );
    } catch (RemoteServiceException e) {
      throw new GatewayException(CoreError.AUTH_GATEWAY_FAILED, e);
    }
  }
}
