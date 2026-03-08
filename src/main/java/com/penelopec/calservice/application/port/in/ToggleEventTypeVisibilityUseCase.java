package com.penelopec.calservice.application.port.in;

import com.penelopec.calservice.application.output.EventTypeOutput;

public interface ToggleEventTypeVisibilityUseCase {
    EventTypeOutput execute(Long eventTypeId);
}
