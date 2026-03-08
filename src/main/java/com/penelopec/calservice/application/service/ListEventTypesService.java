package com.penelopec.calservice.application.service;

import com.penelopec.calservice.application.mapper.EventTypeOutputMapper;
import com.penelopec.calservice.application.output.EventTypeOutput;
import com.penelopec.calservice.application.port.in.ListEventTypesUseCase;
import com.penelopec.calservice.domain.gateway.CalComEventTypeGateway;

import java.util.List;

public class ListEventTypesService implements ListEventTypesUseCase {

    private final CalComEventTypeGateway calComGateway;

    public ListEventTypesService(CalComEventTypeGateway calComGateway) {
        this.calComGateway = calComGateway;
    }

    @Override
    public List<EventTypeOutput> execute() {
        return calComGateway.listAll().stream()
                .map(EventTypeOutputMapper::toOutput)
                .toList();
    }
}
