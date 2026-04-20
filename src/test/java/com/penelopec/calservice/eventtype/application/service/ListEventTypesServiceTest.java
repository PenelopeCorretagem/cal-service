package com.penelopec.calservice.eventtype.application.service;

import com.penelopec.calservice.eventtype.application.output.EventTypeOutput;
import com.penelopec.calservice.eventtype.domain.entity.EventType;
import com.penelopec.calservice.eventtype.domain.gateway.CalComEventTypeGateway;
import com.penelopec.calservice.shared.pagination.Page;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListEventTypesServiceTest {

  @Mock
  private CalComEventTypeGateway calComGateway;

  @InjectMocks
  private ListEventTypesService service;

  @Nested
  @DisplayName("execute")
  class Execute {

    @Test
    @DisplayName("Deve listar e mapear event types")
    void shouldListAndMapEventTypes() {
      // Given
      EventType first = EventType.reconstitute(1L, "Visita A", "visita-a", "Desc A", 60, 120, false, 10L);
      EventType second = EventType.reconstitute(2L, "Visita B", "visita-b", "Desc B", 45, 90, true, 20L);
      when(calComGateway.listAll()).thenReturn(List.of(first, second));

      // When
      Page<EventTypeOutput> outputs = service.execute(0, 1);

      // Then
      assertThat(outputs.content()).hasSize(1);
      assertThat(outputs.content().get(0).id()).isEqualTo(1L);
      assertThat(outputs.content().get(0).slug()).isEqualTo("visita-a");
      assertThat(outputs.page()).isEqualTo(0);
      assertThat(outputs.size()).isEqualTo(1);
      assertThat(outputs.totalElements()).isEqualTo(2);
      assertThat(outputs.totalPages()).isEqualTo(2);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver dados")
    void shouldReturnEmptyListWhenNoData() {
      // Given
      when(calComGateway.listAll()).thenReturn(List.of());

      // When
      Page<EventTypeOutput> outputs = service.execute(0, 20);

      // Then
      assertThat(outputs.content()).isEmpty();
      assertThat(outputs.page()).isEqualTo(0);
      assertThat(outputs.size()).isEqualTo(20);
      assertThat(outputs.totalElements()).isZero();
      assertThat(outputs.totalPages()).isZero();
    }

    @Test
    @DisplayName("Deve usar defaults quando página e tamanho forem inválidos")
    void shouldUseDefaultsWhenPageAndSizeAreInvalid() {
      // Given
      EventType first = EventType.reconstitute(1L, "Visita A", "visita-a", "Desc A", 60, 120, false, 10L);
      when(calComGateway.listAll()).thenReturn(List.of(first));

      // When
      Page<EventTypeOutput> outputs = service.execute(-5, 0);

      // Then
      assertThat(outputs.page()).isEqualTo(0);
      assertThat(outputs.size()).isEqualTo(20);
      assertThat(outputs.totalElements()).isEqualTo(1);
      assertThat(outputs.totalPages()).isEqualTo(1);
      assertThat(outputs.content()).hasSize(1);
    }
  }
}
