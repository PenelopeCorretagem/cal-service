package com.penelopec.calservice.user.infrastructure.web.adapter;

import com.penelopec.calservice.shared.error.core.CoreError;
import com.penelopec.calservice.shared.error.core.GatewayException;
import com.penelopec.calservice.shared.http.exception.RemoteNotFoundException;
import com.penelopec.calservice.shared.http.exception.RemoteServiceException;
import com.penelopec.calservice.shared.http.executor.RestExecutor;
import com.penelopec.calservice.user.domain.gateway.UserGateway;
import com.penelopec.calservice.user.domain.valueobject.UserSummary;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.client.RestClient;

import java.util.Optional;

public class UserServiceAdapter implements UserGateway {

  private static final String SYSTEM = "USER_SERVICE";

  private final RestClient restClient;
  private final RestExecutor restExecutor;

  public UserServiceAdapter(RestClient userRestClient, RestExecutor restExecutor) {
    this.restClient = userRestClient;
    this.restExecutor = restExecutor;
  }

  @Override
  public Optional<UserSummary> findById(Long userId) {
    if (userId == null) {
      return Optional.empty();
    }

    try {
      String authorization = resolveAuthorizationHeader();
      return Optional.ofNullable(
        restExecutor.executeOrNull(SYSTEM, () -> {
          var request = restClient.get()
            .uri("/v1/users/{id}", userId);
          if (authorization != null) {
            request = request.header("Authorization", authorization);
          }
          return request.retrieve().body(UserSummary.class);
        })
      );
    } catch (RemoteNotFoundException e) {
      return Optional.empty();
    } catch (RemoteServiceException e) {
      throw new GatewayException(CoreError.USER_GATEWAY_FAILED, e);
    }
  }

  private String resolveAuthorizationHeader() {
    var attributes = RequestContextHolder.getRequestAttributes();
    if (attributes instanceof ServletRequestAttributes servletAttributes) {
      String header = servletAttributes.getRequest().getHeader("Authorization");
      return header == null || header.isBlank() ? null : header;
    }

    return null;
  }
}
