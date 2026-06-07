package com.penelopec.calservice.appointment.infrastructure.web.monolith.adapter;

import com.penelopec.calservice.appointment.domain.gateway.UserDirectoryGateway;
import com.penelopec.calservice.appointment.infrastructure.web.monolith.dto.MonolithUserResponse;
import com.penelopec.calservice.shared.http.executor.RestExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Component
public class MonolithUserDirectoryAdapter implements UserDirectoryGateway {

  private final RestClient restClient;
  private final RestExecutor restExecutor;

  public MonolithUserDirectoryAdapter(RestClient monolithRestClient, RestExecutor restExecutor) {
    this.restClient = monolithRestClient;
    this.restExecutor = restExecutor;
  }

  @Override
  public Map<Long, String> fetchUserNames(Set<Long> userIds) {
    Map<Long, String> names = new LinkedHashMap<>();
    if (userIds == null || userIds.isEmpty()) {
      return names;
    }

    for (Long userId : userIds) {
      if (userId == null || names.containsKey(userId)) {
        continue;
      }

      MonolithUserResponse response = restExecutor.executeOrNull("MONOLITH", () ->
        restClient.get()
          .uri("/v1/users/{id}", userId)
          .retrieve()
          .body(MonolithUserResponse.class)
      );

      names.put(userId, response != null && response.name() != null ? response.name() : "");
    }

    return names;
  }
}
