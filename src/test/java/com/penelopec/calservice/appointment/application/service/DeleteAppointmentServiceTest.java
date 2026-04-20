package com.penelopec.calservice.appointment.application.service;

import com.penelopec.calservice.appointment.domain.entity.Appointment;
import com.penelopec.calservice.appointment.domain.error.AppointmentError;
import com.penelopec.calservice.shared.error.core.DomainException;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteAppointmentServiceTest {

  @Mock private CalComBookingGateway bookingGateway;
  @Mock private AppointmentRepository repository;
  @InjectMocks private DeleteAppointmentService service;

  @Nested
  @DisplayName("execute")
  class Execute {

    @Test
    @DisplayName("Deve cancelar remotamente e excluir localmente quando bookingUid existe")
    void shouldCancelRemotelyAndDeleteLocally_whenBookingUidExists() {
      Appointment appointment = createAppointment(1L, "booking-123");
      when(repository.findById(1L)).thenReturn(Optional.of(appointment));

      service.execute(1L);

      verify(bookingGateway).cancelBooking("booking-123", "Removido pelo sistema");
      verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve excluir apenas localmente quando bookingUid e nulo")
    void shouldDeleteLocallyOnly_whenBookingUidIsNull() {
      Appointment appointment = createAppointment(1L, null);
      when(repository.findById(1L)).thenReturn(Optional.of(appointment));

      service.execute(1L);

      verify(bookingGateway, never()).cancelBooking(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
      verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lancar excecao quando agendamento nao existe")
    void shouldThrowException_whenAppointmentDoesNotExist() {
      when(repository.findById(99L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.execute(99L))
        .isInstanceOf(DomainException.class)
        .satisfies(ex -> assertThat(((DomainException) ex).error()).isEqualTo(AppointmentError.NOT_FOUND));
    }
  }

  private Appointment createAppointment(Long id, String bookingUid) {
    return Appointment.reconstitute(
      id, bookingUid, 11L, 22L, 33L,
      Status.PENDING,
      LocalDateTime.parse("2026-03-22T10:00:00"),
      LocalDateTime.parse("2026-03-22T11:00:00"),
      "Cliente Teste", "cliente@teste.com", "Notas", null,
      LocalDateTime.parse("2026-03-20T09:00:00"),
      LocalDateTime.parse("2026-03-20T09:00:00")
    );
  }
}
