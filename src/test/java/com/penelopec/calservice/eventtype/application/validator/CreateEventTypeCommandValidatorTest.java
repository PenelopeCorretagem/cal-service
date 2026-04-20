package com.penelopec.calservice.eventtype.application.validator;

import com.penelopec.calservice.eventtype.application.command.CreateEventTypeCommand;
import com.penelopec.calservice.eventtype.domain.error.EventTypeValidationCode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CreateEventTypeCommandValidatorTest {

  private final CreateEventTypeCommandValidator validator = new CreateEventTypeCommandValidator();

  @Test
  void shouldReturnNoErrors_whenCommandIsValid() {
    // Given
    var command = new CreateEventTypeCommand(
      "Visita presencial",
      "Descricao",
      60,
      120,
      false,
      42L
    );

    // When
    var result = validator.validate(command);

    // Then
    assertThat(result.hasErrors()).isFalse();
    assertThat(result.getErrors()).isEmpty();
  }

  @Test
  void shouldReturnTitleRequiredError_whenTitleIsBlank() {
    // Given
    var command = new CreateEventTypeCommand(
      "   ",
      "Descricao",
      60,
      120,
      false,
      42L
    );

    // When
    var result = validator.validate(command);

    // Then
    assertThat(result.hasErrors()).isTrue();
    assertThat(result.getErrors()).anySatisfy(error -> {
      assertThat(error.field()).isEqualTo("title");
      assertThat(error.code()).isEqualTo(EventTypeValidationCode.TITLE_REQUIRED.code());
    });
  }

  @Test
  void shouldReturnEstateIdRequiredError_whenEstateIdIsNull() {
    // Given
    var command = new CreateEventTypeCommand(
      "Visita presencial",
      "Descricao",
      60,
      120,
      false,
      null
    );

    // When
    var result = validator.validate(command);

    // Then
    assertThat(result.hasErrors()).isTrue();
    assertThat(result.getErrors()).anySatisfy(error -> {
      assertThat(error.field()).isEqualTo("estateId");
      assertThat(error.code()).isEqualTo(EventTypeValidationCode.ESTATE_ID_REQUIRED.code());
    });
  }
}