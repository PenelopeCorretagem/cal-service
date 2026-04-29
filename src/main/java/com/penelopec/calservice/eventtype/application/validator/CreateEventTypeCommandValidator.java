package com.penelopec.calservice.eventtype.application.validator;

import com.penelopec.calservice.eventtype.application.command.CreateEventTypeCommand;
import com.penelopec.calservice.eventtype.domain.error.EventTypeError;
import com.penelopec.calservice.shared.validation.CommandValidator;
import com.penelopec.calservice.shared.validation.ValidationResult;

public class CreateEventTypeCommandValidator implements CommandValidator<CreateEventTypeCommand> {

  @Override
  public ValidationResult validate(CreateEventTypeCommand cmd) {
    return new ValidationResult()
      .addErrorIf(cmd.title() == null || cmd.title().isBlank(),
        "title", EventTypeError.VALIDATION_TITLE_REQUIRED)
      .addErrorIf(cmd.estateId() == null,
        "estateId", EventTypeError.VALIDATION_ESTATE_ID_REQUIRED);
  }
}
