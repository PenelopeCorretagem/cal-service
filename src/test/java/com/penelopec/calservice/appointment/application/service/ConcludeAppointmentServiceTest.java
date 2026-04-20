package com.penelopec.calservice.appointment.application.service;

import com.penelopec.calservice.appointment.application.command.ConcludeAppointmentCommand;
import com.penelopec.calservice.appointment.application.output.AppointmentOutput;
import com.penelopec.calservice.appointment.domain.entity.Appointment;
import com.penelopec.calservice.appointment.domain.error.AppointmentError;
import com.penelopec.calservice.shared.error.core.DomainException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConcludeAppointmentServiceTest {

  @Mock private AppointmentRepository repository;
  @InjectMocks private ConcludeAppointmentService service;

  @Nested
  @DisplayName("execute")
  class Execute {

    @Test
    @DisplayName("Deve concluir agendamento quando registro existe")
    void shouldConcludeAppointment_whenAppointmentExists() {
      Appointment appointment = createAppointment(Status.CONFIRMED);
      ConcludeAppointmentCommand command = new ConcludeAppointmentCommand(1L);
      when(repository.findById(1L)).thenReturn(Optional.of(appointment));
      when(repository.save(appointment)).thenReturn(appointment);

      AppointmentOutput output = service.execute(command);

      verify(repository).save(appointment);
      assertThat(output.status()).isEqualTo("CONCLUDED");
    }

    @Test
    @DisplayName("Deve lancar excecao quando agendamento nao existe")
    void shouldThrowException_whenAppointmentDoesNotExist() {
      ConcludeAppointmentCommand command = new ConcludeAppointmentCommand(99L);
      when(repository.findById(99L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.execute(command))
        .isInstanceOf(DomainException.class)
        .satisfies(ex -> assertThat(((DomainException) ex).error()).isEqualTo(AppointmentError.NOT_FOUND));
    }

    @Test
    @DisplayName("Deve lancar excecao ao concluir agendamento CANCELLED")
    void shouldThrowException_whenAppointmentIsCancelled() {
      Appointment appointment = createAppointment(Status.CANCELLED);
      ConcludeAppointmentCommand command = new ConcludeAppointmentCommand(1L);
      when(repository.findById(1L)).thenReturn(Optional.of(appointment));

      assertThatThrownBy(() -> service.execute(command))
        .isInstanceOf(DomainException.class)
        .satisfies(ex -> assertThat(((DomainException) ex).error()).isEqualTo(AppointmentError.INVALID_STATUS_TRANSITION));
    }
  }

  private Appointment createAppointment(Status status) {
    return Appointment.reconstitute(
      1L, "booking-123", 11L, 22L, 33L,
      status,
      LocalDateTime.parse("2026-03-22T10:00:00"),
      LocalDateTime.parse("2026-03-22T11:00:00"),
      "Cliente Teste", "cliente@teste.com", "Notas", null,
      LocalDateTime.parse("2026-03-20T09:00:00"),
      LocalDateTime.parse("2026-03-20T09:00:00")
    );
  }
}
