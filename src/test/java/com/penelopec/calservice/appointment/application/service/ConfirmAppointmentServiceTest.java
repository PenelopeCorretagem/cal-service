package com.penelopec.calservice.appointment.application.service;

import com.penelopec.calservice.appointment.application.command.ConfirmAppointmentCommand;
import com.penelopec.calservice.appointment.application.output.AppointmentOutput;
import com.penelopec.calservice.appointment.domain.entity.Appointment;
import com.penelopec.calservice.appointment.domain.exception.AppointmentNotFoundException;
import com.penelopec.calservice.appointment.domain.repository.AppointmentRepository;
import com.penelopec.calservice.appointment.domain.valueobject.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfirmAppointmentServiceTest {

  @Mock
  private AppointmentRepository repository;

  @InjectMocks
  private ConfirmAppointmentService service;

  @Nested
  @DisplayName("execute")
  class Execute {

    @Test
    @DisplayName("Deve confirmar agendamento quando registro existe")
    void shouldConfirmAppointment_whenAppointmentExists() {
      // Given
      Appointment appointment = createAppointment(Status.PENDING);
      ConfirmAppointmentCommand command = new ConfirmAppointmentCommand(1L);

      when(repository.findById(1L)).thenReturn(Optional.of(appointment));
      when(repository.save(appointment)).thenReturn(appointment);

      // When
      AppointmentOutput output = service.execute(command);

      // Then
      verify(repository).save(appointment);
      assertThat(output.status()).isEqualTo("CONFIRMED");
    }

    @Test
    @DisplayName("Deve lancar excecao quando agendamento nao existe")
    void shouldThrowException_whenAppointmentDoesNotExist() {
      // Given
      ConfirmAppointmentCommand command = new ConfirmAppointmentCommand(99L);
      when(repository.findById(99L)).thenReturn(Optional.empty());

      // When
      Throwable thrown = org.assertj.core.api.Assertions.catchThrowable(() -> service.execute(command));

      // Then
      assertThat(thrown)
        .isInstanceOf(AppointmentNotFoundException.class)
        .hasMessageContaining("Agendamento")
        .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Deve lancar excecao ao confirmar agendamento com status terminal")
    void shouldThrowException_whenAppointmentHasTerminalStatus() {
      // Given
      Appointment appointment = createAppointment(Status.CANCELLED);
      ConfirmAppointmentCommand command = new ConfirmAppointmentCommand(1L);
      when(repository.findById(1L)).thenReturn(Optional.of(appointment));

      // When
      Throwable thrown = org.assertj.core.api.Assertions.catchThrowable(() -> service.execute(command));

      // Then
      assertThat(thrown)
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("CANCELLED");
    }
  }

  private Appointment createAppointment(Status status) {
    return Appointment.reconstitute(
      1L,
      "booking-123",
      11L,
      22L,
      33L,
      44L,
      60,
      status,
      LocalDateTime.parse("2026-03-22T10:00:00"),
      LocalDateTime.parse("2026-03-22T11:00:00"),
      LocalDateTime.parse("2026-03-20T09:00:00"),
      LocalDateTime.parse("2026-03-20T09:00:00")
    );
  }
}
