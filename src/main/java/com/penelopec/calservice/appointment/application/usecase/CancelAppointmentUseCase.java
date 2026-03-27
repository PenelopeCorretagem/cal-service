package com.penelopec.calservice.appointment.application.usecase;

import com.penelopec.calservice.appointment.application.command.CancelAppointmentCommand;
import com.penelopec.calservice.appointment.application.output.AppointmentOutput;

public interface CancelAppointmentUseCase {
  AppointmentOutput execute(CancelAppointmentCommand command);
}
