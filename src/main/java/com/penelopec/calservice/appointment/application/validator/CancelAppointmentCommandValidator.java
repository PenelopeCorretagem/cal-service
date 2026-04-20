package com.penelopec.calservice.appointment.application.validator;

import com.penelopec.calservice.appointment.application.command.CancelAppointmentCommand;
import com.penelopec.calservice.appointment.domain.error.AppointmentValidationCode;
import com.penelopec.calservice.shared.validation.CommandValidator;
import com.penelopec.calservice.shared.validation.ValidationResult;

public class CancelAppointmentCommandValidator implements CommandValidator<CancelAppointmentCommand> {

  @Override
  public ValidationResult validate(CancelAppointmentCommand cmd) {
    return new ValidationResult()
      .addErrorIf(cmd.appointmentId() == null,
        "appointmentId", AppointmentValidationCode.APPOINTMENT_ID_REQUIRED);
  }
}
