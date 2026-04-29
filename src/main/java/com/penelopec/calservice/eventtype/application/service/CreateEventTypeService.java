package com.penelopec.calservice.eventtype.application.service;

import com.penelopec.calservice.eventtype.application.command.CreateEventTypeCommand;
import com.penelopec.calservice.eventtype.application.mapper.EventTypeOutputMapper;
import com.penelopec.calservice.eventtype.application.output.EventTypeOutput;
import com.penelopec.calservice.eventtype.application.port.in.CreateEventTypeUseCase;
import com.penelopec.calservice.eventtype.domain.entity.EventType;
import com.penelopec.calservice.eventtype.domain.gateway.CalComEventTypeGateway;
import com.penelopec.calservice.eventtype.domain.repository.EventTypeRepository;
import com.penelopec.calservice.shared.validation.CommandValidator;

public class CreateEventTypeService implements CreateEventTypeUseCase {

  private final CalComEventTypeGateway calComGateway;
  private final EventTypeRepository eventTypeRepository;
  private final CommandValidator<CreateEventTypeCommand> validator;

  public CreateEventTypeService(CalComEventTypeGateway calComGateway,
                                EventTypeRepository eventTypeRepository,
                                CommandValidator<CreateEventTypeCommand> validator) {
    this.calComGateway = calComGateway;
    this.eventTypeRepository = eventTypeRepository;
    this.validator = validator;
  }

  @Override
  public EventTypeOutput execute(CreateEventTypeCommand command) {
    validator.validateAndThrow(command);

    EventType eventType = EventType.createNew(
      command.title(),
      command.description(),
      command.lengthInMinutes(),
      command.minimumBookingNotice(),
      command.hidden(),
      command.estateId()
    );

    EventType created = calComGateway.create(eventType, eventType.isHidden());

    EventType persisted = eventTypeRepository.save(created);

    return EventTypeOutputMapper.toOutput(persisted);
  }
}
