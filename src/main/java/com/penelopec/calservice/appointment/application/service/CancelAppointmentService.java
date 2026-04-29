package com.penelopec.calservice.appointment.application.service;

import com.penelopec.calservice.appointment.application.command.CancelAppointmentCommand;
import com.penelopec.calservice.appointment.application.mapper.AppointmentOutputMapper;
import com.penelopec.calservice.appointment.application.output.AppointmentOutput;
import com.penelopec.calservice.appointment.application.usecase.CancelAppointmentUseCase;
import com.penelopec.calservice.appointment.application.validator.CancelAppointmentCommandValidator;
import com.penelopec.calservice.appointment.domain.entity.Appointment;
import com.penelopec.calservice.appointment.domain.error.AppointmentError;
import com.penelopec.calservice.shared.error.core.ApplicationException;
import com.penelopec.calservice.shared.error.core.DomainException;
import com.penelopec.calservice.appointment.domain.gateway.CalComBookingGateway;
import com.penelopec.calservice.appointment.domain.repository.AppointmentRepository;
import com.penelopec.calservice.shared.validation.CommandValidator;

public class CancelAppointmentService implements CancelAppointmentUseCase {

  private final CalComBookingGateway bookingGateway;
  private final AppointmentRepository repository;
  private final CommandValidator<CancelAppointmentCommand> validator;

  public CancelAppointmentService(CalComBookingGateway bookingGateway,
                                  AppointmentRepository repository,
                                  CommandValidator<CancelAppointmentCommand> validator) {
    this.bookingGateway = bookingGateway;
    this.repository = repository;
    this.validator = validator;
  }

  @Override
  public AppointmentOutput execute(CancelAppointmentCommand command) {
    validator.validateAndThrow(command);

    Appointment appointment = repository.findById(command.appointmentId())
      .orElseThrow(() -> new DomainException(AppointmentError.NOT_FOUND, command.appointmentId()));

    if (appointment.getBookingUid() == null || appointment.getBookingUid().isBlank()) {
      throw new ApplicationException(AppointmentError.MISSING_BOOKING_UID, appointment.getId());
    }

    appointment.cancel(command.reason());

    bookingGateway.cancelBooking(appointment.getBookingUid(), command.reason());

    Appointment saved = repository.save(appointment);

    return AppointmentOutputMapper.toOutput(saved);
  }
}
