package com.penelopec.calservice.appointment.application.validator;

import com.penelopec.calservice.appointment.application.command.AppointmentCommand;
import com.penelopec.calservice.appointment.application.util.AppointmentDateTimeParser;
import com.penelopec.calservice.appointment.domain.error.AppointmentError;
import com.penelopec.calservice.shared.validation.CommandValidator;
import com.penelopec.calservice.shared.validation.ValidationResult;

public class AppointmentCommandValidator implements CommandValidator<AppointmentCommand> {

  @Override
  public ValidationResult validate(AppointmentCommand cmd) {
    var result = new ValidationResult()
      .addErrorIf(cmd.eventTypeId() == null,
        "eventTypeId", AppointmentError.EVENT_TYPE_ID_REQUIRED)
      .addErrorIf(cmd.attendeeName() == null || cmd.attendeeName().isBlank(),
        "attendeeName", AppointmentError.ATTENDEE_NAME_REQUIRED)
      .addErrorIf(cmd.attendeeEmail() == null || cmd.attendeeEmail().isBlank(),
        "attendeeEmail", AppointmentError.ATTENDEE_EMAIL_REQUIRED)
      .addErrorIf(cmd.startDateTime() == null || cmd.startDateTime().isBlank(),
        "startDateTime", AppointmentError.START_DATETIME_REQUIRED);

    // Valida formato somente se o valor foi fornecido
    if (cmd.startDateTime() != null && !cmd.startDateTime().isBlank()) {
      result.addErrorIf(
        AppointmentDateTimeParser.parseOptional(cmd.startDateTime()).isEmpty(),
        "startDateTime", AppointmentError.START_DATETIME_INVALID);
    }

    return result;
  }
}
