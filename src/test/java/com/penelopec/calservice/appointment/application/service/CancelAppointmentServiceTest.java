package com.penelopec.calservice.appointment.application.service;

import com.penelopec.calservice.appointment.application.command.CancelAppointmentCommand;
import com.penelopec.calservice.appointment.application.output.AppointmentOutput;
import com.penelopec.calservice.appointment.domain.entity.Appointment;
import com.penelopec.calservice.appointment.domain.error.AppointmentError;
import com.penelopec.calservice.shared.error.core.ApplicationException;
import com.penelopec.calservice.shared.error.core.DomainException;
import com.penelopec.calservice.appointment.domain.gateway.CalComBookingGateway;
import com.penelopec.calservice.appointment.domain.repository.AppointmentRepository;
import com.penelopec.calservice.appointment.domain.valueobject.Status;
import com.penelopec.calservice.shared.validation.CommandValidator;
import com.penelopec.calservice.shared.validation.ValidationResult;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CancelAppointmentServiceTest {

  @Mock private CalComBookingGateway bookingGateway;
  @Mock private AppointmentRepository repository;
  @Mock private CommandValidator<CancelAppointmentCommand> validator;
  @InjectMocks private CancelAppointmentService service;

  @BeforeEach
  void setUp() {
    lenient().when(validator.validate(any())).thenReturn(new ValidationResult());
  }

  @Nested
  @DisplayName("execute")
  class Execute {

    @Test
    @DisplayName("Deve cancelar no gateway, persistir localmente e armazenar motivo")
    void shouldCancelRemotelyAndPersistWithReason_whenAppointmentExists() {
      Appointment appointment = createAppointment(1L, "booking-123", Status.PENDING);
      CancelAppointmentCommand command = new CancelAppointmentCommand(1L, "Cliente desistiu");
      when(repository.findById(1L)).thenReturn(Optional.of(appointment));
      when(repository.save(appointment)).thenReturn(appointment);

      AppointmentOutput output = service.execute(command);

      verify(bookingGateway).cancelBooking("booking-123", "Cliente desistiu");
      verify(repository).save(appointment);
      assertThat(output.status()).isEqualTo("CANCELLED");
      assertThat(output.reason()).isEqualTo("Cliente desistiu");
    }

    @Test
    @DisplayName("Deve lancar excecao quando agendamento nao existe")
    void shouldThrowException_whenAppointmentDoesNotExist() {
      CancelAppointmentCommand command = new CancelAppointmentCommand(99L, "Cliente desistiu");
      when(repository.findById(99L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.execute(command))
        .isInstanceOf(DomainException.class)
        .satisfies(ex -> assertThat(((DomainException) ex).error()).isEqualTo(AppointmentError.NOT_FOUND));
    }

    @Test
    @DisplayName("Deve lancar excecao quando bookingUid esta ausente")
    void shouldThrowException_whenBookingUidIsMissing() {
      Appointment appointment = createAppointment(1L, null, Status.PENDING);
      CancelAppointmentCommand command = new CancelAppointmentCommand(1L, "Cliente desistiu");
      when(repository.findById(1L)).thenReturn(Optional.of(appointment));

      assertThatThrownBy(() -> service.execute(command))
        .isInstanceOf(ApplicationException.class)
        .satisfies(ex -> assertThat(((ApplicationException) ex).error()).isEqualTo(AppointmentError.MISSING_BOOKING_UID));
      verify(bookingGateway, never()).cancelBooking(any(), any());
      verify(repository, never()).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao cancelar agendamento com status terminal")
    void shouldThrowException_whenAppointmentHasTerminalStatus() {
      Appointment appointment = createAppointment(1L, "booking-123", Status.CONCLUDED);
      CancelAppointmentCommand command = new CancelAppointmentCommand(1L, "Motivo");
      when(repository.findById(1L)).thenReturn(Optional.of(appointment));

      assertThatThrownBy(() -> service.execute(command))
        .isInstanceOf(DomainException.class)
        .satisfies(ex -> assertThat(((DomainException) ex).error()).isEqualTo(AppointmentError.INVALID_STATUS_TRANSITION));
      verify(bookingGateway, never()).cancelBooking(any(), any());
      verify(repository, never()).save(any(Appointment.class));
    }
  }

  private Appointment createAppointment(Long id, String bookingUid, Status status) {
    return Appointment.reconstitute(
      id, bookingUid, 11L, 22L, 33L,
      status,
      LocalDateTime.parse("2026-03-22T10:00:00"),
      LocalDateTime.parse("2026-03-22T11:00:00"),
      "Cliente Teste", "cliente@teste.com", "Notas", null,
      LocalDateTime.parse("2026-03-20T09:00:00"),
      LocalDateTime.parse("2026-03-20T09:00:00")
    );
  }
}
