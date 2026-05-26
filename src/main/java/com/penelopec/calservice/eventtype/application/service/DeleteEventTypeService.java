package com.penelopec.calservice.eventtype.application.service;

import com.penelopec.calservice.eventtype.application.port.in.DeleteEventTypeUseCase;
import com.penelopec.calservice.eventtype.domain.gateway.CalComEventTypeGateway;
import com.penelopec.calservice.eventtype.domain.repository.EventTypeRepository;
import com.penelopec.calservice.shared.cache.CacheNames;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;

public class DeleteEventTypeService implements DeleteEventTypeUseCase {

  private final CalComEventTypeGateway calComGateway;
  private final EventTypeRepository eventTypeRepository;

  public DeleteEventTypeService(CalComEventTypeGateway calComGateway,
                                EventTypeRepository eventTypeRepository) {
    this.calComGateway = calComGateway;
    this.eventTypeRepository = eventTypeRepository;
  }

  @Override
  @Caching(evict = {
      @CacheEvict(value = CacheNames.EVENT_TYPE, key = "#eventTypeId"),
      @CacheEvict(value = CacheNames.EVENT_TYPES, allEntries = true)
  })
  public void execute(Long eventTypeId) {
    if (eventTypeId == null) {
      throw new IllegalArgumentException("ID do EventType é obrigatório");
    }

    calComGateway.delete(eventTypeId);
    eventTypeRepository.deleteById(eventTypeId);
  }
}
