package com.penelopec.calservice.eventtype.application.service;

import com.penelopec.calservice.eventtype.application.command.CreateEventTypeCommand;
import com.penelopec.calservice.eventtype.application.command.UpdateEventTypeCommand;
import com.penelopec.calservice.eventtype.application.port.in.ChangeEventTypeUseCase;
import com.penelopec.calservice.eventtype.application.port.in.CreateEventTypeUseCase;
import com.penelopec.calservice.eventtype.application.port.in.SyncEventTypesUseCase;
import com.penelopec.calservice.eventtype.domain.entity.EventType;
import com.penelopec.calservice.eventtype.domain.error.EventTypeError;
import com.penelopec.calservice.eventtype.domain.gateway.CalComEventTypeGateway;
import com.penelopec.calservice.eventtype.domain.gateway.AdvertisementGateway;
import com.penelopec.calservice.eventtype.domain.repository.EventTypeRepository;
import com.penelopec.calservice.eventtype.infrastructure.web.monolith.dto.AdvertisementResponse;
import com.penelopec.calservice.eventtype.infrastructure.web.monolith.dto.EstateResponse;
import com.penelopec.calservice.shared.error.core.DomainException;
import com.penelopec.calservice.shared.error.core.GatewayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SyncEventTypesService implements SyncEventTypesUseCase {

  private static final Logger log = LoggerFactory.getLogger(SyncEventTypesService.class);

  private final AdvertisementGateway advertisementGateway;
  private final EventTypeRepository eventTypeRepository;
  private final CreateEventTypeUseCase  createEventTypeUseCase;
  private final ChangeEventTypeUseCase changeEventTypeUseCase;

  public SyncEventTypesService(
    AdvertisementGateway advertisementGateway,
    EventTypeRepository eventTypeRepository,
    CreateEventTypeUseCase createEventTypeUseCase,
    ChangeEventTypeUseCase changeEventTypeUseCase) {

    this.advertisementGateway = advertisementGateway;
    this.eventTypeRepository = eventTypeRepository;
    this.createEventTypeUseCase = createEventTypeUseCase;
    this.changeEventTypeUseCase = changeEventTypeUseCase;
  }

  @Override
  public void execute() {
    try {
      advertisementGateway.fetchAllAdvertisements().forEach(
        advertisement -> {
          EstateResponse estate = advertisement.estate();

          EventType eventType = eventTypeRepository.findByEstateId(estate.id()).orElse(null);

          if (eventType == null) {
            createEventType(advertisement);
            return;
          }

          updateEventTypeIfNeeded(advertisement, eventType);
        }
      );
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
      throw new GatewayException(EventTypeError.SYNC_FAILED, ex.getMessage());
    }
  }

  private void createEventType(AdvertisementResponse advertisement) {
    EstateResponse estate = advertisement.estate();

    CreateEventTypeCommand command = new CreateEventTypeCommand(
      estate.title(),
      estate.description(),
      60,
      60,
      advertisement.active(),
      estate.id()
    );

    createEventTypeUseCase.execute(command);
  }

  private void updateEventTypeIfNeeded(AdvertisementResponse advertisement, EventType existing) {
    if (
      advertisement.active() == existing.isHidden() &&
      advertisement.estate().title().equals(existing.getTitle()) &&
      advertisement.estate().description().equals(existing.getDescription())
    ) return;

    UpdateEventTypeCommand command = new UpdateEventTypeCommand(
      existing.getId(),
      advertisement.estate().title(),
      advertisement.estate().description(),
      60,
      60
    );

    changeEventTypeUseCase.execute(command);
  }
}
