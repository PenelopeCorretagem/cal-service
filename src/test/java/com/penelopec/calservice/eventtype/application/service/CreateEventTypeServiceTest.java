package com.penelopec.calservice.eventtype.application.service;

import com.penelopec.calservice.eventtype.application.command.CreateEventTypeCommand;
import com.penelopec.calservice.eventtype.application.output.EventTypeOutput;
import com.penelopec.calservice.eventtype.application.service.CreateEventTypeService;
import com.penelopec.calservice.eventtype.domain.entity.EventType;
import com.penelopec.calservice.eventtype.domain.gateway.CalComEventTypeGateway;
import com.penelopec.calservice.eventtype.domain.repository.EventTypeRepository;
import com.penelopec.calservice.shared.validation.CommandValidator;
import com.penelopec.calservice.shared.validation.ValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateEventTypeServiceTest {

  @Mock
  private CalComEventTypeGateway calComGateway;

  @Mock
  private EventTypeRepository eventTypeRepository;

  @Mock
  private CommandValidator<CreateEventTypeCommand> validator;

  @InjectMocks
  private CreateEventTypeService service;

  @BeforeEach
  void setUp() {
    lenient().when(validator.validate(any())).thenReturn(new ValidationResult());
  }

  @Nested
  @DisplayName("execute")
  class Execute {

    @Test
    @DisplayName("Deve criar no Cal.com e persistir localmente")
    void shouldCreateOnCalComAndPersistLocally() {
      // Given
      CreateEventTypeCommand command = new CreateEventTypeCommand(
        "Visita Apartamento",
        "Descricao",
        45,
        90,
        true,
        77L
      );

      EventType createdOnCalCom = EventType.reconstitute(
        1001L,
        "Visita Apartamento",
        "visita-apartamento",
        "Descricao",
        45,
        90,
        true,
        77L
      );

      when(calComGateway.create(any(EventType.class), eq(true))).thenReturn(createdOnCalCom);
      when(eventTypeRepository.save(createdOnCalCom)).thenReturn(createdOnCalCom);

      // When
      EventTypeOutput output = service.execute(command);

      // Then
      ArgumentCaptor<EventType> captor = ArgumentCaptor.forClass(EventType.class);
      verify(calComGateway).create(captor.capture(), eq(true));
      verify(eventTypeRepository).save(createdOnCalCom);

      EventType sentToGateway = captor.getValue();
      assertThat(sentToGateway.getId()).isNull();
      assertThat(sentToGateway.getTitle()).isEqualTo("Visita Apartamento");
      assertThat(sentToGateway.getSlugValue()).isEqualTo("visita-apartamento");
      assertThat(sentToGateway.getEstateId()).isEqualTo(77L);

      assertThat(output.id()).isEqualTo(1001L);
      assertThat(output.title()).isEqualTo("Visita Apartamento");
      assertThat(output.slug()).isEqualTo("visita-apartamento");
      assertThat(output.hidden()).isTrue();
    }

    @Test
    @DisplayName("Deve aplicar hidden padr\u00e3o como false quando n\u00e3o informado")
    void shouldUseDefaultHiddenFalseWhenNotProvided() {
      // Given
      CreateEventTypeCommand command = new CreateEventTypeCommand(
        "Visita Casa",
        "Descricao",
        60,
        120,
        null,
        15L
      );

      EventType createdOnCalCom = EventType.reconstitute(
        33L,
        "Visita Casa",
        "visita-casa",
        "Descricao",
        60,
        120,
        false,
        15L
      );

      when(calComGateway.create(any(EventType.class), eq(false))).thenReturn(createdOnCalCom);
      when(eventTypeRepository.save(createdOnCalCom)).thenReturn(createdOnCalCom);

      // When
      EventTypeOutput output = service.execute(command);

      // Then
      verify(calComGateway).create(any(EventType.class), eq(false));
      assertThat(output.hidden()).isFalse();
    }
  }
}
