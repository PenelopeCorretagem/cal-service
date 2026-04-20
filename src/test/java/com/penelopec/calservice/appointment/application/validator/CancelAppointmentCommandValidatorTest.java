package com.penelopec.calservice.appointment.application.validator;

import com.penelopec.calservice.appointment.application.command.CancelAppointmentCommand;
import com.penelopec.calservice.appointment.domain.error.AppointmentValidationCode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CancelAppointmentCommandValidatorTest {

  private final CancelAppointmentCommandValidator validator = new CancelAppointmentCommandValidator();

  @Test
  void shouldReturnNoErrors_whenCommandIsValid() {
    // Given
    var command = new CancelAppointmentCommand(100L, "Cliente desistiu");

    // When
    var result = validator.validate(command);

    // Then
    assertThat(result.hasErrors()).isFalse();
    assertThat(result.getErrors()).isEmpty();
  }

  @Test
  void shouldReturnAppointmentIdRequiredError_whenAppointmentIdIsNull() {
    // Given
    var command = new CancelAppointmentCommand(null, "Cliente desistiu");

    // When
    var result = validator.validate(command);

    // Then
    assertThat(result.hasErrors()).isTrue();
    assertThat(result.getErrors()).singleElement().satisfies(error -> {
      assertThat(error.field()).isEqualTo("appointmentId");
      assertThat(error.code()).isEqualTo(AppointmentValidationCode.APPOINTMENT_ID_REQUIRED.code());
    });
  }
}