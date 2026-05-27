package com.penelopec.calservice.eventtype.application.service;

import com.penelopec.calservice.eventtype.application.mapper.EventTypeOutputMapper;
import com.penelopec.calservice.eventtype.application.output.EventTypeOutput;
import com.penelopec.calservice.eventtype.application.port.in.ListEventTypesUseCase;
import com.penelopec.calservice.eventtype.domain.entity.EventType;
import com.penelopec.calservice.eventtype.domain.gateway.CalComEventTypeGateway;
import com.penelopec.calservice.eventtype.domain.repository.EventTypeRepository;
import com.penelopec.calservice.shared.pagination.Page;

import java.util.List;

public class ListEventTypesService implements ListEventTypesUseCase {

    private final CalComEventTypeGateway calComGateway;
    private final EventTypeRepository eventTypeRepository;

    public ListEventTypesService(CalComEventTypeGateway calComGateway,
                                 EventTypeRepository eventTypeRepository) {
        this.calComGateway = calComGateway;
        this.eventTypeRepository = eventTypeRepository;
    }

    @Override
    public Page<EventTypeOutput> execute(int page, int size) {
        List<EventTypeOutput> outputs = calComGateway.listAll().stream()
                .map(this::enrichEventTypeWithEstateId)
                .map(EventTypeOutputMapper::toOutput)
                .toList();

        return Page.from(outputs, page, size);
    }

    private EventType enrichEventTypeWithEstateId(EventType eventType) {
        if (eventType.getEstateId() == null) {
            return eventTypeRepository.findById(eventType.getId())
                    .map(local -> EventType.reconstitute(
                            eventType.getId(),
                            eventType.getTitle(),
                            eventType.getSlugValue(),
                            eventType.getDescription(),
                            eventType.getLengthInMinutes(),
                            eventType.getMinimumBookingNotice(),
                            eventType.isHidden(),
                            local.getEstateId()))
                    .orElse(eventType);
        }
        return eventType;
    }
}