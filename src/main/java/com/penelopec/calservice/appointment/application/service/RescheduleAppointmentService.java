package com.penelopec.calservice.appointment.application.service;

import com.penelopec.calservice.appointment.application.command.RescheduleAppointmentCommand;
import com.penelopec.calservice.appointment.application.mapper.AppointmentOutputMapper;
import com.penelopec.calservice.appointment.application.output.AppointmentOutput;
import com.penelopec.calservice.appointment.application.usecase.ChangeAppointmentUseCase;
import com.penelopec.calservice.appointment.application.util.AppointmentDateTimeParser;
import com.penelopec.calservice.appointment.domain.entity.Appointment;
import com.penelopec.calservice.appointment.domain.exception.AppointmentNotFoundException;
import com.penelopec.calservice.appointment.domain.gateway.CalComBookingGateway;
import com.penelopec.calservice.appointment.domain.repository.AppointmentRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class RescheduleAppointmentService implements ChangeAppointmentUseCase {

  private final CalComBookingGateway bookingGateway;
  private final AppointmentRepository repository;

  public RescheduleAppointmentService(CalComBookingGateway bookingGateway,
                                      AppointmentRepository repository) {
    this.bookingGateway = bookingGateway;
    this.repository = repository;
  }

  @Override
  public AppointmentOutput execute(RescheduleAppointmentCommand command) {
    Appointment appointment = repository.findById(command.appointmentId())
      .orElseThrow(() -> new AppointmentNotFoundException(
        "Agendamento não encontrado: " + command.appointmentId()));

    if (appointment.getBookingUid() == null || appointment.getBookingUid().isBlank()) {
      throw new IllegalStateException("Agendamento sem bookingUid para reagendamento remoto");
    }

    LocalDateTime newStart = AppointmentDateTimeParser.parseRequired(command.startDateTime(), "startDateTime");
    LocalDateTime newEnd = AppointmentDateTimeParser.parseRequired(command.endDateTime(), "endDateTime");

    appointment.reschedule(newStart, newEnd);

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
