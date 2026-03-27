package com.penelopec.calservice.appointment.application.service;

import com.penelopec.calservice.appointment.application.mapper.AppointmentOutputMapper;
import com.penelopec.calservice.appointment.application.output.AppointmentOutput;
import com.penelopec.calservice.appointment.application.usecase.GetAppointmentUseCase;
import com.penelopec.calservice.appointment.domain.entity.Appointment;
import com.penelopec.calservice.appointment.domain.exception.AppointmentNotFoundException;
import com.penelopec.calservice.appointment.domain.repository.AppointmentRepository;

public class GetAppointmentService implements GetAppointmentUseCase {

  private final AppointmentRepository repository;

  public GetAppointmentService(AppointmentRepository repository) {
    this.repository = repository;
  }

  @Override
  public AppointmentOutput execute(Long id) {
    Appointment appointment = repository.findById(id)
      .orElseThrow(() -> new AppointmentNotFoundException("Agendamento não encontrado: " + id));

    return AppointmentOutputMapper.toOutput(appointment);
  }
}
