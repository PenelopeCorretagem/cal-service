package com.penelopec.calservice.eventtype.domain.repository;

import com.penelopec.calservice.eventtype.domain.entity.EventType;

import java.util.List;
import java.util.Optional;

public interface EventTypeRepository {

  EventType save(EventType eventType);

  Optional<EventType> findById(Long id);

  Optional<EventType> findByEstateId(Long estateId);

  List<EventType> findAll();

  void deleteById(Long id);
}
