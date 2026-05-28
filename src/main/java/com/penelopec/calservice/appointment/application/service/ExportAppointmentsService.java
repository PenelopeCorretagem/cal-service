package com.penelopec.calservice.appointment.application.service;

import com.penelopec.calservice.appointment.application.output.ExportAppointmentOutput;
import com.penelopec.calservice.appointment.application.query.ExportAppointmentsQuery;
import com.penelopec.calservice.appointment.application.validator.ExportAppointmentsQueryValidator;
import com.penelopec.calservice.appointment.application.usecase.ExportAppointmentsUseCase;
import com.penelopec.calservice.appointment.domain.entity.Appointment;
import com.penelopec.calservice.appointment.domain.repository.AppointmentRepository;
import com.penelopec.calservice.appointment.domain.valueobject.Status;
import com.penelopec.calservice.eventtype.domain.repository.EventTypeRepository;
import com.penelopec.calservice.user.domain.gateway.UserGateway;
import com.penelopec.calservice.user.domain.valueobject.UserSummary;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExportAppointmentsService implements ExportAppointmentsUseCase {

  private final AppointmentRepository repository;
  private final EventTypeRepository eventTypeRepository;
  private final UserGateway userGateway;
  private final ExportAppointmentsQueryValidator queryValidator;

  public ExportAppointmentsService(AppointmentRepository repository,
                                   EventTypeRepository eventTypeRepository,
                                   UserGateway userGateway,
                                   ExportAppointmentsQueryValidator queryValidator) {
    this.repository = repository;
    this.eventTypeRepository = eventTypeRepository;
    this.userGateway = userGateway;
    this.queryValidator = queryValidator;
  }

  @Override
  public List<ExportAppointmentOutput> execute(ExportAppointmentsQuery query) {
    queryValidator.validate(query).throwIfHasErrors();

    LocalDateTime startDate = parseStart(query.startDate());
    LocalDateTime endDate = parseEnd(query.endDate());
    Status status = parseStatus(query.status());

    Map<Long, String> eventTypeNames = new HashMap<>();
    Map<Long, String> userNames = new HashMap<>();

    return repository.findForExport(query.userId(), startDate, endDate, status).stream()
      .map(appointment -> toExportOutput(appointment, eventTypeNames, userNames))
      .toList();
  }

  private ExportAppointmentOutput toExportOutput(Appointment appointment,
                                                 Map<Long, String> eventTypeNames,
                                                 Map<Long, String> userNames) {
    String advertisementName = resolveEventTypeName(appointment.getEventTypeId(), eventTypeNames);
    String estateAgentName = resolveUserName(appointment.getEstateAgentId(), userNames);
    String clientName = appointment.getAttendeeName();
    String status = appointment.getStatus() == null ? null : appointment.getStatus().getDescricao();

    return new ExportAppointmentOutput(
      appointment.getId(),
      status,
      appointment.getStartDateTime(),
      appointment.getEndDateTime(),
      appointment.getAttendeeName(),
      appointment.getAttendeeEmail(),
      appointment.getNotes(),
      appointment.getReason(),
      appointment.getCreatedAt(),
      appointment.getUpdatedAt(),
      advertisementName,
      clientName,
      estateAgentName
    );
  }

  private String resolveEventTypeName(Long eventTypeId, Map<Long, String> eventTypeNames) {
    if (eventTypeId == null) {
      return null;
    }

    return eventTypeNames.computeIfAbsent(eventTypeId, id ->
      eventTypeRepository.findById(id)
        .map(eventType -> eventType.getTitle())
        .orElse(null));
  }

  private String resolveUserName(Long userId, Map<Long, String> userNames) {
    if (userId == null) {
      return null;
    }

    return userNames.computeIfAbsent(userId, id ->
      userGateway.findById(id)
        .map(UserSummary::name)
        .orElse(null));
  }

  private Status parseStatus(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    return Status.valueOf(value.trim().toUpperCase());
  }

  private LocalDateTime parseStart(String value) {
    return value == null || value.isBlank() ? null : LocalDate.parse(value.trim()).atStartOfDay();
  }

  private LocalDateTime parseEnd(String value) {
    return value == null || value.isBlank() ? null : LocalDate.parse(value.trim()).atTime(LocalTime.MAX);
  }

}
