package com.penelopec.calservice.appointment.application.service;

import com.penelopec.calservice.appointment.application.command.CancelAppointmentCommand;
import com.penelopec.calservice.appointment.application.mapper.AppointmentOutputMapper;
import com.penelopec.calservice.appointment.application.output.AppointmentOutput;
import com.penelopec.calservice.appointment.application.usecase.CancelAppointmentUseCase;
import com.penelopec.calservice.appointment.domain.entity.Appointment;
import com.penelopec.calservice.appointment.domain.exception.AppointmentNotFoundException;
import com.penelopec.calservice.appointment.domain.gateway.CalComBookingGateway;
import com.penelopec.calservice.appointment.domain.repository.AppointmentRepository;

public class CancelAppointmentService implements CancelAppointmentUseCase {

  private final CalComBookingGateway bookingGateway;
  private final AppointmentRepository repository;

  public CancelAppointmentService(CalComBookingGateway bookingGateway,
                                  AppointmentRepository repository) {
    this.bookingGateway = bookingGateway;
    this.repository = repository;
  }

  @Override
  public AppointmentOutput execute(CancelAppointmentCommand command) {
    Appointment appointment = repository.findById(command.appointmentId())
      .orElseThrow(() -> new AppointmentNotFoundException(
        "Agendamento não encontrado: " + command.appointmentId()));

    if (appointment.getBookingUid() == null || appointment.getBookingUid().isBlank()) {
      throw new IllegalStateException("Agendamento sem bookingUid para cancelamento remoto");
    }

    appointment.cancel();

    bookingGateway.cancelBooking(appointment.getBookingUid(), command.reason());

    Appointment saved = repository.save(appointment);

    return AppointmentOutputMapper.toOutput(saved);
  }
}
