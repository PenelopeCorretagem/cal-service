package com.penelopec.calservice.eventtype.infrastructure.web.monolith.adapter;

import com.penelopec.calservice.eventtype.domain.error.EventTypeError;
import com.penelopec.calservice.eventtype.domain.valueobject.EstateData;
import com.penelopec.calservice.eventtype.domain.gateway.EstateGateway;
import com.penelopec.calservice.shared.error.core.GatewayException;
import com.penelopec.calservice.eventtype.infrastructure.config.properties.MonolithProperties;
import com.penelopec.calservice.eventtype.infrastructure.web.monolith.dto.MonolithEstateResponse;
import com.penelopec.calservice.shared.http.exception.RemoteServiceException;
import com.penelopec.calservice.shared.http.executor.RestExecutor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class MonolithEstateAdapter implements EstateGateway {

  private final RestClient restClient;
  private final MonolithProperties properties;
  private final RestExecutor restExecutor;

  private static final ParameterizedTypeReference<List<MonolithEstateResponse>>
    LIST_TYPE = new ParameterizedTypeReference<>() {
  };

  public MonolithEstateAdapter(RestClient monolithRestClient, MonolithProperties properties,
                               RestExecutor restExecutor) {
    this.restClient = monolithRestClient;
    this.properties = properties;
    this.restExecutor = restExecutor;
  }

  @Override
  public List<EstateData> fetchAllEstates() {
    try {
      List<MonolithEstateResponse> responses = restExecutor.executeOrNull("MONOLITH", () ->
        restClient.get()
          .uri(properties.api().estatesPath())
          .retrieve()
          .body(LIST_TYPE)
      );

      if (responses == null) {
        return List.of();
      }

      return responses.stream()
        .map(this::toEstateData)
        .toList();

    } catch (RemoteServiceException e) {
      throw new GatewayException(EventTypeError.SYNC_FAILED, e);
    }
  }

  private EstateData toEstateData(MonolithEstateResponse response) {
    return new EstateData(
      response.id(),
      response.nome(),
      response.descricao(),
      response.ativo() != null && response.ativo()
    );
  }
}
