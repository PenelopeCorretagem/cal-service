package com.penelopec.calservice.appointment.infrastructure.persistence.repository;

import com.penelopec.calservice.appointment.infrastructure.persistence.entity.AppointmentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface AppointmentJpaRepository extends JpaRepository<AppointmentJpaEntity, Long>,
    JpaSpecificationExecutor<AppointmentJpaEntity> {

  Optional<AppointmentJpaEntity> findByBookingUid(String bookingUid);
}
