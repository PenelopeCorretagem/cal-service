package com.penelopec.calservice.appointment.application.service;

import com.penelopec.calservice.appointment.application.mapper.AppointmentOutputMapper;
import com.penelopec.calservice.appointment.application.output.AppointmentOutput;
import com.penelopec.calservice.appointment.application.output.ListAppointmentsOutput;
import com.penelopec.calservice.appointment.application.query.ListAppointmentsQuery;
import com.penelopec.calservice.appointment.application.util.AppointmentDateTimeParser;
import com.penelopec.calservice.appointment.application.usecase.ListAppointmentsUseCase;
import com.penelopec.calservice.appointment.application.validator.ListAppointmentsQueryValidator;
import com.penelopec.calservice.appointment.domain.entity.Appointment;
import com.penelopec.calservice.appointment.domain.repository.AppointmentRepository;
import com.penelopec.calservice.appointment.domain.repository.PageResult;
import com.penelopec.calservice.appointment.domain.valueobject.Status;

import java.time.LocalDateTime;
import java.util.List;

public class ListAppointmentsService implements ListAppointmentsUseCase {

  private final AppointmentRepository repository;
  private final ListAppointmentsQueryValidator queryValidator;

  public ListAppointmentsService(AppointmentRepository repository,
                                 ListAppointmentsQueryValidator queryValidator) {
    this.repository = repository;
    this.queryValidator = queryValidator;
  }

  @Override
  public ListAppointmentsOutput execute(ListAppointmentsQuery query) {
    queryValidator.validateAndThrow(query);

    int page = query.page() == null ? 0 : query.page();
    int size = query.size() == null ? 20 : query.size();

    Status status = parseStatus(query.status());
    LocalDateTime startDateTime = AppointmentDateTimeParser.parseOptional(query.startDateTime()).orElse(null);
    LocalDateTime endDateTime = AppointmentDateTimeParser.parseOptional(query.endDateTime()).orElse(null);

    PageResult<Appointment> pageResult = repository.findByFilters(
      query.clientId(),
      query.estateAgentId(),
      query.estateId(),
      status,
      startDateTime,
      endDateTime,
      page,
      size
    );

    List<AppointmentOutput> items = pageResult.content().stream()
      .map(AppointmentOutputMapper::toOutput)
      .toList();

    return new ListAppointmentsOutput(items, pageResult.page(), pageResult.size(),
      pageResult.totalElements(), pageResult.totalPages());
  }

  private Status parseStatus(String rawStatus) {
    if (rawStatus == null || rawStatus.isBlank()) {
      return null;
    }
    return Status.valueOf(rawStatus.trim().toUpperCase());
  }
}
