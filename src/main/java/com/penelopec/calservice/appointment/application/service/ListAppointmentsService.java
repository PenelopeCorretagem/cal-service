package com.penelopec.calservice.appointment.application.service;

import com.penelopec.calservice.appointment.application.mapper.AppointmentOutputMapper;
import com.penelopec.calservice.appointment.application.output.AppointmentOutput;
import com.penelopec.calservice.appointment.application.output.ListAppointmentsOutput;
import com.penelopec.calservice.appointment.application.query.ListAppointmentsQuery;
import com.penelopec.calservice.appointment.application.util.AppointmentDateTimeParser;
import com.penelopec.calservice.appointment.application.usecase.ListAppointmentsUseCase;
import com.penelopec.calservice.appointment.domain.repository.AppointmentRepository;
import com.penelopec.calservice.appointment.domain.valueobject.Status;

import java.time.LocalDateTime;
import java.util.List;

public class ListAppointmentsService implements ListAppointmentsUseCase {

  private final AppointmentRepository repository;

  public ListAppointmentsService(AppointmentRepository repository) {
    this.repository = repository;
  }

  @Override
  public ListAppointmentsOutput execute(ListAppointmentsQuery query) {
    int page = query.page() == null ? 0 : query.page();
    int size = query.size() == null ? 20 : query.size();

    if (page < 0) {
      throw new IllegalArgumentException("page deve ser maior ou igual a zero");
    }
    if (size <= 0) {
      throw new IllegalArgumentException("size deve ser maior que zero");
    }

    Status status = parseStatus(query.status());
    LocalDateTime startDateTime = parseDateTime(query.startDateTime(), "startDateTime");
    LocalDateTime endDateTime = parseDateTime(query.endDateTime(), "endDateTime");

    List<AppointmentOutput> filtered = repository.findByFilters(
      query.clientId(),
      query.estateAgentId(),
      query.estateId(),
      status,
      startDateTime,
      endDateTime
    ).stream()
      .map(AppointmentOutputMapper::toOutput)
      .toList();

    long totalElements = filtered.size();
    int totalPages = totalElements == 0 ? 0 : (int) Math.ceil((double) totalElements / size);

    int fromIndex = page * size;
    List<AppointmentOutput> pageItems = fromIndex >= filtered.size()
      ? List.of()
      : filtered.subList(fromIndex, Math.min(fromIndex + size, filtered.size()));

    return new ListAppointmentsOutput(pageItems, page, size, totalElements, totalPages);
  }

  private Status parseStatus(String rawStatus) {
    if (rawStatus == null || rawStatus.isBlank()) {
      return null;
    }

    try {
      return Status.valueOf(rawStatus.trim().toUpperCase());
    } catch (IllegalArgumentException ex) {
      throw new IllegalArgumentException("status inválido: " + rawStatus);
    }
  }

  private LocalDateTime parseDateTime(String rawDateTime, String fieldName) {
    return AppointmentDateTimeParser.parseOptional(rawDateTime)
      .orElseGet(() -> {
        if (rawDateTime == null || rawDateTime.isBlank()) {
          return null;
        }
        throw new IllegalArgumentException("data/hora inválida para " + fieldName + ": " + rawDateTime);
      });
  }
}
