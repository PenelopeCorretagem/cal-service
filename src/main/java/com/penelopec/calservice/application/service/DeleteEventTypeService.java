package com.penelopec.calservice.application.service;

import com.penelopec.calservice.application.port.in.DeleteEventTypeUseCase;
import com.penelopec.calservice.domain.gateway.CalComEventTypeGateway;
import com.penelopec.calservice.domain.repository.EventTypeRepository;

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
