package com.penelopec.calservice.application.service;

import com.penelopec.calservice.application.command.UpdateEventTypeCommand;
import com.penelopec.calservice.application.output.EventTypeOutput;
import com.penelopec.calservice.domain.entity.EventType;
import com.penelopec.calservice.domain.exception.EventTypeNotFoundException;
import com.penelopec.calservice.domain.gateway.CalComEventTypeGateway;
import com.penelopec.calservice.domain.repository.EventTypeRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateEventTypeServiceTest {

    @Mock
    private CalComEventTypeGateway calComGateway;

    @Mock
    private EventTypeRepository eventTypeRepository;

    @InjectMocks
    private UpdateEventTypeService service;

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("Deve lançar exceção quando event type não existir")
        void shouldThrowWhenEventTypeDoesNotExist() {
            // Given
            UpdateEventTypeCommand command = new UpdateEventTypeCommand(99L, "Novo", "Desc", 30, 60);
            when(eventTypeRepository.findById(99L)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(EventTypeNotFoundException.class)
                    .hasMessageContaining("EventType não encontrado");

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
        @DisplayName("Deve falhar quando duração for inválida")
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
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Duração deve ser maior que zero");

            verifyNoInteractions(calComGateway);
        }
    }
}
