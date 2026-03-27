package com.penelopec.calservice.eventtype.application.service;

import com.penelopec.calservice.eventtype.application.mapper.EventTypeOutputMapper;
import com.penelopec.calservice.eventtype.application.output.EventTypeOutput;
import com.penelopec.calservice.eventtype.application.port.in.ToggleEventTypeVisibilityUseCase;
import com.penelopec.calservice.eventtype.domain.entity.EventType;
import com.penelopec.calservice.eventtype.domain.exception.EventTypeNotFoundException;
import com.penelopec.calservice.eventtype.domain.gateway.CalComEventTypeGateway;
import com.penelopec.calservice.eventtype.domain.repository.EventTypeRepository;

public class ToggleEventTypeVisibilityService implements ToggleEventTypeVisibilityUseCase {

  private final CalComEventTypeGateway calComGateway;
  private final EventTypeRepository eventTypeRepository;

  public ToggleEventTypeVisibilityService(CalComEventTypeGateway calComGateway,
                                          EventTypeRepository eventTypeRepository) {
    this.calComGateway = calComGateway;
    this.eventTypeRepository = eventTypeRepository;
  }

  @Override
  public EventTypeOutput execute(Long eventTypeId) {
    EventType eventType = eventTypeRepository.findById(eventTypeId)
      .orElseThrow(() -> new EventTypeNotFoundException(
        "EventType não encontrado: " + eventTypeId));

    eventType.toggleHidden();

    calComGateway.update(eventType, eventType.isHidden());

    EventType persisted = eventTypeRepository.save(eventType);

    return EventTypeOutputMapper.toOutput(persisted);
  }
}
