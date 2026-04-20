package com.penelopec.calservice.eventtype.application.port.in;

import com.penelopec.calservice.eventtype.application.output.EventTypeOutput;
import com.penelopec.calservice.eventtype.application.output.Page;

public interface ListEventTypesUseCase {
  Page<EventTypeOutput> execute(int page, int size);
}
