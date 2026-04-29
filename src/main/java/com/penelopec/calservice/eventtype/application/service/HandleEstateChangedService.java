package com.penelopec.calservice.eventtype.application.service;

import com.penelopec.calservice.eventtype.application.command.HandleEstateChangedCommand;
import com.penelopec.calservice.eventtype.application.port.in.HandleEstateChangedUseCase;
import com.penelopec.calservice.eventtype.domain.entity.EventType;
import com.penelopec.calservice.eventtype.domain.gateway.CalComEventTypeGateway;
import com.penelopec.calservice.eventtype.domain.repository.EventTypeRepository;
import com.penelopec.calservice.shared.validation.CommandValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class HandleEstateChangedService implements HandleEstateChangedUseCase {

  private static final Logger log = LoggerFactory.getLogger(HandleEstateChangedService.class);

  private final EventTypeRepository repository;
  private final CalComEventTypeGateway calComGateway;
  private final CommandValidator<HandleEstateChangedCommand> validator;

  public HandleEstateChangedService(EventTypeRepository repository,
                                    CalComEventTypeGateway calComGateway,
                                    CommandValidator<HandleEstateChangedCommand> validator) {
    this.repository = repository;
    this.calComGateway = calComGateway;
    this.validator = validator;
  }

  @Override
  public void execute(HandleEstateChangedCommand command) {
    validator.validateAndThrow(command);

    Optional<EventType> optional = repository.findByEstateId(command.estateId());
    if (optional.isEmpty()) {
      log.warn("Nenhum EventType encontrado para estateId={}. Mensagem ignorada.", command.estateId());
      return;
    }

    EventType eventType = optional.get();

    if (eventType.isHidden() == command.hide()) {
      log.debug("EventType id={} já está no estado desejado (hidden={}). No-op.", eventType.getId(), command.hide());
      return;
    }

    if (command.hide()) {
      eventType.hide();
    } else {
      eventType.show();
    }

    repository.save(eventType);
    calComGateway.update(eventType, eventType.isHidden());
  }
}
