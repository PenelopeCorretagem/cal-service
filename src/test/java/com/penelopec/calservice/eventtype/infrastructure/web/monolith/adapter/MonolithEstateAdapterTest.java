package com.penelopec.calservice.eventtype.infrastructure.web.monolith.adapter;

import com.penelopec.calservice.eventtype.domain.error.EventTypeError;
import com.penelopec.calservice.eventtype.infrastructure.web.monolith.dto.AdvertisementResponse;
import com.penelopec.calservice.eventtype.infrastructure.web.monolith.dto.EstateResponse;
import com.penelopec.calservice.shared.error.core.GatewayException;
import com.penelopec.calservice.eventtype.infrastructure.config.properties.MonolithProperties;
import com.penelopec.calservice.shared.http.exception.RemoteServiceException;
import com.penelopec.calservice.shared.http.executor.RestExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MonolithEstateAdapterTest {

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private RestClient restClient;

  @Mock
  private RestExecutor restExecutor;

  private MonolithAdvertisementAdapter adapter;

  @BeforeEach
  void setUp() {
    MonolithProperties properties = new MonolithProperties(
      new MonolithProperties.Api("http://monolith", "token", "/v1/advertisements")
    );
    adapter = new MonolithAdvertisementAdapter(restClient, properties, restExecutor);

    when(restExecutor.executeOrNull(anyString(), any()))
      .thenAnswer(inv -> {
        try {
          return inv.<Supplier<?>>getArgument(1).get();
        } catch (RemoteServiceException e) {
          throw e;
        } catch (Exception e) {
          throw new RemoteServiceException("MONOLITH", "Erro inesperado", e);
        }
      });
  }

  @Nested
  @DisplayName("fetchAllAdvertisements")
  class FetchAllAdvertisements {

    @Test
    @DisplayName("Deve retornar lista de AdvertisementResponse do monolito")
    void shouldReturnAdvertisementResponses() {
      // Given
      List<AdvertisementResponse> response = List.of(
        new AdvertisementResponse(true, new EstateResponse(1L, "Emp 1", "Desc 1", null)),
        new AdvertisementResponse(false, new EstateResponse(2L, "Emp 2", "Desc 2", null))
      );

      when(restClient.get()
        .uri("/v1/advertisements")
        .retrieve()
        .body(ArgumentMatchers.<ParameterizedTypeReference<List<AdvertisementResponse>>>any()))
        .thenReturn(response);

      // When
      List<AdvertisementResponse> result = adapter.fetchAllAdvertisements();

      // Then
      assertThat(result).hasSize(2);
      assertThat(result.get(0).estate().id()).isEqualTo(1L);
      assertThat(result.get(0).active()).isTrue();
      assertThat(result.get(1).estate().id()).isEqualTo(2L);
      assertThat(result.get(1).active()).isFalse();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando resposta vier nula")
    void shouldReturnEmptyListWhenResponseIsNull() {
      // Given
      when(restClient.get()
        .uri("/v1/advertisements")
        .retrieve()
        .body(ArgumentMatchers.<ParameterizedTypeReference<List<AdvertisementResponse>>>any()))
        .thenReturn(null);

      // When
      List<AdvertisementResponse> result = adapter.fetchAllAdvertisements();

      // Then
      assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Deve lançar GatewayException com SYNC_FAILED em falha de integração")
    void shouldThrowGatewayExceptionOnIntegrationFailure() {
      // Given
      when(restClient.get()
        .uri("/v1/advertisements")
        .retrieve()
        .body(ArgumentMatchers.<ParameterizedTypeReference<List<AdvertisementResponse>>>any()))
        .thenThrow(new RuntimeException("timeout"));

      // When / Then
      assertThatThrownBy(() -> adapter.fetchAllAdvertisements())
        .isInstanceOf(GatewayException.class)
        .satisfies(ex -> assertThat(((GatewayException) ex).error()).isEqualTo(EventTypeError.SYNC_FAILED));
    }
  }
}
