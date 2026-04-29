package com.penelopec.calservice.eventtype.application.port.in;

import com.penelopec.calservice.eventtype.application.output.EventTypeOutput;

public interface GetEventTypeUseCase {
  EventTypeOutput execute(Long eventTypeId);
}
