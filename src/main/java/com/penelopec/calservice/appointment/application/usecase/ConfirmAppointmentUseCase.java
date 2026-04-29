package com.penelopec.calservice.appointment.application.usecase;

import com.penelopec.calservice.appointment.application.command.ConfirmAppointmentCommand;
import com.penelopec.calservice.appointment.application.output.AppointmentOutput;

public interface ConfirmAppointmentUseCase {
  AppointmentOutput execute(ConfirmAppointmentCommand command);
}
