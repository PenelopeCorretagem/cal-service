package com.penelopec.calservice.eventtype.application.service;

import com.penelopec.calservice.eventtype.application.service.DeleteEventTypeService;
import com.penelopec.calservice.eventtype.domain.gateway.CalComEventTypeGateway;
import com.penelopec.calservice.eventtype.domain.repository.EventTypeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeleteEventTypeServiceTest {

  @Mock
  private CalComEventTypeGateway calComGateway;

  @Mock
  private EventTypeRepository eventTypeRepository;

  @InjectMocks
  private DeleteEventTypeService service;

  @Nested
  @DisplayName("execute")
  class Execute {

    @Test
    @DisplayName("Deve lançar exceção quando ID é nulo")
    void shouldThrowException_whenEventTypeIdIsNull() {
      // When / Then
      assertThatThrownBy(() -> service.execute(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("ID do EventType é obrigatório");
    }

    @Test
    @DisplayName("Deve excluir no Cal.com e no repositório local")
    void shouldDeleteRemotelyAndLocally_whenIdIsValid() {
      // Given
      Long eventTypeId = 10L;

      // When
      service.execute(eventTypeId);

      // Then
      verify(calComGateway).delete(eventTypeId);
      verify(eventTypeRepository).deleteById(eventTypeId);
    }
  }
}
