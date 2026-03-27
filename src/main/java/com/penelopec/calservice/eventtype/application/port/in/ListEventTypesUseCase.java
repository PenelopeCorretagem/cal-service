package com.penelopec.calservice.eventtype.application.port.in;

import com.penelopec.calservice.eventtype.application.output.EventTypeOutput;

import java.util.List;

public interface ListEventTypesUseCase {
  List<EventTypeOutput> execute();
}
