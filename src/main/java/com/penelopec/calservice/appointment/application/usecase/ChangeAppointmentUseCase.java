package com.penelopec.calservice.appointment.application.usecase;

import com.penelopec.calservice.appointment.application.command.RescheduleAppointmentCommand;
import com.penelopec.calservice.appointment.application.output.AppointmentOutput;

public interface ChangeAppointmentUseCase {
  AppointmentOutput execute(RescheduleAppointmentCommand command);
}