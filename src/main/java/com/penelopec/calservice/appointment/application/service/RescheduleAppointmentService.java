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
import java.time.OffsetDateTime;
import java.time.ZoneId;

public class RescheduleAppointmentService implements ChangeAppointmentUseCase {

  private static final ZoneId BRAZIL_TIME_ZONE = ZoneId.of("America/Sao_Paulo");
  private static final int DEFAULT_APPOINTMENT_DURATION_MINUTES = 60;

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

    if (appointment.getEstateAgentId() != null
      && repository.existsActiveByEstateAgentAndStartDateTimeExcludingId(
        appointment.getEstateAgentId(),
        newStart,
        appointment.getId())) {
      throw new DomainException(AppointmentError.SCHEDULE_CONFLICT);
    }

    var bookingResult = bookingGateway.rescheduleBooking(
      appointment.getBookingUid(),
      newStart.atZone(BRAZIL_TIME_ZONE).toOffsetDateTime(),
      null,
      command.reason()
    );

    LocalDateTime actualEnd = resolveEndDateTime(newStart, bookingResult.endTime());
    appointment.reschedule(newStart, actualEnd, command.reason());

    if (bookingResult.uid() != null
      && !bookingResult.uid().isBlank()) {
      appointment.assignBookingUid(bookingResult.uid());
    }

    Appointment saved = repository.save(appointment);

    return AppointmentOutputMapper.toOutput(saved);
  }

  private LocalDateTime resolveEndDateTime(LocalDateTime startDateTime, OffsetDateTime calComEndDateTime) {
    if (calComEndDateTime == null) {
      return startDateTime.plusMinutes(DEFAULT_APPOINTMENT_DURATION_MINUTES);
    }

    return calComEndDateTime.atZoneSameInstant(BRAZIL_TIME_ZONE).toLocalDateTime();
  }
}
