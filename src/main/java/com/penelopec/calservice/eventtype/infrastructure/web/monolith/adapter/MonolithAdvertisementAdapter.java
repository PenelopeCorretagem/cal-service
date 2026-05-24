package com.penelopec.calservice.eventtype.infrastructure.web.monolith.adapter;

import com.penelopec.calservice.eventtype.domain.error.EventTypeError;
import com.penelopec.calservice.eventtype.domain.gateway.AdvertisementGateway;
import com.penelopec.calservice.eventtype.infrastructure.web.monolith.dto.AdvertisementResponse;
import com.penelopec.calservice.shared.error.core.GatewayException;
import com.penelopec.calservice.eventtype.infrastructure.config.properties.MonolithProperties;
import com.penelopec.calservice.shared.http.exception.RemoteServiceException;
import com.penelopec.calservice.shared.http.executor.RestExecutor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class MonolithAdvertisementAdapter implements AdvertisementGateway {

  private final RestClient restClient;
  private final MonolithProperties properties;
  private final RestExecutor restExecutor;

  private static final ParameterizedTypeReference<List<AdvertisementResponse>>
    LIST_TYPE = new ParameterizedTypeReference<>() {
  };

  public MonolithAdvertisementAdapter(RestClient monolithRestClient, MonolithProperties properties,
                                      RestExecutor restExecutor) {
    this.restClient = monolithRestClient;
    this.properties = properties;
    this.restExecutor = restExecutor;
  }

  @Override
  public List<AdvertisementResponse> fetchAllAdvertisements() {

    try {
      List<AdvertisementResponse> responses = restExecutor.executeOrNull("MONOLITH", () ->
        restClient.get()
          .uri(properties.api().advertisementsPath())
          .retrieve()
          .body(LIST_TYPE)
      );

      if (responses == null) {
        return List.of();
      }

      return responses;

    } catch (RemoteServiceException e) {
      throw new GatewayException(EventTypeError.SYNC_FAILED, e);
    }
  }
}
