package com.penelopec.calservice.appointment.application.validator;

import com.penelopec.calservice.appointment.application.command.AppointmentCommand;
import com.penelopec.calservice.appointment.domain.error.AppointmentValidationCode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AppointmentCommandValidatorTest {

  private final AppointmentCommandValidator validator = new AppointmentCommandValidator();

  @Test
  void shouldReturnNoErrors_whenCommandIsValid() {
    // Given
    var command = new AppointmentCommand(
      10L,
      20L,
      30L,
      "2026-04-19T10:00:00",
      "2026-04-19T11:00:00",
      "Cliente Teste",
      "cliente@teste.com",
      "Primeira visita"
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
    var command = new AppointmentCommand(
      null,
      20L,
      30L,
      " ",
      null,
      " ",
      " ",
      "Notas"
    );

    // When
    var result = validator.validate(command);

    // Then
    assertThat(result.hasErrors()).isTrue();
    assertThat(result.getErrors()).hasSize(5);
    assertThat(result.getErrors()).anySatisfy(error -> {
      assertThat(error.field()).isEqualTo("eventTypeId");
      assertThat(error.code()).isEqualTo(AppointmentValidationCode.EVENT_TYPE_ID_REQUIRED.code());
    });
    assertThat(result.getErrors()).anySatisfy(error -> {
      assertThat(error.field()).isEqualTo("attendeeName");
      assertThat(error.code()).isEqualTo(AppointmentValidationCode.ATTENDEE_NAME_REQUIRED.code());
    });
    assertThat(result.getErrors()).anySatisfy(error -> {
      assertThat(error.field()).isEqualTo("attendeeEmail");
      assertThat(error.code()).isEqualTo(AppointmentValidationCode.ATTENDEE_EMAIL_REQUIRED.code());
    });
    assertThat(result.getErrors()).anySatisfy(error -> {
      assertThat(error.field()).isEqualTo("startDateTime");
      assertThat(error.code()).isEqualTo(AppointmentValidationCode.START_DATETIME_REQUIRED.code());
    });
    assertThat(result.getErrors()).anySatisfy(error -> {
      assertThat(error.field()).isEqualTo("endDateTime");
      assertThat(error.code()).isEqualTo(AppointmentValidationCode.END_DATETIME_REQUIRED.code());
    });
  }

  @Test
  void shouldReturnDatetimeInvalidErrors_whenDatetimeFormatIsInvalid() {
    // Given
    var command = new AppointmentCommand(
      10L,
      20L,
      30L,
      "invalid-start",
      "invalid-end",
      "Cliente Teste",
      "cliente@teste.com",
      "Primeira visita"
    );

    // When
    var result = validator.validate(command);

    // Then
    assertThat(result.hasErrors()).isTrue();
    assertThat(result.getErrors()).hasSize(2);
    assertThat(result.getErrors()).anySatisfy(error -> {
      assertThat(error.field()).isEqualTo("startDateTime");
      assertThat(error.code()).isEqualTo(AppointmentValidationCode.START_DATETIME_INVALID.code());
    });
    assertThat(result.getErrors()).anySatisfy(error -> {
      assertThat(error.field()).isEqualTo("endDateTime");
      assertThat(error.code()).isEqualTo(AppointmentValidationCode.END_DATETIME_INVALID.code());
    });
  }
}