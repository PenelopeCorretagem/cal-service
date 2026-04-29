package com.penelopec.calservice.appointment.infrastructure.controller;

import com.penelopec.calservice.appointment.application.command.AppointmentCommand;
import com.penelopec.calservice.appointment.application.command.CancelAppointmentCommand;
import com.penelopec.calservice.appointment.application.command.ConcludeAppointmentCommand;
import com.penelopec.calservice.appointment.application.command.ConfirmAppointmentCommand;
import com.penelopec.calservice.appointment.application.command.RescheduleAppointmentCommand;
import com.penelopec.calservice.appointment.application.output.AppointmentOutput;
import com.penelopec.calservice.appointment.application.query.ListAppointmentsQuery;
import com.penelopec.calservice.appointment.application.usecase.*;
import com.penelopec.calservice.appointment.infrastructure.controller.dto.CancelAppointmentRequest;
import com.penelopec.calservice.appointment.infrastructure.controller.dto.CreateAppointmentRequest;
import com.penelopec.calservice.appointment.infrastructure.controller.dto.RescheduleAppointmentRequest;
import com.penelopec.calservice.shared.pagination.Page;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/appointments")
public class AppointmentController implements AppointmentControllerSwagger {

  private final CreateAppointmentUseCase createUseCase;
  private final GetAppointmentUseCase getUseCase;
  private final ListAppointmentsUseCase listUseCase;
  private final ChangeAppointmentUseCase rescheduleUseCase;
  private final CancelAppointmentUseCase cancelUseCase;
  private final ConfirmAppointmentUseCase confirmUseCase;
  private final ConcludeAppointmentUseCase concludeUseCase;
  private final DeleteAppointmentUseCase deleteUseCase;

  public AppointmentController(CreateAppointmentUseCase createUseCase,
                               GetAppointmentUseCase getUseCase,
                               ListAppointmentsUseCase listUseCase,
                               ChangeAppointmentUseCase rescheduleUseCase,
                               CancelAppointmentUseCase cancelUseCase,
                               ConfirmAppointmentUseCase confirmUseCase,
                               ConcludeAppointmentUseCase concludeUseCase,
                               DeleteAppointmentUseCase deleteUseCase) {
    this.createUseCase = createUseCase;
    this.getUseCase = getUseCase;
    this.listUseCase = listUseCase;
    this.rescheduleUseCase = rescheduleUseCase;
    this.cancelUseCase = cancelUseCase;
    this.confirmUseCase = confirmUseCase;
    this.concludeUseCase = concludeUseCase;
    this.deleteUseCase = deleteUseCase;
  }

  @Override
  @PostMapping
  public ResponseEntity<AppointmentOutput> create(@Valid @RequestBody CreateAppointmentRequest request) {
    var command = new AppointmentCommand(
      request.eventTypeId(),
      request.clientId(),
      request.estateAgentId(),
      request.startDateTime(),
      request.attendeeName(),
      request.attendeeEmail(),
      request.notes()
    );

    AppointmentOutput output = createUseCase.execute(command);

    URI location = URI.create("/appointments/" + output.id());
    return ResponseEntity.created(location).body(output);
  }

  @Override
  @GetMapping("/{id}")
  public ResponseEntity<AppointmentOutput> getById(@PathVariable Long id) {
    AppointmentOutput output = getUseCase.execute(id);
    return ResponseEntity.ok(output);
  }

  @Override
  @GetMapping
  public ResponseEntity<Page<AppointmentOutput>> listAll(
    @RequestParam(required = false) Long clientId,
    @RequestParam(required = false) Long estateAgentId,
    @RequestParam(required = false) Long estateId,
    @RequestParam(required = false) String status,
    @RequestParam(required = false) String startDateTime,
    @RequestParam(required = false) String endDateTime,
    @RequestParam(defaultValue = "0") Integer page,
    @RequestParam(defaultValue = "20") Integer size
  ) {
    var query = new ListAppointmentsQuery(
      clientId,
      estateAgentId,
      estateId,
      status,
      startDateTime,
      endDateTime,
      page,
      size
    );

    Page<AppointmentOutput> output = listUseCase.execute(query);
    return ResponseEntity.ok(output);
  }

  @Override
  @PatchMapping("/{id}/reschedule")
  public ResponseEntity<AppointmentOutput> reschedule(
    @PathVariable Long id,
    @Valid @RequestBody RescheduleAppointmentRequest request
  ) {
    var command = new RescheduleAppointmentCommand(
      id,
      request.startDateTime(),
      request.reason()
    );

    AppointmentOutput output = rescheduleUseCase.execute(command);
    return ResponseEntity.ok(output);
  }

  @Override
  @PostMapping("/{id}/cancel")
  public ResponseEntity<AppointmentOutput> cancel(
    @PathVariable Long id,
    @RequestBody(required = false) CancelAppointmentRequest request
  ) {
    String reason = request != null ? request.reason() : null;
    var command = new CancelAppointmentCommand(id, reason);

    AppointmentOutput output = cancelUseCase.execute(command);
    return ResponseEntity.ok(output);
  }

  @Override
  @PostMapping("/{id}/confirm")
  public ResponseEntity<AppointmentOutput> confirm(@PathVariable Long id) {
    AppointmentOutput output = confirmUseCase.execute(new ConfirmAppointmentCommand(id));
    return ResponseEntity.ok(output);
  }

  @Override
  @PostMapping("/{id}/conclude")
  public ResponseEntity<AppointmentOutput> conclude(@PathVariable Long id) {
    AppointmentOutput output = concludeUseCase.execute(new ConcludeAppointmentCommand(id));
    return ResponseEntity.ok(output);
  }

  @Override
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    deleteUseCase.execute(id);
    return ResponseEntity.noContent().build();
  }
}
