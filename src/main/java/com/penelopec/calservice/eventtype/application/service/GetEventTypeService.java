package com.penelopec.calservice.eventtype.application.service;

import com.penelopec.calservice.eventtype.application.mapper.EventTypeOutputMapper;
import com.penelopec.calservice.eventtype.application.output.EventTypeOutput;
import com.penelopec.calservice.eventtype.application.port.in.GetEventTypeUseCase;
import com.penelopec.calservice.eventtype.domain.entity.EventType;
import com.penelopec.calservice.eventtype.domain.error.EventTypeError;
import com.penelopec.calservice.eventtype.domain.gateway.CalComEventTypeGateway;
import com.penelopec.calservice.shared.error.core.DomainException;
import com.penelopec.calservice.eventtype.domain.repository.EventTypeRepository;

public class GetEventTypeService implements GetEventTypeUseCase {

  private final CalComEventTypeGateway calComGateway;
  private final EventTypeRepository eventTypeRepository;

  public GetEventTypeService(CalComEventTypeGateway calComGateway,
                             EventTypeRepository eventTypeRepository) {
    this.calComGateway = calComGateway;
    this.eventTypeRepository = eventTypeRepository;
  }

  @Override
  public EventTypeOutput execute(Long eventTypeId) {
    EventType eventType = calComGateway.findById(eventTypeId)
      .orElseThrow(() -> new DomainException(EventTypeError.NOT_FOUND, eventTypeId));

    if (eventType.getEstateId() == null) {
      EventType finalEventType = eventType;
      eventType = eventTypeRepository.findById(eventTypeId)
        .map(local -> EventType.reconstitute(
          finalEventType.getId(),
          finalEventType.getTitle(),
          finalEventType.getSlugValue(),
          finalEventType.getDescription(),
          finalEventType.getLengthInMinutes(),
          finalEventType.getMinimumBookingNotice(),
          finalEventType.isHidden(),
          local.getEstateId()))
        .orElse(eventType);
    }

    return EventTypeOutputMapper.toOutput(eventType);
  }
}
