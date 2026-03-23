package com.penelopec.calservice.appointment.application.service;

import com.penelopec.calservice.appointment.application.command.ConfirmAppointmentCommand;
import com.penelopec.calservice.appointment.application.mapper.AppointmentOutputMapper;
import com.penelopec.calservice.appointment.application.output.AppointmentOutput;
import com.penelopec.calservice.appointment.application.usecase.ConfirmAppointmentUseCase;
import com.penelopec.calservice.appointment.domain.entity.Appointment;
import com.penelopec.calservice.appointment.domain.exception.AppointmentNotFoundException;
import com.penelopec.calservice.appointment.domain.repository.AppointmentRepository;

public class ConfirmAppointmentService implements ConfirmAppointmentUseCase {

  private final AppointmentRepository repository;

  public ConfirmAppointmentService(AppointmentRepository repository) {
    this.repository = repository;
  }

  @Override
  public AppointmentOutput execute(ConfirmAppointmentCommand command) {
    Appointment appointment = repository.findById(command.appointmentId())
      .orElseThrow(() -> new AppointmentNotFoundException(
        "Agendamento não encontrado: " + command.appointmentId()));

    appointment.confirm();
    Appointment saved = repository.save(appointment);

    return AppointmentOutputMapper.toOutput(saved);
  }
}
