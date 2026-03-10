package com.penelopec.calservice.application.service;

import com.penelopec.calservice.application.command.UpdateEventTypeCommand;
import com.penelopec.calservice.application.mapper.EventTypeOutputMapper;
import com.penelopec.calservice.application.output.EventTypeOutput;
import com.penelopec.calservice.application.port.in.UpdateEventTypeUseCase;
import com.penelopec.calservice.domain.entity.EventType;
import com.penelopec.calservice.domain.exception.EventTypeNotFoundException;
import com.penelopec.calservice.domain.gateway.CalComEventTypeGateway;
import com.penelopec.calservice.domain.repository.EventTypeRepository;

public class UpdateEventTypeService implements UpdateEventTypeUseCase {

    private final CalComEventTypeGateway calComGateway;
    private final EventTypeRepository eventTypeRepository;

    public UpdateEventTypeService(CalComEventTypeGateway calComGateway,
                                  EventTypeRepository eventTypeRepository) {
        this.calComGateway = calComGateway;
        this.eventTypeRepository = eventTypeRepository;
    }

    @Override
    public EventTypeOutput execute(UpdateEventTypeCommand command) {
        EventType existing = eventTypeRepository.findById(command.eventTypeId())
                .orElseThrow(() -> new EventTypeNotFoundException(
                        "EventType não encontrado: " + command.eventTypeId()));

        if (command.title() != null) {
            existing.updateTitle(command.title());
        }
        if (command.description() != null) {
            existing.updateDescription(command.description());
        }
        if (command.lengthInMinutes() != null) {
            existing.updateLengthInMinutes(command.lengthInMinutes());
        }
        if (command.minimumBookingNotice() != null) {
            existing.updateMinimumBookingNotice(command.minimumBookingNotice());
        }

        calComGateway.update(existing, existing.isHidden());

        EventType persisted = eventTypeRepository.save(existing);

        return EventTypeOutputMapper.toOutput(persisted);
    }
}
