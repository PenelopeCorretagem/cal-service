package com.penelopec.calservice.appointment.domain.repository;

import com.penelopec.calservice.appointment.domain.entity.Appointment;
import com.penelopec.calservice.appointment.domain.valueobject.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository {

  Appointment save(Appointment appointment);

  Optional<Appointment> findById(Long id);

  Optional<Appointment> findByBookingUid(String bookingUid);

  List<Appointment> findAll();

  List<Appointment> findByFilters(Long clientId, Long estateAgentId, Long estateId,
                                  Status status, LocalDateTime startDate, LocalDateTime endDate);

  void deleteById(Long id);
}
