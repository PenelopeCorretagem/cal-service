package com.penelopec.calservice.appointment.application.usecase;

import com.penelopec.calservice.appointment.application.command.AppointmentCommand;
import com.penelopec.calservice.appointment.application.output.AppointmentOutput;

public interface CreateAppointmentUseCase {
  AppointmentOutput execute(AppointmentCommand command);
}