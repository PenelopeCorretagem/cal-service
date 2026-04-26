package com.penelopec.calservice.appointment.application.validator;

import com.penelopec.calservice.appointment.application.command.RescheduleAppointmentCommand;
import com.penelopec.calservice.appointment.domain.error.AppointmentError;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RescheduleAppointmentCommandValidatorTest {

  private final RescheduleAppointmentCommandValidator validator = new RescheduleAppointmentCommandValidator();

  @Test
  void shouldReturnNoErrors_whenCommandIsValid() {
    // Given
    var command = new RescheduleAppointmentCommand(
      100L,
      "2026-04-19T10:00:00",
      "Conflito de agenda"
    );

    // When
    var result = validator.validate(command);

    // Then
    assertThat(result.hasErrors()).isFalse();
    assertThat(result.getErrors()).isEmpty();
  }

  @Test
  void shouldReturnRequiredErrors_whenMandatoryFieldsAreMissing() {
    // Given
    var command = new RescheduleAppointmentCommand(
      null,
      " ",
      "Conflito de agenda"
    );

    // When
    var result = validator.validate(command);

    // Then
    assertThat(result.hasErrors()).isTrue();
    assertThat(result.getErrors()).hasSize(2);
    assertThat(result.getErrors()).anySatisfy(error -> {
      assertThat(error.field()).isEqualTo("appointmentId");
      assertThat(error.code()).isEqualTo(AppointmentError.APPOINTMENT_ID_REQUIRED.code());
    });
    assertThat(result.getErrors()).anySatisfy(error -> {
      assertThat(error.field()).isEqualTo("startDateTime");
      assertThat(error.code()).isEqualTo(AppointmentError.START_DATETIME_REQUIRED.code());
    });
  }

  @Test
  void shouldReturnDatetimeInvalidErrors_whenDatetimeFormatIsInvalid() {
    // Given
    var command = new RescheduleAppointmentCommand(
      100L,
      "invalid-start",
      "Conflito de agenda"
    );

    // When
    var result = validator.validate(command);

    // Then
    assertThat(result.hasErrors()).isTrue();
    assertThat(result.getErrors()).hasSize(1);
    assertThat(result.getErrors()).anySatisfy(error -> {
      assertThat(error.field()).isEqualTo("startDateTime");
      assertThat(error.code()).isEqualTo(AppointmentError.START_DATETIME_INVALID.code());
    });
  }
}