package com.penelopec.calservice.appointment.application.service;

import com.penelopec.calservice.appointment.application.command.RescheduleAppointmentCommand;
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
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RescheduleAppointmentServiceTest {

  @Mock
  private CalComBookingGateway bookingGateway;

  @Mock
  private AppointmentRepository repository;

  @InjectMocks
  private RescheduleAppointmentService service;

  @Nested
  @DisplayName("execute")
  class Execute {

    @Test
    @DisplayName("Deve reagendar no gateway e persistir localmente")
    void shouldRescheduleRemotelyAndPersist_whenAppointmentExists() {
      // Given
      Appointment appointment = createAppointment(1L, "booking-123", Status.PENDING);
      RescheduleAppointmentCommand command = new RescheduleAppointmentCommand(
        1L,
        "2026-03-23T16:00:00",
        "2026-03-23T17:30:00",
        "Conflito de agenda"
      );

      when(repository.findById(1L)).thenReturn(Optional.of(appointment));
      when(repository.save(appointment)).thenReturn(appointment);

      // When
      AppointmentOutput output = service.execute(command);

      // Then
      verify(bookingGateway).rescheduleBooking(
        "booking-123",
        LocalDateTime.parse("2026-03-23T16:00:00").atOffset(ZoneOffset.UTC),
        LocalDateTime.parse("2026-03-23T17:30:00").atOffset(ZoneOffset.UTC),
        "Conflito de agenda"
      );
      verify(repository).save(appointment);
      assertThat(output.startDateTime()).isEqualTo(LocalDateTime.parse("2026-03-23T16:00:00"));
      assertThat(output.endDateTime()).isEqualTo(LocalDateTime.parse("2026-03-23T17:30:00"));
      assertThat(output.durationMinutes()).isEqualTo(90);
    }

    @Test
    @DisplayName("Deve lancar excecao quando agendamento nao existe")
    void shouldThrowException_whenAppointmentDoesNotExist() {
      // Given
      RescheduleAppointmentCommand command = new RescheduleAppointmentCommand(
        99L,
        "2026-03-23T16:00:00",
        "2026-03-23T17:30:00",
        "Conflito de agenda"
      );
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
      RescheduleAppointmentCommand command = new RescheduleAppointmentCommand(
        1L,
        "2026-03-23T16:00:00",
        "2026-03-23T17:30:00",
        "Conflito de agenda"
      );
      when(repository.findById(1L)).thenReturn(Optional.of(appointment));

      // When
      Throwable thrown = org.assertj.core.api.Assertions.catchThrowable(() -> service.execute(command));

      // Then
      assertThat(thrown)
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("bookingUid");

      verify(bookingGateway, never()).rescheduleBooking(any(), any(), any(), any());
      verify(repository, never()).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Deve lancar excecao quando startDateTime e invalido")
    void shouldThrowException_whenStartDateTimeIsInvalid() {
      // Given
      Appointment appointment = createAppointment(1L, "booking-123", Status.PENDING);
      RescheduleAppointmentCommand command = new RescheduleAppointmentCommand(
        1L,
        "data-invalida",
        "2026-03-23T17:30:00",
        "Conflito de agenda"
      );
      when(repository.findById(1L)).thenReturn(Optional.of(appointment));

      // When
      Throwable thrown = org.assertj.core.api.Assertions.catchThrowable(() -> service.execute(command));

      // Then
      assertThat(thrown)
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("startDateTime");

      verify(bookingGateway, never()).rescheduleBooking(any(), any(), any(), any());
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
