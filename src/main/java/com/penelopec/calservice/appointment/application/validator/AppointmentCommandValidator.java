package com.penelopec.calservice.appointment.application.validator;

import com.penelopec.calservice.appointment.application.command.AppointmentCommand;
import com.penelopec.calservice.appointment.application.util.AppointmentDateTimeParser;
import com.penelopec.calservice.appointment.domain.error.AppointmentValidationCode;
import com.penelopec.calservice.shared.validation.CommandValidator;
import com.penelopec.calservice.shared.validation.ValidationResult;

public class AppointmentCommandValidator implements CommandValidator<AppointmentCommand> {

  @Override
  public ValidationResult validate(AppointmentCommand cmd) {
    var result = new ValidationResult()
      .addErrorIf(cmd.eventTypeId() == null,
        "eventTypeId", AppointmentValidationCode.EVENT_TYPE_ID_REQUIRED)
      .addErrorIf(cmd.attendeeName() == null || cmd.attendeeName().isBlank(),
        "attendeeName", AppointmentValidationCode.ATTENDEE_NAME_REQUIRED)
      .addErrorIf(cmd.attendeeEmail() == null || cmd.attendeeEmail().isBlank(),
        "attendeeEmail", AppointmentValidationCode.ATTENDEE_EMAIL_REQUIRED)
      .addErrorIf(cmd.startDateTime() == null || cmd.startDateTime().isBlank(),
        "startDateTime", AppointmentValidationCode.START_DATETIME_REQUIRED)
      .addErrorIf(cmd.endDateTime() == null || cmd.endDateTime().isBlank(),
        "endDateTime", AppointmentValidationCode.END_DATETIME_REQUIRED);

    // Valida formato somente se o valor foi fornecido
    if (cmd.startDateTime() != null && !cmd.startDateTime().isBlank()) {
      result.addErrorIf(
        AppointmentDateTimeParser.parseOptional(cmd.startDateTime()).isEmpty(),
        "startDateTime", AppointmentValidationCode.START_DATETIME_INVALID);
    }
    if (cmd.endDateTime() != null && !cmd.endDateTime().isBlank()) {
      result.addErrorIf(
        AppointmentDateTimeParser.parseOptional(cmd.endDateTime()).isEmpty(),
        "endDateTime", AppointmentValidationCode.END_DATETIME_INVALID);
    }

    return result;
  }
}
