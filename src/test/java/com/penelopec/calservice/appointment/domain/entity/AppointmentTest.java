package com.penelopec.calservice.appointment.domain.entity;

import com.penelopec.calservice.appointment.domain.valueobject.Status;
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
      // When
      Appointment appointment = Appointment.createNew(1L, 2L, 3L, 4L, START, END);

      // Then
      assertThat(appointment.getId()).isNull();
      assertThat(appointment.getBookingUid()).isNull();
      assertThat(appointment.getEventTypeId()).isEqualTo(1L);
      assertThat(appointment.getClientId()).isEqualTo(2L);
      assertThat(appointment.getEstateAgentId()).isEqualTo(3L);
      assertThat(appointment.getEstateId()).isEqualTo(4L);
      assertThat(appointment.getStatus()).isEqualTo(Status.PENDING);
      assertThat(appointment.getDurationMinutes()).isEqualTo(60);
      assertThat(appointment.getStartDateTime()).isEqualTo(START);
      assertThat(appointment.getEndDateTime()).isEqualTo(END);
      assertThat(appointment.getCreatedAt()).isNotNull();
      assertThat(appointment.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Deve lancar IllegalArgumentException quando startDateTime e nulo")
    void shouldThrowException_whenStartDateTimeIsNull() {
      // When / Then
      assertThatThrownBy(() -> Appointment.createNew(1L, 2L, 3L, 4L, null, END))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("obrigatórias");
    }

    @Test
    @DisplayName("Deve lancar IllegalArgumentException quando endDateTime e nulo")
    void shouldThrowException_whenEndDateTimeIsNull() {
      // When / Then
      assertThatThrownBy(() -> Appointment.createNew(1L, 2L, 3L, 4L, START, null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("obrigatórias");
    }

    @Test
    @DisplayName("Deve lancar IllegalArgumentException quando endDateTime nao e posterior a startDateTime")
    void shouldThrowException_whenEndDateTimeIsNotAfterStartDateTime() {
      // Given
      LocalDateTime sameTime = START;

      // When / Then
      assertThatThrownBy(() -> Appointment.createNew(1L, 2L, 3L, 4L, START, sameTime))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("posterior");
    }
  }

  @Nested
  @DisplayName("reconstitute")
  class Reconstitute {

    @Test
    @DisplayName("Deve reconstituir agendamento com todos os campos")
    void shouldReconstituteAppointmentWithAllFields_whenAllFieldsAreProvided() {
      // Given
      LocalDateTime now = LocalDateTime.now();

      // When
      Appointment appointment = Appointment.reconstitute(
        10L, "booking-uid", 1L, 2L, 3L, 4L,
        60, Status.CONFIRMED, START, END, now, now
      );

      // Then
      assertThat(appointment.getId()).isEqualTo(10L);
      assertThat(appointment.getBookingUid()).isEqualTo("booking-uid");
      assertThat(appointment.getEventTypeId()).isEqualTo(1L);
      assertThat(appointment.getClientId()).isEqualTo(2L);
      assertThat(appointment.getEstateAgentId()).isEqualTo(3L);
      assertThat(appointment.getEstateId()).isEqualTo(4L);
      assertThat(appointment.getDurationMinutes()).isEqualTo(60);
      assertThat(appointment.getStatus()).isEqualTo(Status.CONFIRMED);
      assertThat(appointment.getStartDateTime()).isEqualTo(START);
      assertThat(appointment.getEndDateTime()).isEqualTo(END);
    }
  }

  @Nested
  @DisplayName("assignBookingUid")
  class AssignBookingUid {

    @Test
    @DisplayName("Deve atribuir bookingUid e atualizar updatedAt")
    void shouldAssignBookingUidAndUpdateTimestamp_whenCalled() {
      // Given
      Appointment appointment = Appointment.createNew(1L, 2L, 3L, 4L, START, END);
      LocalDateTime beforeAssign = LocalDateTime.now().minusSeconds(1);

      // When
      appointment.assignBookingUid("uid-abc-123");

      // Then
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
      // Given
      Appointment appointment = Appointment.createNew(1L, 2L, 3L, 4L, START, END);

      // When
      appointment.confirm();

      // Then
      assertThat(appointment.getStatus()).isEqualTo(Status.CONFIRMED);
    }

    @Test
    @DisplayName("Deve lancar IllegalStateException ao confirmar agendamento CANCELLED")
    void shouldThrowException_whenStatusIsCancelled() {
      // Given
      Appointment appointment = reconstituted(Status.CANCELLED);

      // When / Then
      assertThatThrownBy(appointment::confirm)
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("CANCELLED");
    }

    @Test
    @DisplayName("Deve lancar IllegalStateException ao confirmar agendamento CONCLUDED")
    void shouldThrowException_whenStatusIsConcluded() {
      // Given
      Appointment appointment = reconstituted(Status.CONCLUDED);

      // When / Then
      assertThatThrownBy(appointment::confirm)
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("CONCLUDED");
    }
  }

  @Nested
  @DisplayName("cancel")
  class Cancel {

    @Test
    @DisplayName("Deve alterar status para CANCELLED quando status e PENDING")
    void shouldChangeStatusToCancelled_whenStatusIsPending() {
      // Given
      Appointment appointment = Appointment.createNew(1L, 2L, 3L, 4L, START, END);

      // When
      appointment.cancel();

      // Then
      assertThat(appointment.getStatus()).isEqualTo(Status.CANCELLED);
    }

    @Test
    @DisplayName("Deve lancar IllegalStateException ao cancelar agendamento ja CANCELLED")
    void shouldThrowException_whenStatusIsAlreadyCancelled() {
      // Given
      Appointment appointment = reconstituted(Status.CANCELLED);

      // When / Then
      assertThatThrownBy(appointment::cancel)
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("CANCELLED");
    }

    @Test
    @DisplayName("Deve lancar IllegalStateException ao cancelar agendamento CONCLUDED")
    void shouldThrowException_whenStatusIsConcluded() {
      // Given
      Appointment appointment = reconstituted(Status.CONCLUDED);

      // When / Then
      assertThatThrownBy(appointment::cancel)
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("CONCLUDED");
    }
  }

  @Nested
  @DisplayName("conclude")
  class Conclude {

    @Test
    @DisplayName("Deve alterar status para CONCLUDED quando status e CONFIRMED")
    void shouldChangeStatusToConcluded_whenStatusIsConfirmed() {
      // Given
      Appointment appointment = reconstituted(Status.CONFIRMED);

      // When
      appointment.conclude();

      // Then
      assertThat(appointment.getStatus()).isEqualTo(Status.CONCLUDED);
    }

    @Test
    @DisplayName("Deve lancar IllegalStateException ao concluir agendamento CANCELLED")
    void shouldThrowException_whenStatusIsCancelled() {
      // Given
      Appointment appointment = reconstituted(Status.CANCELLED);

      // When / Then
      assertThatThrownBy(appointment::conclude)
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("cancelado");
    }
  }

  @Nested
  @DisplayName("reschedule")
  class Reschedule {

    @Test
    @DisplayName("Deve atualizar datas e duracao quando status e PENDING")
    void shouldUpdateDatesAndDuration_whenStatusIsPending() {
      // Given
      Appointment appointment = Appointment.createNew(1L, 2L, 3L, 4L, START, END);
      LocalDateTime newStart = LocalDateTime.parse("2026-03-23T14:00:00");
      LocalDateTime newEnd = LocalDateTime.parse("2026-03-23T15:30:00");

      // When
      appointment.reschedule(newStart, newEnd);

      // Then
      assertThat(appointment.getStartDateTime()).isEqualTo(newStart);
      assertThat(appointment.getEndDateTime()).isEqualTo(newEnd);
      assertThat(appointment.getDurationMinutes()).isEqualTo(90);
    }

    @Test
    @DisplayName("Deve lancar IllegalStateException ao reagendar agendamento CANCELLED")
    void shouldThrowException_whenStatusIsCancelled() {
      // Given
      Appointment appointment = reconstituted(Status.CANCELLED);
      LocalDateTime newStart = LocalDateTime.parse("2026-03-23T14:00:00");
      LocalDateTime newEnd = LocalDateTime.parse("2026-03-23T15:30:00");

      // When / Then
      assertThatThrownBy(() -> appointment.reschedule(newStart, newEnd))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("CANCELLED");
    }

    @Test
    @DisplayName("Deve lancar IllegalArgumentException quando newEnd nao e posterior a newStart")
    void shouldThrowException_whenNewEndIsNotAfterNewStart() {
      // Given
      Appointment appointment = Appointment.createNew(1L, 2L, 3L, 4L, START, END);

      // When / Then
      assertThatThrownBy(() -> appointment.reschedule(START, START))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("posterior");
    }
  }

  private Appointment reconstituted(Status status) {
    return Appointment.reconstitute(
      1L, "booking-uid", 1L, 2L, 3L, 4L,
      60, status, START, END,
      LocalDateTime.parse("2026-03-20T09:00:00"),
      LocalDateTime.parse("2026-03-20T09:00:00")
    );
  }
}
