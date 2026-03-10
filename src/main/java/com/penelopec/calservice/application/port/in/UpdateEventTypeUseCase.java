package com.penelopec.calservice.application.port.in;

import com.penelopec.calservice.application.command.UpdateEventTypeCommand;
import com.penelopec.calservice.application.output.EventTypeOutput;

public interface UpdateEventTypeUseCase {
    EventTypeOutput execute(UpdateEventTypeCommand command);
}
