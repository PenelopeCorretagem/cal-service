package com.penelopec.calservice.application.service;

import com.penelopec.calservice.eventtype.application.command.HandleEstateChangedCommand;
import com.penelopec.calservice.eventtype.application.service.HandleEstateChangedService;
import com.penelopec.calservice.eventtype.domain.entity.EventType;
import com.penelopec.calservice.eventtype.domain.gateway.CalComEventTypeGateway;
import com.penelopec.calservice.eventtype.domain.repository.EventTypeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HandleEstateChangedServiceTest {

  @Mock
  private EventTypeRepository repository;

  @Mock
  private CalComEventTypeGateway calComGateway;

  @InjectMocks
  private HandleEstateChangedService service;

  @Nested
  @DisplayName("execute")
  class Execute {

    @Test
    @DisplayName("Deve ocultar EventType quando status INACTIVE e estava visível")
    void shouldHideEventType_whenInactiveAndCurrentlyVisible() {
      // Given
      EventType eventType = EventType.reconstitute(55L, "Visita", "visita", "Desc", 60, 120, false, 10L);
      when(repository.findByEstateId(10L)).thenReturn(Optional.of(eventType));
      when(repository.save(any())).thenReturn(eventType);

      // When
      service.execute(new HandleEstateChangedCommand(10L, true));

      // Then
      ArgumentCaptor<EventType> captor = ArgumentCaptor.forClass(EventType.class);
      verify(calComGateway).update(captor.capture(), eq(true));
      verify(repository).save(captor.getValue());
      assertThat(captor.getValue().isHidden()).isTrue();
    }

    @Test
    @DisplayName("Deve revelar EventType quando status ACTIVE e estava oculto")
    void shouldShowEventType_whenActiveAndCurrentlyHidden() {
      // Given
      EventType eventType = EventType.reconstitute(55L, "Visita", "visita", "Desc", 60, 120, true, 10L);
      when(repository.findByEstateId(10L)).thenReturn(Optional.of(eventType));
      when(repository.save(any())).thenReturn(eventType);

      // When
      service.execute(new HandleEstateChangedCommand(10L, false));

      // Then
      ArgumentCaptor<EventType> captor = ArgumentCaptor.forClass(EventType.class);
      verify(calComGateway).update(captor.capture(), eq(false));
      verify(repository).save(captor.getValue());
      assertThat(captor.getValue().isHidden()).isFalse();
    }

    @Test
    @DisplayName("Deve ser no-op quando EventType já está oculto e comando pede ocultar")
    void shouldBeNoOp_whenEventTypeAlreadyHidden() {
      // Given
      EventType eventType = EventType.reconstitute(55L, "Visita", "visita", "Desc", 60, 120, true, 10L);
      when(repository.findByEstateId(10L)).thenReturn(Optional.of(eventType));

      // When
      service.execute(new HandleEstateChangedCommand(10L, true));

      // Then
      verifyNoInteractions(calComGateway);
      verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve ser no-op quando EventType já está visível e comando pede revelar")
    void shouldBeNoOp_whenEventTypeAlreadyVisible() {
      // Given
      EventType eventType = EventType.reconstitute(55L, "Visita", "visita", "Desc", 60, 120, false, 10L);
      when(repository.findByEstateId(10L)).thenReturn(Optional.of(eventType));

      // When
      service.execute(new HandleEstateChangedCommand(10L, false));

      // Then
      verifyNoInteractions(calComGateway);
      verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve ignorar mensagem quando nenhum EventType encontrado para o estateId")
    void shouldIgnoreMessage_whenNoEventTypeFoundForEstateId() {
      // Given
      when(repository.findByEstateId(99L)).thenReturn(Optional.empty());

      // When
      service.execute(new HandleEstateChangedCommand(99L, true));

      // Then
      verifyNoInteractions(calComGateway);
      verify(repository, never()).save(any());
    }
  }
}
