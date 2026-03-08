package com.penelopec.calservice.infrastructure.calcom.adapter;

import com.penelopec.calservice.domain.entity.EventType;
import com.penelopec.calservice.domain.exception.EventTypeCreationException;
import com.penelopec.calservice.domain.exception.EventTypeDeletionException;
import com.penelopec.calservice.domain.exception.EventTypeNotFoundException;
import com.penelopec.calservice.domain.gateway.CalComEventTypeGateway;
import com.penelopec.calservice.infrastructure.calcom.dto.CalComApiResponse;
import com.penelopec.calservice.infrastructure.calcom.dto.CalComEventTypeRequest;
import com.penelopec.calservice.infrastructure.calcom.dto.CalComEventTypeResponse;
import com.penelopec.calservice.infrastructure.calcom.dto.CalComUser;
import com.penelopec.calservice.infrastructure.calcom.mapper.CalComEventTypeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.Optional;

@Component
public class CalComEventTypeAdapter implements CalComEventTypeGateway {

    private static final Logger log = LoggerFactory.getLogger(CalComEventTypeAdapter.class);

    private final RestClient restClient;

    private static final ParameterizedTypeReference<CalComApiResponse<CalComEventTypeResponse>>
            SINGLE_TYPE = new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<CalComApiResponse<List<CalComEventTypeResponse>>>
            LIST_TYPE = new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<CalComApiResponse<CalComUser>>
            USER_TYPE = new ParameterizedTypeReference<>() {};

    public CalComEventTypeAdapter(RestClient calRestClient) {
        this.restClient = calRestClient;
    }

    @Override
    public EventType create(EventType eventType, boolean hidden) {
        CalComEventTypeRequest request = CalComEventTypeMapper.toRequest(eventType, hidden);

        try {
            CalComEventTypeResponse response = restClient.post()
                    .uri("/v2/event-types")
                    .body(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (req, res) -> {
                        throw new EventTypeCreationException(
                                "Cal.com retornou status " + res.getStatusCode() + " ao criar EventType");
                    })
                    .body(SINGLE_TYPE)
                    .data();

            if (response == null) {
                throw new EventTypeCreationException("Resposta nula do Cal.com ao criar EventType");
            }

            eventType.assignExternalId(response.id());
            return CalComEventTypeMapper.toDomain(response, eventType.getEstateId());

        } catch (EventTypeCreationException e) {
            throw e;
        } catch (RestClientResponseException e) {
            log.error("Erro HTTP ao criar EventType no Cal.com: {}", e.getStatusText(), e);
            throw new EventTypeCreationException("Falha na comunicação com Cal.com ao criar EventType", e);
        } catch (Exception e) {
            log.error("Erro inesperado ao criar EventType no Cal.com", e);
            throw new EventTypeCreationException("Erro inesperado ao criar EventType no Cal.com", e);
        }
    }

    @Override
    public EventType update(EventType eventType, boolean hidden) {
        CalComEventTypeRequest request = CalComEventTypeMapper.toRequest(eventType, hidden);

        try {
            CalComEventTypeResponse response = restClient.patch()
                    .uri("/v2/event-types/{id}", eventType.getId())
                    .body(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (req, res) -> {
                        throw new EventTypeCreationException(
                                "Cal.com retornou status " + res.getStatusCode()
                                        + " ao atualizar EventType " + eventType.getId());
                    })
                    .body(SINGLE_TYPE)
                    .data();

            if (response == null) {
                throw new EventTypeCreationException(
                        "Resposta nula do Cal.com ao atualizar EventType " + eventType.getId());
            }

            return CalComEventTypeMapper.toDomain(response, eventType.getEstateId());

        } catch (EventTypeCreationException e) {
            throw e;
        } catch (RestClientResponseException e) {
            log.error("Erro HTTP ao atualizar EventType {} no Cal.com: {}", eventType.getId(), e.getStatusText(), e);
            throw new EventTypeCreationException(
                    "Falha na comunicação com Cal.com ao atualizar EventType " + eventType.getId(), e);
        } catch (Exception e) {
            log.error("Erro inesperado ao atualizar EventType {} no Cal.com", eventType.getId(), e);
            throw new EventTypeCreationException(
                    "Erro inesperado ao atualizar EventType " + eventType.getId(), e);
        }
    }

    @Override
    public void delete(Long eventTypeId) {
        try {
            restClient.delete()
                    .uri("/v2/event-types/{id}", eventTypeId)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (req, res) -> {
                        throw new EventTypeDeletionException(
                                "Cal.com retornou status " + res.getStatusCode()
                                        + " ao deletar EventType " + eventTypeId);
                    })
                    .toBodilessEntity();

        } catch (EventTypeDeletionException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao deletar EventType {} no Cal.com", eventTypeId, e);
            throw new EventTypeDeletionException("Falha ao deletar EventType " + eventTypeId, e);
        }
    }

    @Override
    public Optional<EventType> findById(Long eventTypeId) {
        try {
            CalComEventTypeResponse response = restClient.get()
                    .uri("/v2/event-types/{id}", eventTypeId)
                    .retrieve()
                    .onStatus(status -> status.value() == 404, (req, res) -> {
                        throw new EventTypeNotFoundException(
                                "EventType não encontrado no Cal.com: " + eventTypeId);
                    })
                    .body(SINGLE_TYPE)
                    .data();

            return Optional.ofNullable(response)
                    .map(r -> CalComEventTypeMapper.toDomain(r, null));

        } catch (EventTypeNotFoundException e) {
            return Optional.empty();
        } catch (RestClientResponseException e) {
            log.error("Erro HTTP ao buscar EventType {} no Cal.com: {}", eventTypeId, e.getStatusText(), e);
            throw new EventTypeNotFoundException(
                    "Falha na comunicação com Cal.com ao buscar EventType " + eventTypeId);
        }
    }

    @Override
    public List<EventType> listAll() {
        try {
            String username = getAuthenticatedUsername();

            List<CalComEventTypeResponse> responses = Optional.ofNullable(
                    restClient.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/v2/event-types")
                                    .queryParam("username", username)
                                    .build())
                            .retrieve()
                            .body(LIST_TYPE))
                    .map(CalComApiResponse::data)
                    .orElse(List.of());

            return responses.stream()
                    .map(r -> CalComEventTypeMapper.toDomain(r, null))
                    .toList();

        } catch (EventTypeCreationException e) {
            throw e;
        } catch (RestClientResponseException e) {
            log.error("Erro HTTP ao listar EventTypes no Cal.com: {}", e.getStatusText(), e);
            throw new EventTypeCreationException("Falha na comunicação com Cal.com ao listar EventTypes", e);
        } catch (Exception e) {
            log.error("Erro ao listar EventTypes no Cal.com", e);
            throw new EventTypeCreationException("Erro inesperado ao listar EventTypes no Cal.com", e);
        }
    }

    private String getAuthenticatedUsername() {
        return Optional.ofNullable(
                restClient.get()
                        .uri("/v2/me")
                        .retrieve()
                        .body(USER_TYPE))
                .map(wrapper -> wrapper.data().username())
                .orElseThrow(() -> new EventTypeCreationException(
                        "Não foi possível obter o usuário autenticado do Cal.com"));
    }
}
