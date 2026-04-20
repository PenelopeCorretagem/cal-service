package com.penelopec.calservice.appointment.application.service;

import com.penelopec.calservice.appointment.application.command.RescheduleAppointmentCommand;
import com.penelopec.calservice.appointment.application.mapper.AppointmentOutputMapper;
import com.penelopec.calservice.appointment.application.output.AppointmentOutput;
import com.penelopec.calservice.appointment.application.usecase.ChangeAppointmentUseCase;
import com.penelopec.calservice.appointment.application.util.AppointmentDateTimeParser;
import com.penelopec.calservice.appointment.domain.entity.Appointment;
import com.penelopec.calservice.appointment.domain.error.AppointmentError;
import com.penelopec.calservice.shared.error.core.ApplicationException;
import com.penelopec.calservice.shared.error.core.DomainException;
import com.penelopec.calservice.appointment.domain.gateway.CalComBookingGateway;
import com.penelopec.calservice.appointment.domain.repository.AppointmentRepository;
import com.penelopec.calservice.shared.validation.CommandValidator;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class RescheduleAppointmentService implements ChangeAppointmentUseCase {

  private final CalComBookingGateway bookingGateway;
  private final AppointmentRepository repository;
  private final CommandValidator<RescheduleAppointmentCommand> validator;

  public RescheduleAppointmentService(CalComBookingGateway bookingGateway,
                                      AppointmentRepository repository,
                                      CommandValidator<RescheduleAppointmentCommand> validator) {
    this.bookingGateway = bookingGateway;
    this.repository = repository;
    this.validator = validator;
  }

  @Override
  public AppointmentOutput execute(RescheduleAppointmentCommand command) {
    validator.validateAndThrow(command);

    Appointment appointment = repository.findById(command.appointmentId())
      .orElseThrow(() -> new DomainException(AppointmentError.NOT_FOUND, command.appointmentId()));

    if (appointment.getBookingUid() == null || appointment.getBookingUid().isBlank()) {
      throw new ApplicationException(AppointmentError.MISSING_BOOKING_UID, appointment.getId());
    }

    LocalDateTime newStart = AppointmentDateTimeParser.parseRequired(command.startDateTime(), "startDateTime");
    LocalDateTime newEnd = AppointmentDateTimeParser.parseRequired(command.endDateTime(), "endDateTime");

    appointment.reschedule(newStart, newEnd, command.reason());

    bookingGateway.rescheduleBooking(
      appointment.getBookingUid(),
      newStart.atOffset(ZoneOffset.UTC),
      newEnd.atOffset(ZoneOffset.UTC),
      command.reason()
    );

    Appointment saved = repository.save(appointment);

    return AppointmentOutputMapper.toOutput(saved);
  }
}
