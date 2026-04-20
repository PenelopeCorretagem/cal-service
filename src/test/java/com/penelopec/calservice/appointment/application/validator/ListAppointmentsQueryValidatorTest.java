package com.penelopec.calservice.appointment.application.validator;

import com.penelopec.calservice.appointment.application.query.ListAppointmentsQuery;
import com.penelopec.calservice.appointment.domain.error.AppointmentValidationCode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ListAppointmentsQueryValidatorTest {

  private final ListAppointmentsQueryValidator validator = new ListAppointmentsQueryValidator();

  @Test
  void shouldReturnNoErrors_whenQueryIsValid() {
    // Given
    var query = new ListAppointmentsQuery(
      1L,
      2L,
      3L,
      "pending",
      "2026-04-19T10:00:00",
      "2026-04-19T11:00:00",
      0,
      20
    );

    // When
    var result = validator.validate(query);

    // Then
    assertThat(result.hasErrors()).isFalse();
    assertThat(result.getErrors()).isEmpty();
  }

  @Test
  void shouldReturnPageInvalidError_whenPageIsNegative() {
    // Given
    var query = new ListAppointmentsQuery(null, null, null, null, null, null, -1, 20);

    // When
    var result = validator.validate(query);

    // Then
    assertThat(result.hasErrors()).isTrue();
    assertThat(result.getErrors()).singleElement().satisfies(error -> {
      assertThat(error.field()).isEqualTo("page");
      assertThat(error.code()).isEqualTo(AppointmentValidationCode.LIST_PAGE_INVALID.code());
    });
  }

  @Test
  void shouldReturnSizeInvalidError_whenSizeIsZero() {
    // Given
    var query = new ListAppointmentsQuery(null, null, null, null, null, null, 0, 0);

    // When
    var result = validator.validate(query);

    // Then
    assertThat(result.hasErrors()).isTrue();
    assertThat(result.getErrors()).singleElement().satisfies(error -> {
      assertThat(error.field()).isEqualTo("size");
      assertThat(error.code()).isEqualTo(AppointmentValidationCode.LIST_SIZE_INVALID.code());
    });
  }

  @Test
  void shouldReturnStatusInvalidError_whenStatusIsUnknown() {
    // Given
    var query = new ListAppointmentsQuery(null, null, null, "unknown", null, null, 0, 20);

    // When
    var result = validator.validate(query);

    // Then
    assertThat(result.hasErrors()).isTrue();
    assertThat(result.getErrors()).singleElement().satisfies(error -> {
      assertThat(error.field()).isEqualTo("status");
      assertThat(error.code()).isEqualTo(AppointmentValidationCode.LIST_STATUS_INVALID.code());
    });
  }

  @Test
  void shouldReturnDatetimeInvalidErrors_whenDatetimeFormatIsInvalid() {
    // Given
    var query = new ListAppointmentsQuery(
      null,
      null,
      null,
      "pending",
      "invalid-start",
      "invalid-end",
      0,
      20
    );

    // When
    var result = validator.validate(query);

    // Then
    assertThat(result.hasErrors()).isTrue();
    assertThat(result.getErrors()).hasSize(2);
    assertThat(result.getErrors()).anySatisfy(error -> {
      assertThat(error.field()).isEqualTo("startDateTime");
      assertThat(error.code()).isEqualTo(AppointmentValidationCode.LIST_START_DATETIME_INVALID.code());
    });
    assertThat(result.getErrors()).anySatisfy(error -> {
      assertThat(error.field()).isEqualTo("endDateTime");
      assertThat(error.code()).isEqualTo(AppointmentValidationCode.LIST_END_DATETIME_INVALID.code());
    });
  }
}