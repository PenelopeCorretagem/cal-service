package com.penelopec.calservice.eventtype.infrastructure.controller;

import com.penelopec.calservice.eventtype.application.command.CreateEventTypeCommand;
import com.penelopec.calservice.eventtype.application.command.UpdateEventTypeCommand;
import com.penelopec.calservice.eventtype.application.output.EventTypeOutput;
import com.penelopec.calservice.eventtype.application.output.Page;
import com.penelopec.calservice.eventtype.application.port.in.*;
import com.penelopec.calservice.eventtype.infrastructure.controller.doc.EventTypeControllerSwagger;
import com.penelopec.calservice.eventtype.infrastructure.controller.dto.CreateEventTypeRequest;
import com.penelopec.calservice.eventtype.infrastructure.controller.dto.UpdateEventTypeRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/event-types")
public class EventTypeController implements EventTypeControllerSwagger {

  private final CreateEventTypeUseCase createEventTypeUseCase;
  private final GetEventTypeUseCase getEventTypeUseCase;
  private final ListEventTypesUseCase listEventTypesUseCase;
  private final ChangeEventTypeUseCase updateEventTypeUseCase;
  private final DeleteEventTypeUseCase deleteEventTypeUseCase;
  private final ToggleEventTypeVisibilityUseCase toggleEventTypeVisibilityUseCase;

  public EventTypeController(CreateEventTypeUseCase createEventTypeUseCase,
                             GetEventTypeUseCase getEventTypeUseCase,
                             ListEventTypesUseCase listEventTypesUseCase,
                             ChangeEventTypeUseCase updateEventTypeUseCase,
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
  public ResponseEntity<Page<EventTypeOutput>> listAll(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "20") int size
  ) {
    Page<EventTypeOutput> output = listEventTypesUseCase.execute(page, size);
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
