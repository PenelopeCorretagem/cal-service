package com.penelopec.calservice.eventtype.application.validator;

import com.penelopec.calservice.eventtype.application.command.UpdateEventTypeCommand;
import com.penelopec.calservice.eventtype.domain.error.EventTypeError;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateEventTypeCommandValidatorTest {

  private final UpdateEventTypeCommandValidator validator = new UpdateEventTypeCommandValidator();

  @Test
  void shouldReturnNoErrors_whenCommandIsValidAndTitleIsNull() {
    // Given
    var command = new UpdateEventTypeCommand(
      99L,
      null,
      "Descricao atualizada",
      45,
      90
    );

    // When
    var result = validator.validate(command);

    // Then
    assertThat(result.hasErrors()).isFalse();
    assertThat(result.getErrors()).isEmpty();
  }

  @Test
  void shouldReturnEventTypeIdRequiredError_whenEventTypeIdIsNull() {
    // Given
    var command = new UpdateEventTypeCommand(
      null,
      "Novo titulo",
      "Descricao",
      45,
      90
    );

    // When
    var result = validator.validate(command);

    // Then
    assertThat(result.hasErrors()).isTrue();
    assertThat(result.getErrors()).anySatisfy(error -> {
      assertThat(error.field()).isEqualTo("eventTypeId");
      assertThat(error.code()).isEqualTo(EventTypeError.VALIDATION_EVENT_TYPE_ID_REQUIRED.code());
    });
  }

  @Test
  void shouldReturnTitleRequiredError_whenTitleIsBlank() {
    // Given
    var command = new UpdateEventTypeCommand(
      99L,
      "   ",
      "Descricao",
      45,
      90
    );

    // When
    var result = validator.validate(command);

    // Then
    assertThat(result.hasErrors()).isTrue();
    assertThat(result.getErrors()).anySatisfy(error -> {
      assertThat(error.field()).isEqualTo("title");
      assertThat(error.code()).isEqualTo(EventTypeError.VALIDATION_TITLE_REQUIRED.code());
    });
  }
}