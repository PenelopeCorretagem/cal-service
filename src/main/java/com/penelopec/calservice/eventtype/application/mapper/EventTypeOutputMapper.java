package com.penelopec.calservice.eventtype.application.mapper;

import com.penelopec.calservice.eventtype.application.output.EventTypeOutput;
import com.penelopec.calservice.eventtype.domain.entity.EventType;

public class EventTypeOutputMapper {

  private EventTypeOutputMapper() {
  }

  public static EventTypeOutput toOutput(EventType eventType) {
    return new EventTypeOutput(
      eventType.getId(),
      eventType.getTitle(),
      eventType.getSlugValue(),
      eventType.getDescription(),
      eventType.getLengthInMinutes(),
      eventType.getMinimumBookingNotice(),
      eventType.isHidden(),
      eventType.getEstateId()
    );
  }
}
