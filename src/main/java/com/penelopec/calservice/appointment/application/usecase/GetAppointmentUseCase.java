package com.penelopec.calservice.appointment.application.usecase;

import com.penelopec.calservice.appointment.application.output.AppointmentOutput;

public interface GetAppointmentUseCase {
  AppointmentOutput execute(Long id);
}
