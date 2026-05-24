package com.penelopec.calservice.appointment.application.service;

import com.penelopec.calservice.appointment.application.mapper.AppointmentOutputMapper;
import com.penelopec.calservice.appointment.application.output.AppointmentOutput;
import com.penelopec.calservice.appointment.application.query.ExportAppointmentsQuery;
import com.penelopec.calservice.appointment.application.usecase.ExportAppointmentsUseCase;
import com.penelopec.calservice.appointment.domain.repository.AppointmentRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class ExportAppointmentsService implements ExportAppointmentsUseCase {

  private final AppointmentRepository repository;

  public ExportAppointmentsService(AppointmentRepository repository) {
    this.repository = repository;
  }

  @Override
  public List<AppointmentOutput> execute(ExportAppointmentsQuery query) {
    LocalDateTime startDate = parseStart(query.startDate());
    LocalDateTime endDate = parseEnd(query.endDate());

    return repository.findForExport(query.userId(), startDate, endDate).stream()
      .map(AppointmentOutputMapper::toOutput)
      .toList();
  }

  private LocalDateTime parseStart(String value) {
    return value == null || value.isBlank() ? null : LocalDate.parse(value.trim()).atStartOfDay();
  }

  private LocalDateTime parseEnd(String value) {
    return value == null || value.isBlank() ? null : LocalDate.parse(value.trim()).atTime(LocalTime.MAX);
  }

}
