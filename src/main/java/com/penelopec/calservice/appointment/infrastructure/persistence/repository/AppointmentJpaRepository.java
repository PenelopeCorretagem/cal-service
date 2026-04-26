package com.penelopec.calservice.appointment.infrastructure.persistence.repository;

import com.penelopec.calservice.appointment.infrastructure.persistence.entity.AppointmentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

public interface AppointmentJpaRepository extends JpaRepository<AppointmentJpaEntity, Long>,
    JpaSpecificationExecutor<AppointmentJpaEntity> {

  Optional<AppointmentJpaEntity> findByBookingUid(String bookingUid);

  boolean existsByEstateAgentIdAndStartDateTimeAndStatusNotIn(Long estateAgentId,
                                                               LocalDateTime startDateTime,
                                                               Collection<String> statuses);

  boolean existsByEstateAgentIdAndStartDateTimeAndStatusNotInAndIdNot(Long estateAgentId,
                                                                       LocalDateTime startDateTime,
                                                                       Collection<String> statuses,
                                                                       Long id);
}
