package com.penelopec.calservice.infrastructure.calcom.adapter;

import com.penelopec.calservice.domain.entity.EventType;
import com.penelopec.calservice.domain.exception.EventTypeCreationException;
import com.penelopec.calservice.domain.exception.EventTypeDeletionException;
import com.penelopec.calservice.domain.exception.EventTypeNotFoundException;
import com.penelopec.calservice.infrastructure.calcom.dto.CalComApiResponse;
import com.penelopec.calservice.infrastructure.calcom.dto.CalComEventTypeRequest;
import com.penelopec.calservice.infrastructure.calcom.dto.CalComEventTypeResponse;
import com.penelopec.calservice.infrastructure.calcom.dto.CalComUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalComEventTypeAdapterTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private RestClient restClient;

    @InjectMocks
    private CalComEventTypeAdapter adapter;

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
                    .onStatus(ArgumentMatchers.<Predicate<HttpStatusCode>>any(), any(RestClient.ResponseSpec.ErrorHandler.class))
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
                    .onStatus(ArgumentMatchers.<Predicate<HttpStatusCode>>any(), any(RestClient.ResponseSpec.ErrorHandler.class))
                    .body(ArgumentMatchers.<ParameterizedTypeReference<CalComApiResponse<CalComEventTypeResponse>>>any()))
                    .thenReturn(new CalComApiResponse<>("success", null, null));

            // When / Then
            assertThatThrownBy(() -> adapter.create(eventType, false))
                    .isInstanceOf(EventTypeCreationException.class)
                    .hasMessageContaining("Resposta nula do Cal.com ao criar EventType");
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
                    .onStatus(ArgumentMatchers.<Predicate<HttpStatusCode>>any(), any(RestClient.ResponseSpec.ErrorHandler.class))
                    .body(ArgumentMatchers.<ParameterizedTypeReference<CalComApiResponse<CalComEventTypeResponse>>>any()))
                    .thenThrow(httpException);

                // When / Then
                assertThatThrownBy(() -> adapter.create(eventType, false))
                    .isInstanceOf(EventTypeCreationException.class)
                    .hasMessageContaining("Falha na comunicação com Cal.com ao criar EventType")
                    .hasCause(httpException);
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
                    .onStatus(ArgumentMatchers.<Predicate<HttpStatusCode>>any(), any(RestClient.ResponseSpec.ErrorHandler.class))
                    .body(ArgumentMatchers.<ParameterizedTypeReference<CalComApiResponse<CalComEventTypeResponse>>>any()))
                    .thenThrow(new RuntimeException("boom"));

                // When / Then
                assertThatThrownBy(() -> adapter.create(eventType, false))
                    .isInstanceOf(EventTypeCreationException.class)
                    .hasMessageContaining("Erro inesperado ao criar EventType no Cal.com")
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
                    .onStatus(ArgumentMatchers.<Predicate<HttpStatusCode>>any(), any(RestClient.ResponseSpec.ErrorHandler.class))
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
                    .onStatus(ArgumentMatchers.<Predicate<HttpStatusCode>>any(), any(RestClient.ResponseSpec.ErrorHandler.class))
                    .body(ArgumentMatchers.<ParameterizedTypeReference<CalComApiResponse<CalComEventTypeResponse>>>any()))
                    .thenReturn(new CalComApiResponse<>("success", null, null));

                // When / Then
                assertThatThrownBy(() -> adapter.update(eventType, false))
                    .isInstanceOf(EventTypeCreationException.class)
                    .hasMessageContaining("Resposta nula do Cal.com ao atualizar EventType 45");
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
                    .onStatus(ArgumentMatchers.<Predicate<HttpStatusCode>>any(), any(RestClient.ResponseSpec.ErrorHandler.class))
                    .toBodilessEntity())
                    .thenThrow(new RuntimeException("timeout"));

                // When / Then
                assertThatThrownBy(() -> adapter.delete(81L))
                    .isInstanceOf(EventTypeDeletionException.class)
                    .hasMessageContaining("Falha ao deletar EventType 81")
                    .hasCauseInstanceOf(RuntimeException.class);
            }

            @Test
            @DisplayName("Deve propagar EventTypeDeletionException sem encapsular novamente")
            void shouldRethrowEventTypeDeletionException() {
                // Given
                EventTypeDeletionException exception = new EventTypeDeletionException("erro de negócio");

                when(restClient.delete()
                    .uri("/v2/event-types/{id}", 44L)
                    .retrieve()
                    .onStatus(ArgumentMatchers.<Predicate<HttpStatusCode>>any(), any(RestClient.ResponseSpec.ErrorHandler.class))
                    .toBodilessEntity())
                    .thenThrow(exception);

                // When / Then
                assertThatThrownBy(() -> adapter.delete(44L))
                    .isInstanceOf(EventTypeDeletionException.class)
                    .hasMessageContaining("erro de negócio");
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
                    .onStatus(ArgumentMatchers.<Predicate<HttpStatusCode>>any(), any(RestClient.ResponseSpec.ErrorHandler.class))
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
                    .onStatus(ArgumentMatchers.<Predicate<HttpStatusCode>>any(), any(RestClient.ResponseSpec.ErrorHandler.class))
                    .body(ArgumentMatchers.<ParameterizedTypeReference<CalComApiResponse<CalComEventTypeResponse>>>any()))
                    .thenThrow(new EventTypeNotFoundException("não encontrado"));

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
                    .onStatus(ArgumentMatchers.<Predicate<HttpStatusCode>>any(), any(RestClient.ResponseSpec.ErrorHandler.class))
                    .body(ArgumentMatchers.<ParameterizedTypeReference<CalComApiResponse<CalComEventTypeResponse>>>any()))
                    .thenThrow(httpException(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error"));

                // When / Then
                assertThatThrownBy(() -> adapter.findById(eventTypeId))
                    .isInstanceOf(EventTypeNotFoundException.class)
                    .hasMessageContaining("Falha na comunicação com Cal.com ao buscar EventType 13");
            }

                @Test
                @DisplayName("Deve cobrir os dois ramos do predicate de status no findById")
                void shouldEvaluateBothBranchesOfFindByIdStatusPredicate() {
                    // Given
                    Long eventTypeId = 21L;
                    CalComEventTypeResponse response = responseOf(21L, "Visita", "visita", 60, "Desc", false, 120);

                    when(restClient.get()
                        .uri("/v2/event-types/{id}", eventTypeId)
                        .retrieve()
                        .onStatus(ArgumentMatchers.<Predicate<HttpStatusCode>>any(), any(RestClient.ResponseSpec.ErrorHandler.class))
                        .body(ArgumentMatchers.<ParameterizedTypeReference<CalComApiResponse<CalComEventTypeResponse>>>any()))
                        .thenReturn(new CalComApiResponse<>("success", response, null));

                    // When
                    adapter.findById(eventTypeId);

                    // Then
                    @SuppressWarnings("unchecked")
                    ArgumentCaptor<Predicate<HttpStatusCode>> statusCaptor =
                        (ArgumentCaptor<Predicate<HttpStatusCode>>) (ArgumentCaptor<?>) ArgumentCaptor.forClass(Predicate.class);

                    verify(restClient.get()
                        .uri("/v2/event-types/{id}", eventTypeId)
                        .retrieve())
                        .onStatus(statusCaptor.capture(), any(RestClient.ResponseSpec.ErrorHandler.class));

                    Predicate<HttpStatusCode> predicate = statusCaptor.getValue();
                    assertThat(predicate.test(HttpStatus.NOT_FOUND)).isTrue();
                    assertThat(predicate.test(HttpStatus.INTERNAL_SERVER_ERROR)).isFalse();
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
                    .isInstanceOf(EventTypeCreationException.class)
                    .hasMessageContaining("Não foi possível obter o usuário autenticado do Cal.com");
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
                    .isInstanceOf(EventTypeCreationException.class)
                    .hasMessageContaining("Falha na comunicação com Cal.com ao listar EventTypes")
                    .hasCauseInstanceOf(RestClientResponseException.class);
            }

            @Test
            @DisplayName("Deve encapsular erro inesperado ao listar EventTypes")
            void shouldWrapUnexpectedExceptionWhenListAllFailsUnexpectedly() {
                // Given
                when(restClient.get()
                    .uri("/v2/me")
                    .retrieve()
                    .body(ArgumentMatchers.<ParameterizedTypeReference<CalComApiResponse<CalComUser>>>any()))
                    .thenReturn(new CalComApiResponse<>("success", null, null));

                // When / Then
                assertThatThrownBy(() -> adapter.listAll())
                    .isInstanceOf(EventTypeCreationException.class)
                    .hasMessageContaining("Erro inesperado ao listar EventTypes no Cal.com")
                    .hasCauseInstanceOf(NullPointerException.class);
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
