package com.penelopec.calservice.appointment.application.service;

import com.penelopec.calservice.appointment.application.usecase.DeleteAppointmentUseCase;
import com.penelopec.calservice.appointment.domain.entity.Appointment;
import com.penelopec.calservice.appointment.domain.exception.AppointmentNotFoundException;
import com.penelopec.calservice.appointment.domain.gateway.CalComBookingGateway;
import com.penelopec.calservice.appointment.domain.repository.AppointmentRepository;

public class DeleteAppointmentService implements DeleteAppointmentUseCase {

  private final CalComBookingGateway bookingGateway;
  private final AppointmentRepository repository;

  public DeleteAppointmentService(CalComBookingGateway bookingGateway,
                                  AppointmentRepository repository) {
    this.bookingGateway = bookingGateway;
    this.repository = repository;
  }

  @Override
  public void execute(Long id) {
    Appointment appointment = repository.findById(id)
      .orElseThrow(() -> new AppointmentNotFoundException("Agendamento não encontrado: " + id));

    if (appointment.getBookingUid() != null) {
      bookingGateway.cancelBooking(appointment.getBookingUid(), "Removido pelo sistema");
    }

    repository.deleteById(id);
  }
}
