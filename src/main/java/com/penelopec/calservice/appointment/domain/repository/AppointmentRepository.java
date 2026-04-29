package com.penelopec.calservice.appointment.domain.repository;

import com.penelopec.calservice.appointment.domain.entity.Appointment;
import com.penelopec.calservice.appointment.domain.valueobject.Status;

import java.time.LocalDateTime;
import java.util.Optional;

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

  void deleteById(Long id);
}
