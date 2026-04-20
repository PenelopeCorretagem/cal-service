package com.penelopec.calservice.appointment.application.validator;

import com.penelopec.calservice.appointment.application.command.RescheduleAppointmentCommand;
import com.penelopec.calservice.appointment.application.util.AppointmentDateTimeParser;
import com.penelopec.calservice.appointment.domain.error.AppointmentError;
import com.penelopec.calservice.shared.validation.CommandValidator;
import com.penelopec.calservice.shared.validation.ValidationResult;

public class RescheduleAppointmentCommandValidator implements CommandValidator<RescheduleAppointmentCommand> {

  @Override
  public ValidationResult validate(RescheduleAppointmentCommand cmd) {
    var result = new ValidationResult()
      .addErrorIf(cmd.appointmentId() == null,
        "appointmentId", AppointmentError.APPOINTMENT_ID_REQUIRED)
      .addErrorIf(cmd.startDateTime() == null || cmd.startDateTime().isBlank(),
        "startDateTime", AppointmentError.START_DATETIME_REQUIRED)
      .addErrorIf(cmd.endDateTime() == null || cmd.endDateTime().isBlank(),
        "endDateTime", AppointmentError.END_DATETIME_REQUIRED);

    if (cmd.startDateTime() != null && !cmd.startDateTime().isBlank()) {
      result.addErrorIf(
        AppointmentDateTimeParser.parseOptional(cmd.startDateTime()).isEmpty(),
        "startDateTime", AppointmentError.START_DATETIME_INVALID);
    }
    if (cmd.endDateTime() != null && !cmd.endDateTime().isBlank()) {
      result.addErrorIf(
        AppointmentDateTimeParser.parseOptional(cmd.endDateTime()).isEmpty(),
        "endDateTime", AppointmentError.END_DATETIME_INVALID);
    }

    return result;
  }
}
