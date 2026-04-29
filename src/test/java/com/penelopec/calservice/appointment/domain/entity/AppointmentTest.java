package com.penelopec.calservice.appointment.domain.entity;

import com.penelopec.calservice.appointment.domain.error.AppointmentError;
import com.penelopec.calservice.appointment.domain.valueobject.Status;
import com.penelopec.calservice.shared.error.core.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AppointmentTest {

  private static final LocalDateTime START = LocalDateTime.parse("2026-03-22T10:00:00");
  private static final LocalDateTime END = LocalDateTime.parse("2026-03-22T11:00:00");

  @Nested
  @DisplayName("createNew")
  class CreateNew {

    @Test
    @DisplayName("Deve criar agendamento com status PENDING e duracao calculada")
    void shouldCreateAppointmentWithPendingStatusAndCalculatedDuration_whenDatesAreValid() {
      Appointment appointment = Appointment.createNew(1L, 2L, 3L, START, END,
        "Cliente Teste", "cliente@teste.com", "Primeira visita");

      assertThat(appointment.getId()).isNull();
      assertThat(appointment.getBookingUid()).isNull();
      assertThat(appointment.getEventTypeId()).isEqualTo(1L);
      assertThat(appointment.getClientId()).isEqualTo(2L);
      assertThat(appointment.getEstateAgentId()).isEqualTo(3L);
      assertThat(appointment.getStatus()).isEqualTo(Status.PENDING);
      assertThat(appointment.getDurationMinutes()).isEqualTo(60);
      assertThat(appointment.getStartDateTime()).isEqualTo(START);
      assertThat(appointment.getEndDateTime()).isEqualTo(END);
      assertThat(appointment.getAttendeeName()).isEqualTo("Cliente Teste");
      assertThat(appointment.getAttendeeEmail()).isEqualTo("cliente@teste.com");
      assertThat(appointment.getNotes()).isEqualTo("Primeira visita");
      assertThat(appointment.getReason()).isNull();
      assertThat(appointment.getCreatedAt()).isNotNull();
      assertThat(appointment.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Deve lancar DomainException quando startDateTime e nulo")
    void shouldThrowException_whenStartDateTimeIsNull() {
      assertThatThrownBy(() -> Appointment.createNew(1L, 2L, 3L, null, END,
        "Cliente", "c@t.com", null))
        .isInstanceOf(DomainException.class)
        .satisfies(ex -> assertThat(((DomainException) ex).error()).isEqualTo(AppointmentError.MISSING_DATETIMES));
    }

    @Test
    @DisplayName("Deve lancar DomainException quando endDateTime e nulo")
    void shouldThrowException_whenEndDateTimeIsNull() {
      assertThatThrownBy(() -> Appointment.createNew(1L, 2L, 3L, START, null,
        "Cliente", "c@t.com", null))
        .isInstanceOf(DomainException.class)
        .satisfies(ex -> assertThat(((DomainException) ex).error()).isEqualTo(AppointmentError.MISSING_DATETIMES));
    }

    @Test
    @DisplayName("Deve lancar DomainException quando endDateTime nao e posterior a startDateTime")
    void shouldThrowException_whenEndDateTimeIsNotAfterStartDateTime() {
      LocalDateTime sameTime = START;
      assertThatThrownBy(() -> Appointment.createNew(1L, 2L, 3L, START, sameTime,
        "Cliente", "c@t.com", null))
        .isInstanceOf(DomainException.class)
        .satisfies(ex -> assertThat(((DomainException) ex).error()).isEqualTo(AppointmentError.INVALID_DATES));
    }
  }

  @Nested
  @DisplayName("reconstitute")
  class Reconstitute {

    @Test
    @DisplayName("Deve reconstituir agendamento com todos os campos")
    void shouldReconstituteAppointmentWithAllFields_whenAllFieldsAreProvided() {
      LocalDateTime now = LocalDateTime.now();
      Appointment appointment = Appointment.reconstitute(
        10L, "booking-uid", 1L, 2L, 3L,
        Status.CONFIRMED, START, END,
        "Cliente Teste", "cliente@teste.com", "Notas", "Motivo",
        now, now
      );

      assertThat(appointment.getId()).isEqualTo(10L);
      assertThat(appointment.getBookingUid()).isEqualTo("booking-uid");
      assertThat(appointment.getEventTypeId()).isEqualTo(1L);
      assertThat(appointment.getClientId()).isEqualTo(2L);
      assertThat(appointment.getEstateAgentId()).isEqualTo(3L);
      assertThat(appointment.getDurationMinutes()).isEqualTo(60);
      assertThat(appointment.getStatus()).isEqualTo(Status.CONFIRMED);
      assertThat(appointment.getStartDateTime()).isEqualTo(START);
      assertThat(appointment.getEndDateTime()).isEqualTo(END);
      assertThat(appointment.getAttendeeName()).isEqualTo("Cliente Teste");
      assertThat(appointment.getAttendeeEmail()).isEqualTo("cliente@teste.com");
      assertThat(appointment.getNotes()).isEqualTo("Notas");
      assertThat(appointment.getReason()).isEqualTo("Motivo");
    }
  }

  @Nested
  @DisplayName("assignBookingUid")
  class AssignBookingUid {

    @Test
    @DisplayName("Deve atribuir bookingUid e atualizar updatedAt")
    void shouldAssignBookingUidAndUpdateTimestamp_whenCalled() {
      Appointment appointment = Appointment.createNew(1L, 2L, 3L, START, END,
        "Cliente", "c@t.com", null);
      LocalDateTime beforeAssign = LocalDateTime.now().minusSeconds(1);
      appointment.assignBookingUid("uid-abc-123");

      assertThat(appointment.getBookingUid()).isEqualTo("uid-abc-123");
      assertThat(appointment.getUpdatedAt()).isAfterOrEqualTo(beforeAssign);
    }
  }

  @Nested
  @DisplayName("confirm")
  class Confirm {

    @Test
    @DisplayName("Deve alterar status para CONFIRMED quando status e PENDING")
    void shouldChangeStatusToConfirmed_whenStatusIsPending() {
      Appointment appointment = Appointment.createNew(1L, 2L, 3L, START, END,
        "Cliente", "c@t.com", null);
      appointment.confirm();
      assertThat(appointment.getStatus()).isEqualTo(Status.CONFIRMED);
    }

    @Test
    @DisplayName("Deve lancar DomainException ao confirmar agendamento CANCELLED")
    void shouldThrowException_whenStatusIsCancelled() {
      Appointment appointment = reconstituted(Status.CANCELLED);
      assertThatThrownBy(appointment::confirm)
        .isInstanceOf(DomainException.class)
        .satisfies(ex -> assertThat(((DomainException) ex).error()).isEqualTo(AppointmentError.INVALID_STATUS_TRANSITION));
    }

    @Test
    @DisplayName("Deve lancar DomainException ao confirmar agendamento CONCLUDED")
    void shouldThrowException_whenStatusIsConcluded() {
      Appointment appointment = reconstituted(Status.CONCLUDED);
      assertThatThrownBy(appointment::confirm)
        .isInstanceOf(DomainException.class)
        .satisfies(ex -> assertThat(((DomainException) ex).error()).isEqualTo(AppointmentError.INVALID_STATUS_TRANSITION));
    }
  }

  @Nested
  @DisplayName("cancel")
  class Cancel {

    @Test
    @DisplayName("Deve alterar status para CANCELLED e armazenar motivo")
    void shouldChangeStatusToCancelledAndStoreReason_whenStatusIsPending() {
      Appointment appointment = Appointment.createNew(1L, 2L, 3L, START, END,
        "Cliente", "c@t.com", null);
      appointment.cancel("Cliente desistiu");
      assertThat(appointment.getStatus()).isEqualTo(Status.CANCELLED);
      assertThat(appointment.getReason()).isEqualTo("Cliente desistiu");
    }

    @Test
    @DisplayName("Deve lancar DomainException ao cancelar agendamento ja CANCELLED")
    void shouldThrowException_whenStatusIsAlreadyCancelled() {
      Appointment appointment = reconstituted(Status.CANCELLED);
      assertThatThrownBy(() -> appointment.cancel("Motivo"))
        .isInstanceOf(DomainException.class)
        .satisfies(ex -> assertThat(((DomainException) ex).error()).isEqualTo(AppointmentError.INVALID_STATUS_TRANSITION));
    }

    @Test
    @DisplayName("Deve lancar DomainException ao cancelar agendamento CONCLUDED")
    void shouldThrowException_whenStatusIsConcluded() {
      Appointment appointment = reconstituted(Status.CONCLUDED);
      assertThatThrownBy(() -> appointment.cancel("Motivo"))
        .isInstanceOf(DomainException.class)
        .satisfies(ex -> assertThat(((DomainException) ex).error()).isEqualTo(AppointmentError.INVALID_STATUS_TRANSITION));
    }
  }

  @Nested
  @DisplayName("conclude")
  class Conclude {

    @Test
    @DisplayName("Deve alterar status para CONCLUDED quando status e CONFIRMED")
    void shouldChangeStatusToConcluded_whenStatusIsConfirmed() {
      Appointment appointment = reconstituted(Status.CONFIRMED);
      appointment.conclude();
      assertThat(appointment.getStatus()).isEqualTo(Status.CONCLUDED);
    }

    @Test
    @DisplayName("Deve lancar DomainException ao concluir agendamento CANCELLED")
    void shouldThrowException_whenStatusIsCancelled() {
      Appointment appointment = reconstituted(Status.CANCELLED);
      assertThatThrownBy(appointment::conclude)
        .isInstanceOf(DomainException.class)
        .satisfies(ex -> assertThat(((DomainException) ex).error()).isEqualTo(AppointmentError.INVALID_STATUS_TRANSITION));
    }
  }

  @Nested
  @DisplayName("reschedule")
  class Reschedule {

    @Test
    @DisplayName("Deve atualizar datas e armazenar motivo quando status e PENDING")
    void shouldUpdateDatesAndStoreReason_whenStatusIsPending() {
      Appointment appointment = Appointment.createNew(1L, 2L, 3L, START, END,
        "Cliente", "c@t.com", null);
      LocalDateTime newStart = LocalDateTime.parse("2026-03-23T14:00:00");
      LocalDateTime newEnd = LocalDateTime.parse("2026-03-23T15:30:00");
      appointment.reschedule(newStart, newEnd, "Conflito de agenda");

      assertThat(appointment.getStartDateTime()).isEqualTo(newStart);
      assertThat(appointment.getEndDateTime()).isEqualTo(newEnd);
      assertThat(appointment.getDurationMinutes()).isEqualTo(90);
      assertThat(appointment.getReason()).isEqualTo("Conflito de agenda");
    }

    @Test
    @DisplayName("Deve lancar DomainException ao reagendar agendamento CANCELLED")
    void shouldThrowException_whenStatusIsCancelled() {
      Appointment appointment = reconstituted(Status.CANCELLED);
      LocalDateTime newStart = LocalDateTime.parse("2026-03-23T14:00:00");
      LocalDateTime newEnd = LocalDateTime.parse("2026-03-23T15:30:00");
      assertThatThrownBy(() -> appointment.reschedule(newStart, newEnd, "Motivo"))
        .isInstanceOf(DomainException.class)
        .satisfies(ex -> assertThat(((DomainException) ex).error()).isEqualTo(AppointmentError.INVALID_STATUS_TRANSITION));
    }

    @Test
    @DisplayName("Deve lancar DomainException quando newEnd nao e posterior a newStart")
    void shouldThrowException_whenNewEndIsNotAfterNewStart() {
      Appointment appointment = Appointment.createNew(1L, 2L, 3L, START, END,
        "Cliente", "c@t.com", null);
      assertThatThrownBy(() -> appointment.reschedule(START, START, "Motivo"))
        .isInstanceOf(DomainException.class)
        .satisfies(ex -> assertThat(((DomainException) ex).error()).isEqualTo(AppointmentError.INVALID_DATES));
    }
  }

  private Appointment reconstituted(Status status) {
    return Appointment.reconstitute(
      1L, "booking-uid", 1L, 2L, 3L,
      status, START, END,
      "Cliente Teste", "cliente@teste.com", "Notas", null,
      LocalDateTime.parse("2026-03-20T09:00:00"),
      LocalDateTime.parse("2026-03-20T09:00:00")
    );
  }
}
