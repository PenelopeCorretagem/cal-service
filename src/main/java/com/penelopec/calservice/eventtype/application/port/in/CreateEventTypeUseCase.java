package com.penelopec.calservice.eventtype.application.port.in;

import com.penelopec.calservice.eventtype.application.command.CreateEventTypeCommand;
import com.penelopec.calservice.eventtype.application.output.EventTypeOutput;

public interface CreateEventTypeUseCase {
  EventTypeOutput execute(CreateEventTypeCommand command);
}
