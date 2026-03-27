package com.penelopec.calservice.eventtype.domain.gateway;

import com.penelopec.calservice.eventtype.domain.entity.EventType;

import java.util.List;
import java.util.Optional;

public interface CalComEventTypeGateway {

  EventType create(EventType eventType, boolean hidden);

  EventType update(EventType eventType, boolean hidden);

  void delete(Long eventTypeId);

  Optional<EventType> findById(Long eventTypeId);

  List<EventType> listAll();
}
