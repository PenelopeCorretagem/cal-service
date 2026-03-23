package com.penelopec.calservice.eventtype.infrastructure.web.monolith.adapter;

import com.penelopec.calservice.eventtype.domain.exception.EventTypeCreationException;
import com.penelopec.calservice.eventtype.domain.gateway.EstateData;
import com.penelopec.calservice.eventtype.domain.gateway.EstateGateway;
import com.penelopec.calservice.eventtype.infrastructure.config.properties.MonolithProperties;
import com.penelopec.calservice.eventtype.infrastructure.web.monolith.dto.MonolithEstateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class MonolithEstateAdapter implements EstateGateway {

  private static final Logger log = LoggerFactory.getLogger(MonolithEstateAdapter.class);

  private final RestClient restClient;
  private final MonolithProperties properties;

  private static final ParameterizedTypeReference<List<MonolithEstateResponse>>
    LIST_TYPE = new ParameterizedTypeReference<>() {
  };

  public MonolithEstateAdapter(RestClient monolithRestClient, MonolithProperties properties) {
    this.restClient = monolithRestClient;
    this.properties = properties;
  }

  @Override
  public List<EstateData> fetchAllEstates() {
    try {
      List<MonolithEstateResponse> responses = restClient.get()
        .uri(properties.api().estatesPath())
        .retrieve()
        .body(LIST_TYPE);

      if (responses == null) {
        return List.of();
      }

      return responses.stream()
        .map(this::toEstateData)
        .toList();

    } catch (Exception e) {
      log.error("Erro ao buscar empreendimentos no monolito: {}", e.getMessage(), e);
      throw new EventTypeCreationException("Falha ao buscar empreendimentos no monolito", e);
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
