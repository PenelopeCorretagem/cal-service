package com.penelopec.calservice.eventtype.application.validator;

import com.penelopec.calservice.eventtype.application.command.HandleEstateChangedCommand;
import com.penelopec.calservice.eventtype.domain.error.EventTypeError;
import com.penelopec.calservice.shared.validation.CommandValidator;
import com.penelopec.calservice.shared.validation.ValidationResult;

public class HandleEstateChangedCommandValidator implements CommandValidator<HandleEstateChangedCommand> {

  @Override
  public ValidationResult validate(HandleEstateChangedCommand cmd) {
    return new ValidationResult()
      .addErrorIf(cmd.estateId() == null,
        "estateId", EventTypeError.VALIDATION_ESTATE_CHANGED_ID_REQUIRED);
  }
}
