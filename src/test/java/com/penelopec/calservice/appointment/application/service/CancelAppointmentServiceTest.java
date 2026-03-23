package com.penelopec.calservice.appointment.application.service;

import com.penelopec.calservice.appointment.application.command.CancelAppointmentCommand;
import com.penelopec.calservice.appointment.application.output.AppointmentOutput;
import com.penelopec.calservice.appointment.domain.entity.Appointment;
import com.penelopec.calservice.appointment.domain.exception.AppointmentNotFoundException;
import com.penelopec.calservice.appointment.domain.gateway.CalComBookingGateway;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CancelAppointmentServiceTest {

  @Mock
  private CalComBookingGateway bookingGateway;

  @Mock
  private AppointmentRepository repository;

  @InjectMocks
  private CancelAppointmentService service;

  @Nested
  @DisplayName("execute")
  class Execute {

    @Test
    @DisplayName("Deve cancelar no gateway e persistir localmente")
    void shouldCancelRemotelyAndPersist_whenAppointmentExists() {
      // Given
      Appointment appointment = createAppointment(1L, "booking-123", Status.PENDING);
      CancelAppointmentCommand command = new CancelAppointmentCommand(1L, "Cliente desistiu");

      when(repository.findById(1L)).thenReturn(Optional.of(appointment));
      when(repository.save(appointment)).thenReturn(appointment);

      // When
      AppointmentOutput output = service.execute(command);

      // Then
      verify(bookingGateway).cancelBooking("booking-123", "Cliente desistiu");
      verify(repository).save(appointment);
      assertThat(output.status()).isEqualTo("CANCELLED");
    }

    @Test
    @DisplayName("Deve lancar excecao quando agendamento nao existe")
    void shouldThrowException_whenAppointmentDoesNotExist() {
      // Given
      CancelAppointmentCommand command = new CancelAppointmentCommand(99L, "Cliente desistiu");
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
    @DisplayName("Deve lancar excecao quando bookingUid esta ausente")
    void shouldThrowException_whenBookingUidIsMissing() {
      // Given
      Appointment appointment = createAppointment(1L, null, Status.PENDING);
      CancelAppointmentCommand command = new CancelAppointmentCommand(1L, "Cliente desistiu");
      when(repository.findById(1L)).thenReturn(Optional.of(appointment));

      // When
      Throwable thrown = org.assertj.core.api.Assertions.catchThrowable(() -> service.execute(command));

      // Then
      assertThat(thrown)
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("bookingUid");

      verify(bookingGateway, never()).cancelBooking(any(), any());
      verify(repository, never()).save(any(Appointment.class));
    }
  }

  private Appointment createAppointment(Long id, String bookingUid, Status status) {
    return Appointment.reconstitute(
      id,
      bookingUid,
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
