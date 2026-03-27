package com.penelopec.calservice.eventtype.infrastructure.persistence.repository;

import com.penelopec.calservice.eventtype.infrastructure.persistence.entity.EventTypeJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventTypeJpaRepository extends JpaRepository<EventTypeJpaEntity, Long> {

  Optional<EventTypeJpaEntity> findByEstateId(Long estateId);
}
