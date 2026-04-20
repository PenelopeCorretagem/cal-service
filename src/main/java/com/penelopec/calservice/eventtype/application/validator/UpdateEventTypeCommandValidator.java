package com.penelopec.calservice.eventtype.application.validator;

import com.penelopec.calservice.eventtype.application.command.UpdateEventTypeCommand;
import com.penelopec.calservice.eventtype.domain.error.EventTypeError;
import com.penelopec.calservice.shared.validation.CommandValidator;
import com.penelopec.calservice.shared.validation.ValidationResult;

public class UpdateEventTypeCommandValidator implements CommandValidator<UpdateEventTypeCommand> {

  @Override
  public ValidationResult validate(UpdateEventTypeCommand cmd) {
    var result = new ValidationResult()
      .addErrorIf(cmd.eventTypeId() == null,
        "eventTypeId", EventTypeError.VALIDATION_EVENT_TYPE_ID_REQUIRED);

    // Título é opcional no update, mas se informado não pode ser blank
    if (cmd.title() != null) {
      result.addErrorIf(cmd.title().isBlank(), "title", EventTypeError.VALIDATION_TITLE_REQUIRED);
    }

    return result;
  }
}
