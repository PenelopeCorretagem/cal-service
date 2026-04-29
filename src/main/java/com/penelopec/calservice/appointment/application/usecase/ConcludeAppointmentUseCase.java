package com.penelopec.calservice.appointment.application.usecase;

import com.penelopec.calservice.appointment.application.command.ConcludeAppointmentCommand;
import com.penelopec.calservice.appointment.application.output.AppointmentOutput;

public interface ConcludeAppointmentUseCase {
  AppointmentOutput execute(ConcludeAppointmentCommand command);
}
