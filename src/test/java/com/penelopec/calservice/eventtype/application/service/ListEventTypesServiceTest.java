package com.penelopec.calservice.eventtype.application.service;

import com.penelopec.calservice.eventtype.application.output.EventTypeOutput;
import com.penelopec.calservice.eventtype.domain.entity.EventType;
import com.penelopec.calservice.eventtype.domain.gateway.CalComEventTypeGateway;
import com.penelopec.calservice.eventtype.domain.repository.EventTypeRepository;
import com.penelopec.calservice.shared.pagination.Page;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListEventTypesServiceTest {

  @Mock
  private CalComEventTypeGateway calComGateway;

  @Mock
  private EventTypeRepository eventTypeRepository;

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

  @Nested
  @DisplayName("Enriquecimento de estateId")
  class EstateIdEnrichment {

    @Test
    @DisplayName("Deve enriquecer EventType com estateId do repositório quando ausente no calComGateway")
    void shouldEnrichEventTypeWithEstateIdFromRepository() {
      // Given
      EventType eventTypeFromCalCom = EventType.reconstitute(1L, "Visita A", "visita-a", "Desc A", 60, 120, false, null);
      EventType eventTypeFromRepository = EventType.reconstitute(1L, "Visita A", "visita-a", "Desc A", 60, 120, false, 10L);
      
      when(calComGateway.listAll()).thenReturn(List.of(eventTypeFromCalCom));
      when(eventTypeRepository.findAll()).thenReturn(List.of(eventTypeFromRepository));

      // When
      Page<EventTypeOutput> outputs = service.execute(0, 20);

      // Then
      assertThat(outputs.content()).hasSize(1);
      assertThat(outputs.content().get(0).id()).isEqualTo(1L);
      assertThat(outputs.content().get(0).estateId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("Deve retornar EventType com estateId original quando já preenchido")
    void shouldReturnOriginalEstateIdWhenAlreadyFilled() {
      // Given
      EventType eventType = EventType.reconstitute(1L, "Visita A", "visita-a", "Desc A", 60, 120, false, 10L);
      
      when(calComGateway.listAll()).thenReturn(List.of(eventType));
      when(eventTypeRepository.findAll()).thenReturn(List.of());

      // When
      Page<EventTypeOutput> outputs = service.execute(0, 20);

      // Then
      assertThat(outputs.content()).hasSize(1);
      assertThat(outputs.content().get(0).estateId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("Deve retornar EventType com estateId null quando não encontrar no repositório")
    void shouldReturnNullEstateIdWhenNotFoundInRepository() {
      // Given
      EventType eventTypeFromCalCom = EventType.reconstitute(1L, "Visita A", "visita-a", "Desc A", 60, 120, false, null);
      
      when(calComGateway.listAll()).thenReturn(List.of(eventTypeFromCalCom));
      when(eventTypeRepository.findAll()).thenReturn(List.of());

      // When
      Page<EventTypeOutput> outputs = service.execute(0, 20);

      // Then
      assertThat(outputs.content()).hasSize(1);
      assertThat(outputs.content().get(0).estateId()).isNull();
    }

    @Test
    @DisplayName("Deve enriquecer múltiplos EventTypes em uma única query ao repositório (sem N+1)")
    void shouldEnrichMultipleEventTypesWithSingleRepositoryQuery() {
      // Given
      EventType eventType1 = EventType.reconstitute(1L, "Visita A", "visita-a", "Desc A", 60, 120, false, null);
      EventType eventType2 = EventType.reconstitute(2L, "Visita B", "visita-b", "Desc B", 45, 90, true, null);
      EventType eventType3 = EventType.reconstitute(3L, "Visita C", "visita-c", "Desc C", 30, 60, false, 30L);
      
      EventType localEventType1 = EventType.reconstitute(1L, "Visita A", "visita-a", "Desc A", 60, 120, false, 10L);
      EventType localEventType2 = EventType.reconstitute(2L, "Visita B", "visita-b", "Desc B", 45, 90, true, 20L);
      
      when(calComGateway.listAll()).thenReturn(List.of(eventType1, eventType2, eventType3));
      when(eventTypeRepository.findAll()).thenReturn(List.of(localEventType1, localEventType2));

      // When
      Page<EventTypeOutput> outputs = service.execute(0, 20);

      // Then
      assertThat(outputs.content()).hasSize(3);
      assertThat(outputs.content().get(0).estateId()).isEqualTo(10L); // enriquecido
      assertThat(outputs.content().get(1).estateId()).isEqualTo(20L); // enriquecido
      assertThat(outputs.content().get(2).estateId()).isEqualTo(30L); // original
    }
  }
}
