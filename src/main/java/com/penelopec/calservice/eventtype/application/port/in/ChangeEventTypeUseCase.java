package com.penelopec.calservice.eventtype.application.port.in;

import com.penelopec.calservice.eventtype.application.command.UpdateEventTypeCommand;
import com.penelopec.calservice.eventtype.application.output.EventTypeOutput;

public interface ChangeEventTypeUseCase {
  EventTypeOutput execute(UpdateEventTypeCommand command);
}
