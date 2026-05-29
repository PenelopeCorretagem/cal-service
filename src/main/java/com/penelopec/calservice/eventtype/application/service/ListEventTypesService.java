package com.penelopec.calservice.eventtype.application.service;

import com.penelopec.calservice.eventtype.application.mapper.EventTypeOutputMapper;
import com.penelopec.calservice.eventtype.application.output.EventTypeOutput;
import com.penelopec.calservice.eventtype.application.port.in.ListEventTypesUseCase;
import com.penelopec.calservice.eventtype.domain.entity.EventType;
import com.penelopec.calservice.eventtype.domain.gateway.CalComEventTypeGateway;
import com.penelopec.calservice.eventtype.domain.repository.EventTypeRepository;
import com.penelopec.calservice.shared.cache.CacheNames;
import com.penelopec.calservice.shared.pagination.Page;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Map;

public class ListEventTypesService implements ListEventTypesUseCase {

    private final CalComEventTypeGateway calComGateway;
    private final EventTypeRepository eventTypeRepository;

    public ListEventTypesService(CalComEventTypeGateway calComGateway,
                                 EventTypeRepository eventTypeRepository) {
        this.calComGateway = calComGateway;
        this.eventTypeRepository = eventTypeRepository;
    }

    @Override
  @Cacheable(value = CacheNames.EVENT_TYPES, key = "#page + ':' + #size")
  public Page<EventTypeOutput> execute(int page, int size) {
    List<EventType> allEventTypes = calComGateway.listAll();
    
    // Preload all local EventTypes once to avoid N+1 queries
    List<EventType> localEvents = eventTypeRepository.findAll();
    Map<Long, EventType> localById = localEvents.stream()
      .collect(java.util.stream.Collectors.toMap(EventType::getId, e -> e));
    
    List<EventTypeOutput> outputs = allEventTypes.stream()
      .map(eventType -> enrichEventTypeWithEstateId(eventType, localById))
      .map(EventTypeOutputMapper::toOutput)
      .toList();
    return Page.from(outputs, page, size);
  }

  private EventType enrichEventTypeWithEstateId(EventType eventType, Map<Long, EventType> localById) {
    if (eventType.getEstateId() == null && localById.containsKey(eventType.getId())) {
      EventType local = localById.get(eventType.getId());
      return EventType.reconstitute(
        eventType.getId(),
        eventType.getTitle(),
        eventType.getSlugValue(),
        eventType.getDescription(),
        eventType.getLengthInMinutes(),
        eventType.getMinimumBookingNotice(),
        eventType.isHidden(),
        local.getEstateId());
    }
    return eventType;
  }

}