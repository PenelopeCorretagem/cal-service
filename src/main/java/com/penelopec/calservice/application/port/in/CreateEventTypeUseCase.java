package com.penelopec.calservice.application.port.in;

import com.penelopec.calservice.application.command.CreateEventTypeCommand;
import com.penelopec.calservice.application.output.EventTypeOutput;

public interface CreateEventTypeUseCase {
    EventTypeOutput execute(CreateEventTypeCommand command);
}
