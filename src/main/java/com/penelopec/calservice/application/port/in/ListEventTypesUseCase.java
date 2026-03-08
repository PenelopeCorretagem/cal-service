package com.penelopec.calservice.application.port.in;

import com.penelopec.calservice.application.output.EventTypeOutput;

import java.util.List;

public interface ListEventTypesUseCase {
    List<EventTypeOutput> execute();
}
