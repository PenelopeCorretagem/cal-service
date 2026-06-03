package com.penelopec.calservice.appointment.domain.repository;

import com.penelopec.calservice.appointment.domain.entity.Appointment;
import com.penelopec.calservice.appointment.domain.valueobject.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface AppointmentRepository {

  Appointment save(Appointment appointment);

  Optional<Appointment> findById(Long id);

  boolean existsActiveByEstateAgentAndStartDateTime(Long estateAgentId, LocalDateTime startDateTime);

  boolean existsActiveByEstateAgentAndStartDateTimeExcludingId(Long estateAgentId,
                                                               LocalDateTime startDateTime,
                                                               Long excludedId);

  PageResult<Appointment> findByFilters(Long clientId, Long estateAgentId, Long estateId,
                                        Status status, LocalDateTime startDate,
                                        LocalDateTime endDate, int page, int size);

  PageResult<AppointmentReportRow> findForReport(Long clientId, Long estateAgentId,
                                                 Set<Long> estateIds,
                                                 Status status, LocalDateTime startDate,
                                                 LocalDateTime endDate, int page, int size);

  List<Appointment> findForExport(Long userId, LocalDateTime startDate, LocalDateTime endDate, Status status);

  void deleteById(Long id);
}
