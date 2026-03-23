package com.penelopec.calservice.application.service;

import com.penelopec.calservice.eventtype.application.output.EventTypeOutput;
import com.penelopec.calservice.eventtype.application.service.GetEventTypeService;
import com.penelopec.calservice.eventtype.domain.entity.EventType;
import com.penelopec.calservice.eventtype.domain.exception.EventTypeNotFoundException;
import com.penelopec.calservice.eventtype.domain.gateway.CalComEventTypeGateway;
import com.penelopec.calservice.eventtype.domain.repository.EventTypeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetEventTypeServiceTest {

  @Mock
  private CalComEventTypeGateway calComGateway;

  @Mock
  private EventTypeRepository eventTypeRepository;

  @InjectMocks
  private GetEventTypeService service;

  @Nested
  @DisplayName("execute")
  class Execute {

    @Test
    @DisplayName("Deve lançar exceção quando não existir no Cal.com")
    void shouldThrowWhenNotFoundOnCalCom() {
      // Given
      Long eventTypeId = 9L;
      when(calComGateway.findById(eventTypeId)).thenReturn(Optional.empty());

      // When / Then
      assertThatThrownBy(() -> service.execute(eventTypeId))
        .isInstanceOf(EventTypeNotFoundException.class)
        .hasMessageContaining("EventType não encontrado no Cal.com");
    }

    @Test
    @DisplayName("Deve retornar dados do Cal.com quando estateId já estiver preenchido")
    void shouldReturnCalComDataWhenEstateIdAlreadyPresent() {
      // Given
      Long eventTypeId = 21L;
      EventType eventType = EventType.reconstitute(
        eventTypeId,
        "Visita Cobertura",
        "visita-cobertura",
        "Descricao",
        60,
        120,
        false,
        300L
      );

      when(calComGateway.findById(eventTypeId)).thenReturn(Optional.of(eventType));

      // When
      EventTypeOutput output = service.execute(eventTypeId);

      // Then
      assertThat(output.id()).isEqualTo(eventTypeId);
      assertThat(output.estateId()).isEqualTo(300L);
      verifyNoInteractions(eventTypeRepository);
    }

    @Test
    @DisplayName("Deve complementar estateId com dado local quando vier nulo do Cal.com")
    void shouldCompleteEstateIdFromLocalDataWhenMissingOnCalCom() {
      // Given
      Long eventTypeId = 30L;
      EventType fromCalCom = EventType.reconstitute(
        eventTypeId,
        "Visita Studio",
        "visita-studio",
        "Descricao",
        45,
        90,
        false,
        null
      );
      EventType fromLocal = EventType.reconstitute(
        eventTypeId,
        "Visita Studio",
        "visita-studio",
        "Descricao",
        45,
        90,
        false,
        501L
      );

      when(calComGateway.findById(eventTypeId)).thenReturn(Optional.of(fromCalCom));
      when(eventTypeRepository.findById(eventTypeId)).thenReturn(Optional.of(fromLocal));

      // When
      EventTypeOutput output = service.execute(eventTypeId);

      // Then
      assertThat(output.estateId()).isEqualTo(501L);
      verify(eventTypeRepository).findById(eventTypeId);
    }

    @Test
    @DisplayName("Deve manter estateId nulo quando não houver dado local")
    void shouldKeepEstateIdNullWhenLocalDataDoesNotExist() {
      // Given
      Long eventTypeId = 31L;
      EventType fromCalCom = EventType.reconstitute(
        eventTypeId,
        "Visita Loja",
        "visita-loja",
        "Descricao",
        45,
        90,
        false,
        null
      );

      when(calComGateway.findById(eventTypeId)).thenReturn(Optional.of(fromCalCom));
      when(eventTypeRepository.findById(eventTypeId)).thenReturn(Optional.empty());

      // When
      EventTypeOutput output = service.execute(eventTypeId);

      // Then
      assertThat(output.estateId()).isNull();
      verify(eventTypeRepository).findById(eventTypeId);
    }
  }
}
