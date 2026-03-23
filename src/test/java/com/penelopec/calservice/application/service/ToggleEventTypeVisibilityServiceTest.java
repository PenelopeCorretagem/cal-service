package com.penelopec.calservice.application.service;

import com.penelopec.calservice.eventtype.application.output.EventTypeOutput;
import com.penelopec.calservice.eventtype.application.service.ToggleEventTypeVisibilityService;
import com.penelopec.calservice.eventtype.domain.entity.EventType;
import com.penelopec.calservice.eventtype.domain.exception.EventTypeNotFoundException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ToggleEventTypeVisibilityServiceTest {

  @Mock
  private CalComEventTypeGateway calComGateway;

  @Mock
  private EventTypeRepository eventTypeRepository;

  @InjectMocks
  private ToggleEventTypeVisibilityService service;

  @Nested
  @DisplayName("execute")
  class Execute {

    @Test
    @DisplayName("Deve lançar exceção quando event type não existir")
    void shouldThrowWhenEventTypeDoesNotExist() {
      // Given
      Long eventTypeId = 999L;
      when(eventTypeRepository.findById(eventTypeId)).thenReturn(Optional.empty());

      // When / Then
      assertThatThrownBy(() -> service.execute(eventTypeId))
        .isInstanceOf(EventTypeNotFoundException.class)
        .hasMessageContaining("EventType não encontrado");

      verifyNoInteractions(calComGateway);
    }

    @Test
    @DisplayName("Deve alternar visibilidade, atualizar remoto e persistir")
    void shouldToggleVisibilityAndPersist() {
      // Given
      Long eventTypeId = 12L;
      EventType existing = EventType.reconstitute(
        eventTypeId,
        "Visita Duplex",
        "visita-duplex",
        "Descricao",
        60,
        120,
        false,
        80L
      );

      when(eventTypeRepository.findById(eventTypeId)).thenReturn(Optional.of(existing));
      when(eventTypeRepository.save(existing)).thenReturn(existing);

      // When
      EventTypeOutput output = service.execute(eventTypeId);

      // Then
      ArgumentCaptor<EventType> captor = ArgumentCaptor.forClass(EventType.class);
      verify(calComGateway).update(captor.capture(), eq(true));
      verify(eventTypeRepository).save(existing);

      EventType sentToGateway = captor.getValue();
      assertThat(sentToGateway.isHidden()).isTrue();
      assertThat(output.hidden()).isTrue();
      assertThat(output.id()).isEqualTo(eventTypeId);
    }
  }
}
