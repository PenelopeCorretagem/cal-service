package com.penelopec.calservice.infrastructure.controller;

import com.penelopec.calservice.application.command.CreateEventTypeCommand;
import com.penelopec.calservice.application.command.UpdateEventTypeCommand;
import com.penelopec.calservice.application.output.EventTypeOutput;
import com.penelopec.calservice.application.port.in.CreateEventTypeUseCase;
import com.penelopec.calservice.application.port.in.DeleteEventTypeUseCase;
import com.penelopec.calservice.application.port.in.GetEventTypeUseCase;
import com.penelopec.calservice.application.port.in.ListEventTypesUseCase;
import com.penelopec.calservice.application.port.in.ToggleEventTypeVisibilityUseCase;
import com.penelopec.calservice.application.port.in.UpdateEventTypeUseCase;
import com.penelopec.calservice.infrastructure.controller.dto.CreateEventTypeRequest;
import com.penelopec.calservice.infrastructure.controller.dto.UpdateEventTypeRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/event-types")
public class EventTypeController implements EventTypeControllerSwagger {

    private final CreateEventTypeUseCase createEventTypeUseCase;
    private final GetEventTypeUseCase getEventTypeUseCase;
    private final ListEventTypesUseCase listEventTypesUseCase;
    private final UpdateEventTypeUseCase updateEventTypeUseCase;
    private final DeleteEventTypeUseCase deleteEventTypeUseCase;
    private final ToggleEventTypeVisibilityUseCase toggleEventTypeVisibilityUseCase;

    public EventTypeController(CreateEventTypeUseCase createEventTypeUseCase,
                               GetEventTypeUseCase getEventTypeUseCase,
                               ListEventTypesUseCase listEventTypesUseCase,
                               UpdateEventTypeUseCase updateEventTypeUseCase,
                               DeleteEventTypeUseCase deleteEventTypeUseCase,
                               ToggleEventTypeVisibilityUseCase toggleEventTypeVisibilityUseCase) {
        this.createEventTypeUseCase = createEventTypeUseCase;
        this.getEventTypeUseCase = getEventTypeUseCase;
        this.listEventTypesUseCase = listEventTypesUseCase;
        this.updateEventTypeUseCase = updateEventTypeUseCase;
        this.deleteEventTypeUseCase = deleteEventTypeUseCase;
        this.toggleEventTypeVisibilityUseCase = toggleEventTypeVisibilityUseCase;
    }

    @Override
    @PostMapping
    public ResponseEntity<EventTypeOutput> create(@Valid @RequestBody CreateEventTypeRequest request) {
        var command = new CreateEventTypeCommand(
                request.title(),
                request.description(),
                request.lengthInMinutes(),
                request.minimumBookingNotice(),
                request.hidden(),
                request.estateId()
        );

        EventTypeOutput output = createEventTypeUseCase.execute(command);

        URI location = URI.create("/event-types/" + output.id());
        return ResponseEntity.created(location).body(output);
    }

    @Override
    @GetMapping("/{eventTypeId}")
    public ResponseEntity<EventTypeOutput> getById(@PathVariable Long eventTypeId) {
        EventTypeOutput output = getEventTypeUseCase.execute(eventTypeId);
        return ResponseEntity.ok(output);
    }

    @Override
    @GetMapping
    public ResponseEntity<List<EventTypeOutput>> listAll() {
        List<EventTypeOutput> output = listEventTypesUseCase.execute();
        return ResponseEntity.ok(output);
    }

    @Override
    @PatchMapping("/{eventTypeId}")
    public ResponseEntity<EventTypeOutput> update(
            @PathVariable Long eventTypeId,
            @Valid @RequestBody UpdateEventTypeRequest request
    ) {
        var command = new UpdateEventTypeCommand(
                eventTypeId,
                request.title(),
                request.description(),
                request.lengthInMinutes(),
                request.minimumBookingNotice()
        );

        EventTypeOutput output = updateEventTypeUseCase.execute(command);
        return ResponseEntity.ok(output);
    }

    @Override
    @DeleteMapping("/{eventTypeId}")
    public ResponseEntity<Void> delete(@PathVariable Long eventTypeId) {
        deleteEventTypeUseCase.execute(eventTypeId);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PatchMapping("/{eventTypeId}/toggle-visibility")
    public ResponseEntity<EventTypeOutput> toggleVisibility(@PathVariable Long eventTypeId) {
        EventTypeOutput output = toggleEventTypeVisibilityUseCase.execute(eventTypeId);
        return ResponseEntity.ok(output);
    }
}
