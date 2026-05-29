package com.penelopec.calservice.eventtype.application.service;

import com.penelopec.calservice.eventtype.application.mapper.EventTypeOutputMapper;
import com.penelopec.calservice.eventtype.application.output.EventTypeOutput;
import com.penelopec.calservice.eventtype.application.port.in.ToggleEventTypeVisibilityUseCase;
import com.penelopec.calservice.eventtype.domain.entity.EventType;
import com.penelopec.calservice.eventtype.domain.error.EventTypeError;
import com.penelopec.calservice.eventtype.domain.gateway.CalComEventTypeGateway;
import com.penelopec.calservice.shared.cache.CacheNames;
import com.penelopec.calservice.shared.error.core.DomainException;
import com.penelopec.calservice.eventtype.domain.repository.EventTypeRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;

public class ToggleEventTypeVisibilityService implements ToggleEventTypeVisibilityUseCase {

  private final CalComEventTypeGateway calComGateway;
  private final EventTypeRepository eventTypeRepository;

  public ToggleEventTypeVisibilityService(CalComEventTypeGateway calComGateway,
                                          EventTypeRepository eventTypeRepository) {
    this.calComGateway = calComGateway;
    this.eventTypeRepository = eventTypeRepository;
  }

  @Override
    @Caching(evict = {
      @CacheEvict(value = CacheNames.EVENT_TYPES, allEntries = true),
      @CacheEvict(value = CacheNames.AVAILABLE_SLOTS, allEntries = true)
    }, put = {
      @CachePut(value = CacheNames.EVENT_TYPE, key = "#eventTypeId")
    })
  public EventTypeOutput execute(Long eventTypeId) {
    EventType eventType = eventTypeRepository.findById(eventTypeId)
      .orElseThrow(() -> new DomainException(EventTypeError.NOT_FOUND, eventTypeId));

    eventType.toggleHidden();

    calComGateway.update(eventType, eventType.isHidden());

    EventType persisted = eventTypeRepository.save(eventType);

    return EventTypeOutputMapper.toOutput(persisted);
  }
}
