package com.penelopec.calservice.eventtype.application.service;

import com.penelopec.calservice.eventtype.application.mapper.EventTypeOutputMapper;
import com.penelopec.calservice.eventtype.application.output.EventTypeOutput;
import com.penelopec.calservice.eventtype.application.port.in.ListEventTypesUseCase;
import com.penelopec.calservice.eventtype.domain.gateway.CalComEventTypeGateway;
import com.penelopec.calservice.shared.pagination.Page;

import java.util.List;

public class ListEventTypesService implements ListEventTypesUseCase {

    private final CalComEventTypeGateway calComGateway;
    public ListEventTypesService(CalComEventTypeGateway calComGateway) {
        this.calComGateway = calComGateway;
    }

    @Override
    public Page<EventTypeOutput> execute(int page, int size) {
        List<EventTypeOutput> outputs = calComGateway.listAll().stream()
                .map(EventTypeOutputMapper::toOutput)
                .toList();

        return Page.from(outputs, page, size);
    }

}