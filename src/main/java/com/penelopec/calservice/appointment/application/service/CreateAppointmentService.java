package com.penelopec.calservice.appointment.application.service;

import com.penelopec.calservice.appointment.application.command.AppointmentCommand;
import com.penelopec.calservice.appointment.application.mapper.AppointmentOutputMapper;
import com.penelopec.calservice.appointment.application.output.AppointmentOutput;
import com.penelopec.calservice.appointment.application.usecase.CreateAppointmentUseCase;
import com.penelopec.calservice.appointment.application.util.AppointmentDateTimeParser;
import com.penelopec.calservice.appointment.domain.entity.Appointment;
import com.penelopec.calservice.appointment.domain.gateway.CalComBookingGateway;
import com.penelopec.calservice.appointment.domain.gateway.CalComBookingGateway.BookingResult;
import com.penelopec.calservice.appointment.domain.gateway.CalComBookingGateway.CreateBookingRequest;
import com.penelopec.calservice.appointment.domain.repository.AppointmentRepository;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class CreateAppointmentService implements CreateAppointmentUseCase {

  private final CalComBookingGateway bookingGateway;
  private final AppointmentRepository repository;

  public CreateAppointmentService(CalComBookingGateway bookingGateway,
                                  AppointmentRepository repository) {
    this.bookingGateway = bookingGateway;
    this.repository = repository;
  }

  @Override
  public AppointmentOutput execute(AppointmentCommand command) {
    LocalDateTime start = AppointmentDateTimeParser.parseRequired(command.startDateTime(), "startDateTime");
    LocalDateTime end = AppointmentDateTimeParser.parseRequired(command.endDateTime(), "endDateTime");

    Appointment appointment = Appointment.createNew(
      command.eventTypeId(),
      command.clientId(),
      command.estateAgentId(),
      command.estateId(),
      start,
      end
    );

    BookingResult result = bookingGateway.createBooking(new CreateBookingRequest(
      command.eventTypeId(),
      start.atOffset(ZoneOffset.UTC),
      end.atOffset(ZoneOffset.UTC),
      command.attendeeName(),
      command.attendeeEmail(),
      command.notes()
    ));

    appointment.assignBookingUid(result.uid());

    Appointment saved = repository.save(appointment);

    return AppointmentOutputMapper.toOutput(saved);
  }
}
