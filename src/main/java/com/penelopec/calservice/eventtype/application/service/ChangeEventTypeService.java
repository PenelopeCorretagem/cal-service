package com.penelopec.calservice.eventtype.application.service;

import com.penelopec.calservice.eventtype.application.command.UpdateEventTypeCommand;
import com.penelopec.calservice.eventtype.application.mapper.EventTypeOutputMapper;
import com.penelopec.calservice.eventtype.application.output.EventTypeOutput;
import com.penelopec.calservice.eventtype.application.port.in.ChangeEventTypeUseCase;
import com.penelopec.calservice.eventtype.domain.entity.EventType;
import com.penelopec.calservice.eventtype.domain.exception.EventTypeNotFoundException;
import com.penelopec.calservice.eventtype.domain.gateway.CalComEventTypeGateway;
import com.penelopec.calservice.eventtype.domain.repository.EventTypeRepository;

public class ChangeEventTypeService implements ChangeEventTypeUseCase {

  private final CalComEventTypeGateway calComGateway;
  private final EventTypeRepository eventTypeRepository;

  public ChangeEventTypeService(CalComEventTypeGateway calComGateway,
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
