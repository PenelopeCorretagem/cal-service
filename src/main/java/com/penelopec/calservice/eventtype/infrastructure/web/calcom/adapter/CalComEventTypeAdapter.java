package com.penelopec.calservice.eventtype.infrastructure.web.calcom.adapter;

import com.penelopec.calservice.eventtype.domain.entity.EventType;
import com.penelopec.calservice.eventtype.domain.error.EventTypeError;
import com.penelopec.calservice.eventtype.domain.gateway.CalComEventTypeGateway;
import com.penelopec.calservice.eventtype.domain.repository.EventTypeRepository;
import com.penelopec.calservice.shared.error.core.GatewayException;
import com.penelopec.calservice.eventtype.infrastructure.web.calcom.dto.CalComApiResponse;
import com.penelopec.calservice.eventtype.infrastructure.web.calcom.dto.CalComEventTypeRequest;
import com.penelopec.calservice.eventtype.infrastructure.web.calcom.dto.CalComEventTypeResponse;
import com.penelopec.calservice.eventtype.infrastructure.web.calcom.dto.CalComUser;
import com.penelopec.calservice.eventtype.infrastructure.web.calcom.mapper.CalComEventTypeMapper;
import com.penelopec.calservice.shared.http.exception.RemoteNotFoundException;
import com.penelopec.calservice.shared.http.exception.RemoteServiceException;
import com.penelopec.calservice.shared.http.executor.RestExecutor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class CalComEventTypeAdapter implements CalComEventTypeGateway {

  private static final String SYSTEM = "CALCOM";

  private final RestClient restClient;
  private final RestExecutor restExecutor;
  private final EventTypeRepository eventTypeRepository;

  private static final ParameterizedTypeReference<CalComApiResponse<CalComEventTypeResponse>>
    SINGLE_TYPE = new ParameterizedTypeReference<>() {};

  private static final ParameterizedTypeReference<CalComApiResponse<List<CalComEventTypeResponse>>>
    LIST_TYPE = new ParameterizedTypeReference<>() {};

  private static final ParameterizedTypeReference<CalComApiResponse<CalComUser>>
    USER_TYPE = new ParameterizedTypeReference<>() {};

  public CalComEventTypeAdapter(RestClient calRestClient, RestExecutor restExecutor, EventTypeRepository eventTypeRepository) {
    this.restClient = calRestClient;
    this.restExecutor = restExecutor;
    this.eventTypeRepository = eventTypeRepository;
  }

  @Override
  public EventType create(EventType eventType, boolean hidden) {
    CalComEventTypeRequest request = CalComEventTypeMapper.toRequest(eventType, hidden);
    try {
      CalComEventTypeResponse response = restExecutor.execute(SYSTEM, () ->
        Objects.requireNonNull(restClient.post()
            .uri("/v2/event-types")
            .body(request)
            .retrieve()
            .body(SINGLE_TYPE))
          .data()
      );

      eventType.assignExternalId(response.id());
      return CalComEventTypeMapper.toDomain(response, eventType.getEstateId());

    } catch (RemoteServiceException e) {
      throw new GatewayException(EventTypeError.CREATION_FAILED, e);
    }
  }

  @Override
  public EventType update(EventType eventType, boolean hidden) {
    CalComEventTypeRequest request = CalComEventTypeMapper.toRequest(eventType, hidden);
    try {
      CalComEventTypeResponse response = restExecutor.execute(SYSTEM, () ->
        Objects.requireNonNull(restClient.patch()
            .uri("/v2/event-types/{id}", eventType.getId())
            .body(request)
            .retrieve()
            .body(SINGLE_TYPE))
          .data()
      );

      return CalComEventTypeMapper.toDomain(response, eventType.getEstateId());

    } catch (RemoteServiceException e) {
      throw new GatewayException(EventTypeError.UPDATE_FAILED, e);
    }
  }

  @Override
  public void delete(Long eventTypeId) {
    try {
      restExecutor.executeVoid(SYSTEM, () ->
        restClient.delete()
          .uri("/v2/event-types/{id}", eventTypeId)
          .retrieve()
          .toBodilessEntity()
      );
    } catch (RemoteServiceException e) {
      throw new GatewayException(EventTypeError.DELETION_FAILED, e);
    }
  }

  @Override
  public Optional<EventType> findById(Long eventTypeId) {
    try {
      CalComEventTypeResponse response = restExecutor.executeOrNull(SYSTEM, () ->
        Objects.requireNonNull(restClient.get()
            .uri("/v2/event-types/{id}", eventTypeId)
            .retrieve()
            .body(SINGLE_TYPE))
          .data()
      );

      return Optional.ofNullable(response)
        .map(r -> {
          Long estateId = eventTypeRepository.findById(r.id())
              .map(EventType::getEstateId)
              .orElse(null);
          return CalComEventTypeMapper.toDomain(r, estateId);
        });

    } catch (RemoteNotFoundException e) {
      return Optional.empty();
    } catch (RemoteServiceException e) {
      throw new GatewayException(EventTypeError.INTEGRATION_UNAVAILABLE, e);
    }
  }

  @Override
  public List<EventType> listAll() {
    try {
      String username = getAuthenticatedUsername();

      List<CalComEventTypeResponse> responses = Optional.ofNullable(
          restExecutor.executeOrNull(SYSTEM, () ->
            restClient.get()
              .uri(uriBuilder -> uriBuilder
                .path("/v2/event-types")
                .queryParam("username", username)
                .build())
              .retrieve()
              .body(LIST_TYPE)))
        .map(CalComApiResponse::data)
        .orElse(List.of());

      return responses.stream()
        .map(r -> {
          Long estateId = eventTypeRepository.findById(r.id())
              .map(EventType::getEstateId)
              .orElse(null);
          return CalComEventTypeMapper.toDomain(r, estateId);
        })
        .toList();

    } catch (RemoteServiceException e) {
      throw new GatewayException(EventTypeError.INTEGRATION_UNAVAILABLE, e);
    }
  }

  private String getAuthenticatedUsername() {
    return Optional.ofNullable(
        restExecutor.executeOrNull(SYSTEM, () ->
          restClient.get()
            .uri("/v2/me")
            .retrieve()
            .body(USER_TYPE)))
      .map(wrapper -> wrapper.data().username())
      .orElseThrow(() -> new GatewayException(EventTypeError.EXTERNAL_USER_FETCH_FAILED));
  }
}