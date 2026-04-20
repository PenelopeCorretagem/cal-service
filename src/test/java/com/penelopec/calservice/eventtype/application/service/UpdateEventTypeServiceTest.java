package com.penelopec.calservice.eventtype.application.service;

import com.penelopec.calservice.eventtype.application.command.UpdateEventTypeCommand;
import com.penelopec.calservice.eventtype.application.output.EventTypeOutput;
import com.penelopec.calservice.eventtype.application.service.ChangeEventTypeService;
import com.penelopec.calservice.eventtype.domain.entity.EventType;
import com.penelopec.calservice.eventtype.domain.error.EventTypeError;
import com.penelopec.calservice.eventtype.domain.gateway.CalComEventTypeGateway;
import com.penelopec.calservice.eventtype.domain.repository.EventTypeRepository;
import com.penelopec.calservice.shared.error.core.DomainException;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateEventTypeServiceTest {

  @Mock
  private CalComEventTypeGateway calComGateway;

  @Mock
  private EventTypeRepository eventTypeRepository;

  @Mock
  private CommandValidator<UpdateEventTypeCommand> validator;

  @InjectMocks
  private ChangeEventTypeService service;

  @BeforeEach
  void setUp() {
    lenient().when(validator.validate(any())).thenReturn(new ValidationResult());
  }

  @Nested
  @DisplayName("execute")
  class Execute {

    @Test
    @DisplayName("Deve lan\u00e7ar exce\u00e7\u00e3o quando event type n\u00e3o existir")
    void shouldThrowWhenEventTypeDoesNotExist() {
      // Given
      UpdateEventTypeCommand command = new UpdateEventTypeCommand(99L, "Novo", "Desc", 30, 60);
      when(eventTypeRepository.findById(99L)).thenReturn(Optional.empty());

      // When / Then
      assertThatThrownBy(() -> service.execute(command))
        .isInstanceOf(DomainException.class)
        .satisfies(ex -> assertThat(((DomainException) ex).error()).isEqualTo(EventTypeError.NOT_FOUND));

      verifyNoInteractions(calComGateway);
    }

    @Test
    @DisplayName("Deve atualizar campos informados e persistir")
    void shouldUpdateProvidedFieldsAndPersist() {
      // Given
      Long eventTypeId = 5L;
      EventType existing = EventType.reconstitute(
        eventTypeId,
        "Titulo Antigo",
        "titulo-antigo",
        "Descricao Antiga",
        60,
        120,
        false,
        42L
      );
      UpdateEventTypeCommand command = new UpdateEventTypeCommand(
        eventTypeId,
        "Titulo Novo",
        "Descricao Nova",
        30,
        45
      );

      when(eventTypeRepository.findById(eventTypeId)).thenReturn(Optional.of(existing));
      when(eventTypeRepository.save(existing)).thenReturn(existing);

      // When
      EventTypeOutput output = service.execute(command);

      // Then
      ArgumentCaptor<EventType> captor = ArgumentCaptor.forClass(EventType.class);
      verify(calComGateway).update(captor.capture(), eq(false));
      verify(eventTypeRepository).save(existing);

      EventType updated = captor.getValue();
      assertThat(updated.getTitle()).isEqualTo("Titulo Novo");
      assertThat(updated.getSlugValue()).isEqualTo("titulo-novo");
      assertThat(updated.getDescription()).isEqualTo("Descricao Nova");
      assertThat(updated.getLengthInMinutes()).isEqualTo(30);
      assertThat(updated.getMinimumBookingNotice()).isEqualTo(45);

      assertThat(output.title()).isEqualTo("Titulo Novo");
      assertThat(output.slug()).isEqualTo("titulo-novo");
    }

    @Test
    @DisplayName("Deve manter campos quando command tiver valores nulos")
    void shouldKeepFieldsUnchangedWhenCommandFieldsAreNull() {
      // Given
      Long eventTypeId = 8L;
      EventType existing = EventType.reconstitute(
        eventTypeId,
        "Titulo Base",
        "titulo-base",
        "Descricao Base",
        55,
        100,
        false,
        9L
      );
      UpdateEventTypeCommand command = new UpdateEventTypeCommand(eventTypeId, null, "Nova Desc", null, null);

      when(eventTypeRepository.findById(eventTypeId)).thenReturn(Optional.of(existing));
      when(eventTypeRepository.save(existing)).thenReturn(existing);

      // When
      EventTypeOutput output = service.execute(command);

      // Then
      verify(calComGateway).update(existing, false);
      assertThat(output.title()).isEqualTo("Titulo Base");
      assertThat(output.description()).isEqualTo("Nova Desc");
      assertThat(output.lengthInMinutes()).isEqualTo(55);
      assertThat(output.minimumBookingNotice()).isEqualTo(100);
    }

    @Test
    @DisplayName("Deve falhar quando dura\u00e7\u00e3o for inv\u00e1lida")
    void shouldFailWhenLengthIsInvalid() {
      // Given
      Long eventTypeId = 70L;
      EventType existing = EventType.reconstitute(
        eventTypeId,
        "Titulo",
        "titulo",
        "Descricao",
        60,
        120,
        false,
        3L
      );
      UpdateEventTypeCommand command = new UpdateEventTypeCommand(eventTypeId, null, null, 0, null);

      when(eventTypeRepository.findById(eventTypeId)).thenReturn(Optional.of(existing));

      // When / Then
      assertThatThrownBy(() -> service.execute(command))
        .isInstanceOf(DomainException.class)
        .satisfies(ex -> assertThat(((DomainException) ex).error()).isEqualTo(EventTypeError.INVALID_DURATION));

      verifyNoInteractions(calComGateway);
    }
  }
}
