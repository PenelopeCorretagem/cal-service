package com.penelopec.calservice.appointment.application.service;

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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAppointmentServiceTest {

  @Mock private AppointmentRepository repository;
  @InjectMocks private GetAppointmentService service;

  @Nested
  @DisplayName("execute")
  class Execute {

    @Test
    @DisplayName("Deve retornar agendamento quando registro existe")
    void shouldReturnAppointment_whenAppointmentExists() {
      Appointment appointment = Appointment.reconstitute(
        1L, "booking-123", 11L, 22L, 33L,
        Status.PENDING,
        LocalDateTime.parse("2026-03-22T10:00:00"),
        LocalDateTime.parse("2026-03-22T11:00:00"),
        "Cliente Teste", "cliente@teste.com", "Notas", null,
        LocalDateTime.parse("2026-03-20T09:00:00"),
        LocalDateTime.parse("2026-03-20T09:00:00")
      );
      when(repository.findById(1L)).thenReturn(Optional.of(appointment));

      AppointmentOutput output = service.execute(1L);

      assertThat(output.id()).isEqualTo(1L);
      assertThat(output.bookingUid()).isEqualTo("booking-123");
      assertThat(output.status()).isEqualTo("PENDING");
      assertThat(output.attendeeName()).isEqualTo("Cliente Teste");
      assertThat(output.attendeeEmail()).isEqualTo("cliente@teste.com");
    }

    @Test
    @DisplayName("Deve lancar excecao quando agendamento nao existe")
    void shouldThrowException_whenAppointmentDoesNotExist() {
      when(repository.findById(99L)).thenReturn(Optional.empty());

      Throwable thrown = org.assertj.core.api.Assertions.catchThrowable(() -> service.execute(99L));

      assertThat(thrown)
        .isInstanceOf(DomainException.class)
        .satisfies(ex -> assertThat(((DomainException) ex).error()).isEqualTo(AppointmentError.NOT_FOUND));
    }
  }
}
