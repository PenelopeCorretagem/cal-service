package com.penelopec.calservice.application.service;

import com.penelopec.calservice.eventtype.application.service.SyncEventTypesService;
import com.penelopec.calservice.eventtype.domain.entity.EventType;
import com.penelopec.calservice.eventtype.domain.gateway.CalComEventTypeGateway;
import com.penelopec.calservice.eventtype.domain.gateway.EstateData;
import com.penelopec.calservice.eventtype.domain.gateway.EstateGateway;
import com.penelopec.calservice.eventtype.domain.repository.EventTypeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SyncEventTypesServiceTest {

  @Mock
  private EstateGateway estateGateway;

  @Mock
  private CalComEventTypeGateway calComGateway;

  @Mock
  private EventTypeRepository eventTypeRepository;

  @InjectMocks
  private SyncEventTypesService service;

  @Nested
  @DisplayName("execute")
  class Execute {

    @Test
    @DisplayName("Deve falhar quando houver EventTypes duplicados por empreendimento")
    void shouldFail_whenDuplicateEstateIdsExist() {
      // Given
      EventType first = EventType.reconstitute(1L, "Visita A", "visita-a", "Desc", 60, 120, false, 99L);
      EventType second = EventType.reconstitute(2L, "Visita B", "visita-b", "Desc", 60, 120, false, 99L);

      when(estateGateway.fetchAllEstates()).thenReturn(List.of(new EstateData(99L, "Emp", "Desc", true)));
      when(eventTypeRepository.findAll()).thenReturn(List.of(first, second));

      // When / Then
      assertThatThrownBy(() -> service.execute())
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("duplicados por empreendimento");
    }

    @Test
    @DisplayName("Deve criar EventType para empreendimento ativo sem vínculo existente")
    void shouldCreateEventTypeForActiveEstateWithoutExistingBinding() {
      // Given
      EstateData estate = new EstateData(101L, "Empreendimento A", "Desc A", true);
      EventType created = EventType.reconstitute(5001L, "Empreendimento A", "empreendimento-a", "Desc A", 60, 120, false, 101L);

      when(estateGateway.fetchAllEstates()).thenReturn(List.of(estate));
      when(eventTypeRepository.findAll()).thenReturn(List.of());
      when(calComGateway.create(any(EventType.class), eq(false))).thenReturn(created);
      when(eventTypeRepository.save(created)).thenReturn(created);

      // When
      service.execute();

      // Then
      verify(calComGateway).create(any(EventType.class), eq(false));
      verify(eventTypeRepository).save(created);
    }

    @Test
    @DisplayName("Deve atualizar EventType quando título ou descrição mudarem")
    void shouldUpdateEventTypeWhenTitleOrDescriptionChanges() {
      // Given
      EstateData estate = new EstateData(202L, "Nome Novo", "Descrição Nova", true);
      EventType existing = EventType.reconstitute(88L, "Nome Antigo", "nome-antigo", "Descrição Antiga", 60, 120, false, 202L);

      when(estateGateway.fetchAllEstates()).thenReturn(List.of(estate));
      when(eventTypeRepository.findAll()).thenReturn(List.of(existing));
      when(eventTypeRepository.save(existing)).thenReturn(existing);

      // When
      service.execute();

      // Then
      verify(calComGateway).update(existing, false);
      verify(eventTypeRepository).save(existing);
    }

    @Test
    @DisplayName("Deve reativar EventType oculto quando empreendimento estiver ativo")
    void shouldReactivateHiddenEventTypeWhenEstateIsActive() {
      // Given
      EstateData estate = new EstateData(303L, "Emp", "Desc", true);
      EventType hidden = EventType.reconstitute(44L, "Emp", "emp", "Desc", 60, 120, true, 303L);

      when(estateGateway.fetchAllEstates()).thenReturn(List.of(estate));
      when(eventTypeRepository.findAll()).thenReturn(List.of(hidden));
      when(eventTypeRepository.save(hidden)).thenReturn(hidden);

      // When
      service.execute();

      // Then
      verify(calComGateway).update(hidden, false);
      verify(eventTypeRepository).save(hidden);
    }

    @Test
    @DisplayName("Deve desativar EventType quando empreendimento não estiver ativo")
    void shouldDeactivateEventTypeWhenEstateIsNotActive() {
      // Given
      EstateData activeEstate = new EstateData(404L, "Ativo", "Desc", true);
      EventType activeExisting = EventType.reconstitute(78L, "Ativo", "ativo", "Desc", 60, 120, false, 404L);
      EventType shouldDeactivate = EventType.reconstitute(77L, "Outro", "outro", "Desc", 60, 120, false, 999L);

      when(estateGateway.fetchAllEstates()).thenReturn(List.of(activeEstate));
      when(eventTypeRepository.findAll()).thenReturn(List.of(activeExisting, shouldDeactivate));
      when(eventTypeRepository.save(shouldDeactivate)).thenReturn(shouldDeactivate);

      // When
      service.execute();

      // Then
      verify(calComGateway).update(shouldDeactivate, true);
      verify(eventTypeRepository).save(shouldDeactivate);
    }

    @Test
    @DisplayName("Deve continuar sincronização mesmo com erro em um empreendimento")
    void shouldContinueSyncWhenOneEstateFails() {
      // Given
      EstateData first = new EstateData(1L, "Primeiro", "Desc 1", true);
      EstateData second = new EstateData(2L, "Segundo", "Desc 2", true);
      EventType createdSecond = EventType.reconstitute(9002L, "Segundo", "segundo", "Desc 2", 60, 120, false, 2L);

      when(estateGateway.fetchAllEstates()).thenReturn(List.of(first, second));
      when(eventTypeRepository.findAll()).thenReturn(List.of());
      when(calComGateway.create(any(EventType.class), eq(false)))
        .thenThrow(new RuntimeException("falha primeiro"))
        .thenReturn(createdSecond);
      when(eventTypeRepository.save(createdSecond)).thenReturn(createdSecond);

      // When
      service.execute();

      // Then
      verify(calComGateway, times(2)).create(any(EventType.class), eq(false));
      verify(eventTypeRepository).save(createdSecond);
    }

    @Test
    @DisplayName("Não deve sincronizar empreendimento inativo")
    void shouldSkipInactiveEstate() {
      // Given
      EstateData inactive = new EstateData(333L, "Inativo", "Desc", false);

      when(estateGateway.fetchAllEstates()).thenReturn(List.of(inactive));
      when(eventTypeRepository.findAll()).thenReturn(List.of());

      // When
      service.execute();

      // Then
      verify(calComGateway, never()).create(any(EventType.class), eq(false));
      verify(calComGateway, never()).update(any(EventType.class), eq(true));
      verify(calComGateway, never()).update(any(EventType.class), eq(false));
    }
  }
}
