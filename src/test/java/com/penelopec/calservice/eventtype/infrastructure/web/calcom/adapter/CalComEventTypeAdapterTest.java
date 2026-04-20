package com.penelopec.calservice.eventtype.infrastructure.web.calcom.adapter;

import com.penelopec.calservice.eventtype.domain.entity.EventType;
import com.penelopec.calservice.eventtype.domain.error.EventTypeError;
import com.penelopec.calservice.eventtype.infrastructure.web.calcom.adapter.CalComEventTypeAdapter;
import com.penelopec.calservice.eventtype.infrastructure.web.calcom.dto.CalComApiResponse;
import com.penelopec.calservice.eventtype.infrastructure.web.calcom.dto.CalComEventTypeRequest;
import com.penelopec.calservice.eventtype.infrastructure.web.calcom.dto.CalComEventTypeResponse;
import com.penelopec.calservice.eventtype.infrastructure.web.calcom.dto.CalComUser;
import com.penelopec.calservice.shared.error.core.GatewayException;
import com.penelopec.calservice.shared.http.exception.RemoteNotFoundException;
import com.penelopec.calservice.shared.http.exception.RemoteServiceException;
import com.penelopec.calservice.shared.http.executor.RestExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalComEventTypeAdapterTest {

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private RestClient restClient;

  @Mock
  private RestExecutor restExecutor;

  @InjectMocks
  private CalComEventTypeAdapter adapter;

  @BeforeEach
  void setUp() {
    lenient().when(restExecutor.execute(anyString(), any())).thenAnswer(inv -> {
      try {
        Object result = inv.<Supplier<?>>getArgument(1).get();
        if (result == null) throw new RemoteServiceException("CALCOM", "Resposta nula inesperada", null);
        return result;
      } catch (RemoteServiceException e) {
        throw e;
      } catch (RestClientResponseException e) {
        if (e.getStatusCode().value() == 404) throw new RemoteNotFoundException("CALCOM", e.getMessage());
        throw new RemoteServiceException("CALCOM", e.getStatusCode().value(), e.getMessage());
      } catch (Exception e) {
        throw new RemoteServiceException("CALCOM", "Erro inesperado", e);
      }
    });
    lenient().when(restExecutor.executeOrNull(anyString(), any())).thenAnswer(inv -> {
      try {
        return inv.<Supplier<?>>getArgument(1).get();
      } catch (RemoteServiceException e) {
        throw e;
      } catch (RestClientResponseException e) {
        if (e.getStatusCode().value() == 404) throw new RemoteNotFoundException("CALCOM", e.getMessage());
        throw new RemoteServiceException("CALCOM", e.getStatusCode().value(), e.getMessage());
      } catch (Exception e) {
        throw new RemoteServiceException("CALCOM", "Erro inesperado", e);
      }
    });
    lenient().doAnswer(inv -> {
      try {
        inv.<Runnable>getArgument(1).run();
      } catch (RemoteServiceException e) {
        throw e;
      } catch (RestClientResponseException e) {
        if (e.getStatusCode().value() == 404) throw new RemoteNotFoundException("CALCOM", e.getMessage());
        throw new RemoteServiceException("CALCOM", e.getStatusCode().value(), e.getMessage());
      } catch (Exception e) {
        throw new RemoteServiceException("CALCOM", "Erro inesperado", e);
      }
      return null;
    }).when(restExecutor).executeVoid(anyString(), any());
  }

  @Nested
  @DisplayName("create")
  class Create {

    @Test
    @DisplayName("Deve criar EventType quando Cal.com retornar dados válidos")
    void shouldCreateEventTypeWhenCalComReturnsValidData() {
      // Given
      EventType eventType = newEventType();
      CalComEventTypeResponse response = responseOf(501L, "Visita", "visita", 45, "Desc", false, 90);

      when(restClient.post()
        .uri("/v2/event-types")
        .body(any(CalComEventTypeRequest.class))
        .retrieve()
        .body(ArgumentMatchers.<ParameterizedTypeReference<CalComApiResponse<CalComEventTypeResponse>>>any()))
        .thenReturn(new CalComApiResponse<>("success", response, null));

      // When
      EventType result = adapter.create(eventType, false);

      // Then
      assertThat(result.getId()).isEqualTo(501L);
      assertThat(result.getTitle()).isEqualTo("Visita");
      assertThat(eventType.getId()).isEqualTo(501L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando resposta de criação vier sem data")
    void shouldThrowWhenCreateResponseHasNoData() {
      // Given
      EventType eventType = EventType.createNew("Visita", "Desc", 60, 120, false, 10L);

      when(restClient.post()
        .uri("/v2/event-types")
        .body(any(CalComEventTypeRequest.class))
        .retrieve()
        .body(ArgumentMatchers.<ParameterizedTypeReference<CalComApiResponse<CalComEventTypeResponse>>>any()))
        .thenReturn(new CalComApiResponse<>("success", null, null));

      // When / Then
      assertThatThrownBy(() -> adapter.create(eventType, false))
        .isInstanceOf(GatewayException.class)
        .satisfies(ex -> assertThat(((GatewayException) ex).error()).isEqualTo(EventTypeError.CREATION_FAILED));
    }

    @Test
    @DisplayName("Deve encapsular erro HTTP ao criar EventType")
    void shouldWrapHttpExceptionWhenCreateFailsByHttp() {
      // Given
      EventType eventType = newEventType();
      RestClientResponseException httpException = httpException(HttpStatus.BAD_REQUEST, "Bad Request");

      when(restClient.post()
        .uri("/v2/event-types")
        .body(any(CalComEventTypeRequest.class))
        .retrieve()
        .body(ArgumentMatchers.<ParameterizedTypeReference<CalComApiResponse<CalComEventTypeResponse>>>any()))
        .thenThrow(httpException);

      // When / Then
      assertThatThrownBy(() -> adapter.create(eventType, false))
        .isInstanceOf(GatewayException.class)
        .satisfies(ex -> assertThat(((GatewayException) ex).error()).isEqualTo(EventTypeError.CREATION_FAILED))
        .hasCauseInstanceOf(RemoteServiceException.class);
    }

    @Test
    @DisplayName("Deve encapsular erro inesperado ao criar EventType")
    void shouldWrapUnexpectedExceptionWhenCreateFails() {
      // Given
      EventType eventType = newEventType();

      when(restClient.post()
        .uri("/v2/event-types")
        .body(any(CalComEventTypeRequest.class))
        .retrieve()
        .body(ArgumentMatchers.<ParameterizedTypeReference<CalComApiResponse<CalComEventTypeResponse>>>any()))
        .thenThrow(new RuntimeException("boom"));

      // When / Then
      assertThatThrownBy(() -> adapter.create(eventType, false))
        .isInstanceOf(GatewayException.class)
        .satisfies(ex -> assertThat(((GatewayException) ex).error()).isEqualTo(EventTypeError.CREATION_FAILED))
        .hasCauseInstanceOf(RuntimeException.class);
    }
  }

  @Nested
  @DisplayName("update")
  class Update {

    @Test
    @DisplayName("Deve atualizar EventType quando Cal.com retornar dados válidos")
    void shouldUpdateEventTypeWhenCalComReturnsValidData() {
      // Given
      EventType eventType = existingEventType(99L);
      CalComEventTypeResponse response = responseOf(99L, "Tour", "tour", 30, "Novo", true, 120);

      when(restClient.patch()
        .uri("/v2/event-types/{id}", 99L)
        .body(any(CalComEventTypeRequest.class))
        .retrieve()
        .body(ArgumentMatchers.<ParameterizedTypeReference<CalComApiResponse<CalComEventTypeResponse>>>any()))
        .thenReturn(new CalComApiResponse<>("success", response, null));

      // When
      EventType result = adapter.update(eventType, true);

      // Then
      assertThat(result.getId()).isEqualTo(99L);
      assertThat(result.isHidden()).isTrue();
      assertThat(result.getLengthInMinutes()).isEqualTo(30);
    }

    @Test
    @DisplayName("Deve lançar exceção quando resposta de atualização vier sem data")
    void shouldThrowWhenUpdateResponseHasNoData() {
      // Given
      EventType eventType = existingEventType(45L);

      when(restClient.patch()
        .uri("/v2/event-types/{id}", 45L)
        .body(any(CalComEventTypeRequest.class))
        .retrieve()
        .body(ArgumentMatchers.<ParameterizedTypeReference<CalComApiResponse<CalComEventTypeResponse>>>any()))
        .thenReturn(new CalComApiResponse<>("success", null, null));

      // When / Then
      assertThatThrownBy(() -> adapter.update(eventType, false))
        .isInstanceOf(GatewayException.class)
        .satisfies(ex -> assertThat(((GatewayException) ex).error()).isEqualTo(EventTypeError.UPDATE_FAILED));
    }
  }

  @Nested
  @DisplayName("delete")
  class Delete {

    @Test
    @DisplayName("Deve lançar exceção de domínio quando integração falhar ao deletar")
    void shouldThrowDomainExceptionWhenDeleteIntegrationFails() {
      // Given
      when(restClient.delete()
        .uri("/v2/event-types/{id}", 81L)
        .retrieve()
        .toBodilessEntity())
        .thenThrow(new RuntimeException("timeout"));

      // When / Then
      assertThatThrownBy(() -> adapter.delete(81L))
        .isInstanceOf(GatewayException.class)
        .satisfies(ex -> assertThat(((GatewayException) ex).error()).isEqualTo(EventTypeError.DELETION_FAILED))
        .hasCauseInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Deve propagar EventTypeDeletionException sem encapsular novamente")
    void shouldRethrowEventTypeDeletionException() {
      // Given
      GatewayException exception = new GatewayException(EventTypeError.DELETION_FAILED);

      when(restClient.delete()
        .uri("/v2/event-types/{id}", 44L)
        .retrieve()
        .toBodilessEntity())
        .thenThrow(exception);

      // When / Then
      assertThatThrownBy(() -> adapter.delete(44L))
        .isInstanceOf(GatewayException.class)
        .satisfies(ex -> assertThat(((GatewayException) ex).error()).isEqualTo(EventTypeError.DELETION_FAILED));
    }
  }

  @Nested
  @DisplayName("findById")
  class FindById {

    @Test
    @DisplayName("Deve retornar EventType quando busca por id encontrar dado")
    void shouldReturnEventTypeWhenFindByIdSucceeds() {
      // Given
      Long eventTypeId = 77L;
      CalComEventTypeResponse response = responseOf(77L, "Visita", "visita", 60, "Desc", false, 120);

      when(restClient.get()
        .uri("/v2/event-types/{id}", eventTypeId)
        .retrieve()
        .body(ArgumentMatchers.<ParameterizedTypeReference<CalComApiResponse<CalComEventTypeResponse>>>any()))
        .thenReturn(new CalComApiResponse<>("success", response, null));

      // When
      Optional<EventType> result = adapter.findById(eventTypeId);

      // Then
      assertThat(result).isPresent();
      assertThat(result.get().getId()).isEqualTo(77L);
      assertThat(result.get().getEstateId()).isNull();
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando EventType não existir")
    void shouldReturnEmptyOptionalWhenEventTypeDoesNotExist() {
      // Given
      Long eventTypeId = 99L;
      when(restClient.get()
        .uri("/v2/event-types/{id}", eventTypeId)
        .retrieve()
        .body(ArgumentMatchers.<ParameterizedTypeReference<CalComApiResponse<CalComEventTypeResponse>>>any()))
        .thenThrow(httpException(HttpStatus.NOT_FOUND, "Not Found"));

      // When
      Optional<EventType> result = adapter.findById(eventTypeId);

      // Then
      assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Deve lançar exceção quando houver erro HTTP ao buscar por id")
    void shouldThrowWhenFindByIdHasHttpError() {
      // Given
      Long eventTypeId = 13L;

      when(restClient.get()
        .uri("/v2/event-types/{id}", eventTypeId)
        .retrieve()
        .body(ArgumentMatchers.<ParameterizedTypeReference<CalComApiResponse<CalComEventTypeResponse>>>any()))
        .thenThrow(httpException(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error"));

      // When / Then
      assertThatThrownBy(() -> adapter.findById(eventTypeId))
        .isInstanceOf(GatewayException.class)
        .satisfies(ex -> assertThat(((GatewayException) ex).error()).isEqualTo(EventTypeError.INTEGRATION_UNAVAILABLE));
    }

    @Test
    @DisplayName("Deve retornar Optional vazio para 404 e GatewayException para 5xx")
    void shouldEvaluateBothBranchesOfFindByIdStatusPredicate() {
      // Given
      Long eventTypeId = 21L;

      // 404 deve retornar Optional.empty()
      doThrow(new RemoteNotFoundException("CALCOM", "Not Found"))
        .when(restExecutor).executeOrNull(anyString(), any());

      assertThat(adapter.findById(eventTypeId)).isEmpty();

      // 5xx deve lançar GatewayException
      doThrow(new RemoteServiceException("CALCOM", 500, "Server Error"))
        .when(restExecutor).executeOrNull(anyString(), any());

      assertThatThrownBy(() -> adapter.findById(eventTypeId))
        .isInstanceOf(GatewayException.class)
        .satisfies(ex -> assertThat(((GatewayException) ex).error()).isEqualTo(EventTypeError.INTEGRATION_UNAVAILABLE));
    }
  }

  @Nested
  @DisplayName("listAll")
  class ListAll {

    @Test
    @DisplayName("Deve listar EventTypes do usuário autenticado")
    void shouldListAllEventTypesFromAuthenticatedUser() {
      // Given
      when(restClient.get()
        .uri("/v2/me")
        .retrieve()
        .body(ArgumentMatchers.<ParameterizedTypeReference<CalComApiResponse<CalComUser>>>any()))
        .thenReturn(new CalComApiResponse<>("success", new CalComUser(10L, "kenner", "k@cal.com", "Kenner"), null));

      List<CalComEventTypeResponse> responses = List.of(
        responseOf(1L, "A", "a", 60, "Desc A", false, 30),
        responseOf(2L, "B", "b", 45, "Desc B", true, 10)
      );

      when(restClient.get()
        .uri(ArgumentMatchers.<Function<UriBuilder, URI>>any())
        .retrieve()
        .body(ArgumentMatchers.<ParameterizedTypeReference<CalComApiResponse<List<CalComEventTypeResponse>>>>any()))
        .thenReturn(new CalComApiResponse<>("success", responses, null));

      // When
      List<EventType> result = adapter.listAll();

      // Then
      assertThat(result).hasSize(2);
      assertThat(result.get(0).getId()).isEqualTo(1L);
      assertThat(result.get(1).isHidden()).isTrue();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando API de listagem vier nula")
    void shouldReturnEmptyListWhenListResponseIsNull() {
      // Given
      when(restClient.get()
        .uri("/v2/me")
        .retrieve()
        .body(ArgumentMatchers.<ParameterizedTypeReference<CalComApiResponse<CalComUser>>>any()))
        .thenReturn(new CalComApiResponse<>("success", new CalComUser(10L, "kenner", "k@cal.com", "Kenner"), null));

      when(restClient.get()
        .uri(ArgumentMatchers.<Function<UriBuilder, URI>>any())
        .retrieve()
        .body(ArgumentMatchers.<ParameterizedTypeReference<CalComApiResponse<List<CalComEventTypeResponse>>>>any()))
        .thenReturn(null);

      // When
      List<EventType> result = adapter.listAll();

      // Then
      assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Deve lançar exceção quando não conseguir obter usuário autenticado")
    void shouldThrowWhenAuthenticatedUserCannotBeResolved() {
      // Given
      when(restClient.get()
        .uri("/v2/me")
        .retrieve()
        .body(ArgumentMatchers.<ParameterizedTypeReference<CalComApiResponse<CalComUser>>>any()))
        .thenReturn(null);

      // When / Then
      assertThatThrownBy(() -> adapter.listAll())
        .isInstanceOf(GatewayException.class)
        .satisfies(ex -> assertThat(((GatewayException) ex).error()).isEqualTo(EventTypeError.EXTERNAL_USER_FETCH_FAILED));
    }

    @Test
    @DisplayName("Deve encapsular erro HTTP ao listar EventTypes")
    void shouldWrapHttpExceptionWhenListAllFails() {
      // Given
      when(restClient.get()
        .uri("/v2/me")
        .retrieve()
        .body(ArgumentMatchers.<ParameterizedTypeReference<CalComApiResponse<CalComUser>>>any()))
        .thenReturn(new CalComApiResponse<>("success", new CalComUser(10L, "kenner", "k@cal.com", "Kenner"), null));

      when(restClient.get()
        .uri(ArgumentMatchers.<Function<UriBuilder, URI>>any())
        .retrieve()
        .body(ArgumentMatchers.<ParameterizedTypeReference<CalComApiResponse<List<CalComEventTypeResponse>>>>any()))
        .thenThrow(httpException(HttpStatus.BAD_GATEWAY, "Bad Gateway"));

      // When / Then
      assertThatThrownBy(() -> adapter.listAll())
        .isInstanceOf(GatewayException.class)
        .satisfies(ex -> assertThat(((GatewayException) ex).error()).isEqualTo(EventTypeError.INTEGRATION_UNAVAILABLE))
        .hasCauseInstanceOf(RemoteServiceException.class);
    }

    @Test
    @DisplayName("Deve encapsular erro inesperado ao listar EventTypes")
    void shouldWrapUnexpectedExceptionWhenListAllFailsUnexpectedly() {
      // Given
      when(restClient.get()
        .uri("/v2/me")
        .retrieve()
        .body(ArgumentMatchers.<ParameterizedTypeReference<CalComApiResponse<CalComUser>>>any()))
        .thenReturn(new CalComApiResponse<>("success", new CalComUser(10L, "kenner", "k@cal.com", "Kenner"), null));

      when(restClient.get()
        .uri(ArgumentMatchers.<Function<UriBuilder, URI>>any())
        .retrieve()
        .body(ArgumentMatchers.<ParameterizedTypeReference<CalComApiResponse<List<CalComEventTypeResponse>>>>any()))
        .thenThrow(new RuntimeException("unexpected error"));

      // When / Then
      assertThatThrownBy(() -> adapter.listAll())
        .isInstanceOf(GatewayException.class)
        .satisfies(ex -> assertThat(((GatewayException) ex).error()).isEqualTo(EventTypeError.INTEGRATION_UNAVAILABLE))
        .hasCauseInstanceOf(RemoteServiceException.class);
    }
  }

  private static EventType newEventType() {
    return EventType.createNew("Visita", "Desc", 60, 120, false, 10L);
  }

  private static EventType existingEventType(Long id) {
    return EventType.reconstitute(id, "Tour", "tour", "Desc", 60, 120, false, 10L);
  }

  private static CalComEventTypeResponse responseOf(
    Long id,
    String title,
    String slug,
    Integer length,
    String description,
    Boolean hidden,
    Integer minimumBookingNotice
  ) {
    return new CalComEventTypeResponse(
      id,
      title,
      slug,
      length,
      description,
      hidden,
      minimumBookingNotice,
      OffsetDateTime.now(),
      OffsetDateTime.now()
    );
  }

  private static RestClientResponseException httpException(HttpStatus status, String statusText) {
    return new HttpClientErrorException(
      status,
      statusText,
      HttpHeaders.EMPTY,
      new byte[0],
      StandardCharsets.UTF_8
    );
  }
}
