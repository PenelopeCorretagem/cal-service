package com.penelopec.calservice.eventtype.application.service;

import com.penelopec.calservice.eventtype.application.command.UpdateEventTypeCommand;
import com.penelopec.calservice.eventtype.application.mapper.EventTypeOutputMapper;
import com.penelopec.calservice.eventtype.application.output.EventTypeOutput;
import com.penelopec.calservice.eventtype.application.port.in.ChangeEventTypeUseCase;
import com.penelopec.calservice.eventtype.domain.entity.EventType;
import com.penelopec.calservice.eventtype.domain.error.EventTypeError;
import com.penelopec.calservice.eventtype.domain.gateway.CalComEventTypeGateway;
import com.penelopec.calservice.shared.cache.CacheNames;
import com.penelopec.calservice.shared.error.core.DomainException;
import com.penelopec.calservice.eventtype.domain.repository.EventTypeRepository;
import com.penelopec.calservice.shared.validation.CommandValidator;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;

public class ChangeEventTypeService implements ChangeEventTypeUseCase {

  private final CalComEventTypeGateway calComGateway;
  private final EventTypeRepository eventTypeRepository;
  private final CommandValidator<UpdateEventTypeCommand> validator;

  public ChangeEventTypeService(CalComEventTypeGateway calComGateway,
                                EventTypeRepository eventTypeRepository,
                                CommandValidator<UpdateEventTypeCommand> validator) {
    this.calComGateway = calComGateway;
    this.eventTypeRepository = eventTypeRepository;
    this.validator = validator;
  }

  @Override
    @Caching(evict = {
      @CacheEvict(value = CacheNames.EVENT_TYPES, allEntries = true),
      @CacheEvict(value = CacheNames.AVAILABLE_SLOTS, allEntries = true)
    }, put = {
      @CachePut(value = CacheNames.EVENT_TYPE, key = "#command.eventTypeId")
    })
  public EventTypeOutput execute(UpdateEventTypeCommand command) {
    validator.validateAndThrow(command);

    EventType existing = eventTypeRepository.findById(command.eventTypeId())
      .orElseThrow(() -> new DomainException(EventTypeError.NOT_FOUND, command.eventTypeId()));

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
