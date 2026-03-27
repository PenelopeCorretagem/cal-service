package com.penelopec.calservice.appointment.infrastructure.persistence.repository;

import com.penelopec.calservice.appointment.infrastructure.persistence.entity.AppointmentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentJpaRepository extends JpaRepository<AppointmentJpaEntity, Long> {

  Optional<AppointmentJpaEntity> findByBookingUid(String bookingUid);

  List<AppointmentJpaEntity> findByClientId(Long clientId);

  List<AppointmentJpaEntity> findByEstateAgentId(Long estateAgentId);

  List<AppointmentJpaEntity> findByEstateId(Long estateId);

  List<AppointmentJpaEntity> findByStatus(String status);

  List<AppointmentJpaEntity> findByStartDateTimeBetween(LocalDateTime start, LocalDateTime end);
}
