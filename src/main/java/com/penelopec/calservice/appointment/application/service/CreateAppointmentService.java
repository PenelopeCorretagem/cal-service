package com.penelopec.calservice.appointment.application.service;

import com.penelopec.calservice.appointment.application.command.AppointmentCommand;
import com.penelopec.calservice.appointment.application.mapper.AppointmentOutputMapper;
import com.penelopec.calservice.appointment.application.output.AppointmentOutput;
import com.penelopec.calservice.appointment.application.usecase.CreateAppointmentUseCase;
import com.penelopec.calservice.appointment.application.util.AppointmentDateTimeParser;
import com.penelopec.calservice.appointment.domain.entity.Appointment;
import com.penelopec.calservice.appointment.domain.error.AppointmentError;
import com.penelopec.calservice.appointment.domain.gateway.CalComBookingGateway;
import com.penelopec.calservice.appointment.domain.gateway.CalComBookingGateway.BookingResult;
import com.penelopec.calservice.appointment.domain.gateway.CalComBookingGateway.CreateBookingRequest;
import com.penelopec.calservice.appointment.domain.repository.AppointmentRepository;
import com.penelopec.calservice.shared.error.core.DomainException;
import com.penelopec.calservice.shared.validation.CommandValidator;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class CreateAppointmentService implements CreateAppointmentUseCase {

  private static final int DEFAULT_DURATION_MINUTES = 60;
  private static final ZoneId BRAZIL_TIME_ZONE = ZoneId.of("America/Sao_Paulo");

  private final CalComBookingGateway bookingGateway;
  private final AppointmentRepository repository;
  private final CommandValidator<AppointmentCommand> validator;

  public CreateAppointmentService(CalComBookingGateway bookingGateway,
                                  AppointmentRepository repository,
                                  CommandValidator<AppointmentCommand> validator) {
    this.bookingGateway = bookingGateway;
    this.repository = repository;
    this.validator = validator;
  }

  @Override
  public AppointmentOutput execute(AppointmentCommand command) {
    validator.validateAndThrow(command);

    LocalDateTime start = AppointmentDateTimeParser.parseRequired(command.startDateTime(), "startDateTime");
    LocalDateTime end = start.plusMinutes(DEFAULT_DURATION_MINUTES);

    if (command.estateAgentId() != null
      && repository.existsActiveByEstateAgentAndStartDateTime(command.estateAgentId(), start)) {
      throw new DomainException(AppointmentError.SCHEDULE_CONFLICT);
    }

    Appointment appointment = Appointment.createNew(
      command.eventTypeId(),
      command.clientId(),
      command.estateAgentId(),
      start,
      end,
      command.attendeeName(),
      command.attendeeEmail(),
      command.notes()
    );

    BookingResult result = bookingGateway.createBooking(new CreateBookingRequest(
      command.eventTypeId(),
      start.atZone(BRAZIL_TIME_ZONE).toOffsetDateTime(),
      end.atZone(BRAZIL_TIME_ZONE).toOffsetDateTime(),
      command.attendeeName(),
      command.attendeeEmail(),
      command.notes()
    ));

    appointment.assignBookingUid(result.uid());

    Appointment saved = repository.save(appointment);

    return AppointmentOutputMapper.toOutput(saved);
  }
}
