package com.penelopec.calservice.eventtype.application.service;

import com.penelopec.calservice.eventtype.application.port.in.DeleteEventTypeUseCase;
import com.penelopec.calservice.eventtype.domain.gateway.CalComEventTypeGateway;
import com.penelopec.calservice.eventtype.domain.repository.EventTypeRepository;

public class DeleteEventTypeService implements DeleteEventTypeUseCase {

  private final CalComEventTypeGateway calComGateway;
  private final EventTypeRepository eventTypeRepository;

  public DeleteEventTypeService(CalComEventTypeGateway calComGateway,
                                EventTypeRepository eventTypeRepository) {
    this.calComGateway = calComGateway;
    this.eventTypeRepository = eventTypeRepository;
  }

  @Override
  public void execute(Long eventTypeId) {
    if (eventTypeId == null) {
      throw new IllegalArgumentException("ID do EventType é obrigatório");
    }

    calComGateway.delete(eventTypeId);
    eventTypeRepository.deleteById(eventTypeId);
  }
}
