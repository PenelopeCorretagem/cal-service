package com.penelopec.calservice.infrastructure.monolith.adapter;

import com.penelopec.calservice.domain.exception.EventTypeCreationException;
import com.penelopec.calservice.domain.gateway.EstateData;
import com.penelopec.calservice.infrastructure.config.properties.MonolithProperties;
import com.penelopec.calservice.infrastructure.monolith.dto.MonolithEstateResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MonolithEstateAdapterTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private RestClient restClient;

    private MonolithEstateAdapter adapter;

    @BeforeEach
    void setUp() {
        MonolithProperties properties = new MonolithProperties(
                new MonolithProperties.Api("http://monolith", "token", "/api/estates")
        );
        adapter = new MonolithEstateAdapter(restClient, properties);
    }

    @Nested
    @DisplayName("fetchAllEstates")
    class FetchAllEstates {

        @Test
        @DisplayName("Deve mapear resposta do monolito para EstateData")
        void shouldMapMonolithResponseToEstateData() {
            // Given
            List<MonolithEstateResponse> response = List.of(
                    new MonolithEstateResponse(1L, "Emp 1", "Desc 1", true),
                    new MonolithEstateResponse(2L, "Emp 2", "Desc 2", null)
            );

            when(restClient.get()
                    .uri("/api/estates")
                    .retrieve()
                    .body(ArgumentMatchers.<ParameterizedTypeReference<List<MonolithEstateResponse>>>any()))
                    .thenReturn(response);

            // When
            List<EstateData> result = adapter.fetchAllEstates();

            // Then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).id()).isEqualTo(1L);
            assertThat(result.get(0).active()).isTrue();
            assertThat(result.get(1).id()).isEqualTo(2L);
            assertThat(result.get(1).active()).isFalse();
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando resposta vier nula")
        void shouldReturnEmptyListWhenResponseIsNull() {
            // Given
            when(restClient.get()
                    .uri("/api/estates")
                    .retrieve()
                    .body(ArgumentMatchers.<ParameterizedTypeReference<List<MonolithEstateResponse>>>any()))
                    .thenReturn(null);

            // When
            List<EstateData> result = adapter.fetchAllEstates();

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Deve lançar EventTypeCreationException em falha de integração")
        void shouldThrowEventTypeCreationExceptionOnIntegrationFailure() {
            // Given
            when(restClient.get()
                    .uri("/api/estates")
                    .retrieve()
                    .body(ArgumentMatchers.<ParameterizedTypeReference<List<MonolithEstateResponse>>>any()))
                    .thenThrow(new RuntimeException("timeout"));

            // When / Then
            assertThatThrownBy(() -> adapter.fetchAllEstates())
                    .isInstanceOf(EventTypeCreationException.class)
                    .hasMessageContaining("Falha ao buscar empreendimentos no monolito");
        }
    }
}
