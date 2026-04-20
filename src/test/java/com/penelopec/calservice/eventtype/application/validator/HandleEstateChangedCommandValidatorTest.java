package com.penelopec.calservice.eventtype.application.validator;

import com.penelopec.calservice.eventtype.application.command.HandleEstateChangedCommand;
import com.penelopec.calservice.eventtype.domain.error.EventTypeError;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HandleEstateChangedCommandValidatorTest {

  private final HandleEstateChangedCommandValidator validator = new HandleEstateChangedCommandValidator();

  @Test
  void shouldReturnNoErrors_whenCommandIsValid() {
    // Given
    var command = new HandleEstateChangedCommand(7L, true);

    // When
    var result = validator.validate(command);

    // Then
    assertThat(result.hasErrors()).isFalse();
    assertThat(result.getErrors()).isEmpty();
  }

  @Test
  void shouldReturnEstateChangedIdRequiredError_whenEstateIdIsNull() {
    // Given
    var command = new HandleEstateChangedCommand(null, true);

    // When
    var result = validator.validate(command);

    // Then
    assertThat(result.hasErrors()).isTrue();
    assertThat(result.getErrors()).singleElement().satisfies(error -> {
      assertThat(error.field()).isEqualTo("estateId");
      assertThat(error.code()).isEqualTo(EventTypeError.VALIDATION_ESTATE_CHANGED_ID_REQUIRED.code());
    });
  }
}