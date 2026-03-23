package com.penelopec.calservice.appointment.application.service;

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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteAppointmentServiceTest {

  @Mock
  private CalComBookingGateway bookingGateway;

  @Mock
  private AppointmentRepository repository;

  @InjectMocks
  private DeleteAppointmentService service;

  @Nested
  @DisplayName("execute")
  class Execute {

    @Test
    @DisplayName("Deve cancelar remotamente e excluir localmente quando bookingUid existe")
    void shouldCancelRemotelyAndDeleteLocally_whenBookingUidExists() {
      // Given
      Appointment appointment = createAppointment(1L, "booking-123");
      when(repository.findById(1L)).thenReturn(Optional.of(appointment));

      // When
      service.execute(1L);

      // Then
      verify(bookingGateway).cancelBooking("booking-123", "Removido pelo sistema");
      verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve excluir apenas localmente quando bookingUid e nulo")
    void shouldDeleteLocallyOnly_whenBookingUidIsNull() {
      // Given
      Appointment appointment = createAppointment(1L, null);
      when(repository.findById(1L)).thenReturn(Optional.of(appointment));

      // When
      service.execute(1L);

      // Then
      verify(bookingGateway, never()).cancelBooking(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
      verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lancar excecao quando agendamento nao existe")
    void shouldThrowException_whenAppointmentDoesNotExist() {
      // Given
      when(repository.findById(99L)).thenReturn(Optional.empty());

      // When
      Throwable thrown = org.assertj.core.api.Assertions.catchThrowable(() -> service.execute(99L));

      // Then
      assertThat(thrown)
        .isInstanceOf(AppointmentNotFoundException.class)
        .hasMessageContaining("Agendamento")
        .hasMessageContaining("99");
    }
  }

  private Appointment createAppointment(Long id, String bookingUid) {
    return Appointment.reconstitute(
      id,
      bookingUid,
      11L,
      22L,
      33L,
      44L,
      60,
      Status.PENDING,
      LocalDateTime.parse("2026-03-22T10:00:00"),
      LocalDateTime.parse("2026-03-22T11:00:00"),
      LocalDateTime.parse("2026-03-20T09:00:00"),
      LocalDateTime.parse("2026-03-20T09:00:00")
    );
  }
}
