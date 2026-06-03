package com.penelopec.calservice.appointment.infrastructure.controller;

import com.penelopec.calservice.appointment.application.command.AppointmentCommand;
import com.penelopec.calservice.appointment.application.command.CancelAppointmentCommand;
import com.penelopec.calservice.appointment.application.command.ConcludeAppointmentCommand;
import com.penelopec.calservice.appointment.application.command.ConfirmAppointmentCommand;
import com.penelopec.calservice.appointment.application.command.RescheduleAppointmentCommand;
import com.penelopec.calservice.appointment.application.output.AppointmentOutput;
import com.penelopec.calservice.appointment.application.output.ExportAppointmentOutput;
import com.penelopec.calservice.appointment.application.output.AvailableSlotsOutput;
import com.penelopec.calservice.appointment.application.output.ScheduleOutput;
import com.penelopec.calservice.appointment.application.output.AppointmentReportOutput;
import com.penelopec.calservice.appointment.application.query.GetAvailableSlotsQuery;
import com.penelopec.calservice.appointment.application.query.ExportAppointmentsQuery;
import com.penelopec.calservice.appointment.application.query.ListAppointmentsQuery;
import com.penelopec.calservice.appointment.application.query.ReportAppointmentsQuery;
import com.penelopec.calservice.appointment.application.usecase.*;
import com.penelopec.calservice.appointment.infrastructure.controller.dto.CancelAppointmentRequest;
import com.penelopec.calservice.appointment.infrastructure.controller.dto.CreateAppointmentRequest;
import com.penelopec.calservice.appointment.infrastructure.controller.dto.RescheduleAppointmentRequest;
import com.penelopec.calservice.appointment.infrastructure.export.AppointmentExportFormatter;
import com.penelopec.calservice.shared.pagination.Page;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

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
    private final GetSchedulesUseCase getSchedulesUseCase;
    private final GetAvailableSlotsUseCase getAvailableSlotsUseCase;
    private final ExportAppointmentsUseCase exportUseCase;
    private final ReportAppointmentsUseCase reportUseCase;

    public AppointmentController(CreateAppointmentUseCase createUseCase,
                                 GetAppointmentUseCase getUseCase,
                                 ListAppointmentsUseCase listUseCase,
                                 ChangeAppointmentUseCase rescheduleUseCase,
                                 CancelAppointmentUseCase cancelUseCase,
                                 ConfirmAppointmentUseCase confirmUseCase,
                                 ConcludeAppointmentUseCase concludeUseCase,
                                 DeleteAppointmentUseCase deleteUseCase,
                                 GetSchedulesUseCase getSchedulesUseCase,
                                 GetAvailableSlotsUseCase getAvailableSlotsUseCase,
                                 ExportAppointmentsUseCase exportUseCase,
                                 ReportAppointmentsUseCase reportUseCase
    ) {
        this.createUseCase = createUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
        this.rescheduleUseCase = rescheduleUseCase;
        this.cancelUseCase = cancelUseCase;
        this.confirmUseCase = confirmUseCase;
        this.concludeUseCase = concludeUseCase;
        this.deleteUseCase = deleteUseCase;
        this.getSchedulesUseCase = getSchedulesUseCase;
        this.getAvailableSlotsUseCase = getAvailableSlotsUseCase;
        this.exportUseCase = exportUseCase;
        this.reportUseCase = reportUseCase;
    }

    @Override
    @PostMapping
    public ResponseEntity<AppointmentOutput> create(@Valid @RequestBody CreateAppointmentRequest request,
                                                    Authentication authentication) {
        assertCanCreateAppointment(authentication, request.clientId(), request.estateAgentId());

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
    public ResponseEntity<AppointmentOutput> getById(@PathVariable Long id, Authentication authentication) {
        AppointmentOutput output = getUseCase.execute(id);
        assertCanViewAppointment(authentication, output.clientId(), output.estateAgentId());
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
            @RequestParam(defaultValue = "20") Integer size,
            Authentication authentication
    ) {
        Long effectiveEstateAgentId = isBroker(authentication) ? currentUserId(authentication) : estateAgentId;
        Long effectiveClientId = isClient(authentication) ? currentUserId(authentication) : clientId;

        if (isBroker(authentication) && estateAgentId != null && !currentUserId(authentication).equals(estateAgentId)) {
            throw new ResponseStatusException(FORBIDDEN, "Corretores só podem listar seus próprios agendamentos");
        }

        if (isClient(authentication) && clientId != null && !currentUserId(authentication).equals(clientId)) {
            throw new ResponseStatusException(FORBIDDEN, "Clientes só podem listar seus próprios agendamentos");
        }

        var query = new ListAppointmentsQuery(
                effectiveClientId,
                effectiveEstateAgentId,
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
            @Valid @RequestBody RescheduleAppointmentRequest request,
            Authentication authentication
    ) {
        AppointmentOutput currentAppointment = getUseCase.execute(id);
        assertCanMutateAppointment(authentication, currentAppointment.clientId(), currentAppointment.estateAgentId());

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
            @RequestBody(required = false) CancelAppointmentRequest request,
            Authentication authentication
    ) {
        AppointmentOutput currentAppointment = getUseCase.execute(id);
        assertCanMutateAppointment(authentication, currentAppointment.clientId(), currentAppointment.estateAgentId());

        String reason = request != null ? request.reason() : null;
        var command = new CancelAppointmentCommand(id, reason);

        AppointmentOutput output = cancelUseCase.execute(command);
        return ResponseEntity.ok(output);
    }

    @Override
    @PostMapping("/{id}/confirm")
    public ResponseEntity<AppointmentOutput> confirm(@PathVariable Long id, Authentication authentication) {
        AppointmentOutput currentAppointment = getUseCase.execute(id);
        assertCanConfirmOrConclude(authentication, currentAppointment.estateAgentId());
        AppointmentOutput output = confirmUseCase.execute(new ConfirmAppointmentCommand(id));
        return ResponseEntity.ok(output);
    }

    @Override
    @PostMapping("/{id}/conclude")
    public ResponseEntity<AppointmentOutput> conclude(@PathVariable Long id, Authentication authentication) {
        AppointmentOutput currentAppointment = getUseCase.execute(id);
        assertCanConfirmOrConclude(authentication, currentAppointment.estateAgentId());
        AppointmentOutput output = concludeUseCase.execute(new ConcludeAppointmentCommand(id));
        return ResponseEntity.ok(output);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(
            @RequestParam(name = "idCorretor", required = false) Long idCorretor,
            @RequestParam(required = false) String periodoInicio,
            @RequestParam(required = false) String periodoFim,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "csv") String format
    ) {
        String normalizedFormat = format == null ? "" : format.trim().toLowerCase();
        if (!"csv".equals(normalizedFormat) && !"xlsx".equals(normalizedFormat)) {
            throw new ResponseStatusException(BAD_REQUEST, "Formato invalido. Use csv ou xlsx");
        }

        List<ExportAppointmentOutput> appointments = exportUseCase.execute(
            new ExportAppointmentsQuery(idCorretor, periodoInicio, periodoFim, status)
        );

        if ("xlsx".equals(normalizedFormat)) {
            byte[] bytes = AppointmentExportFormatter.toXlsx(appointments);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=agendamentos.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(bytes);
        }

        String csv = AppointmentExportFormatter.toCsv(appointments);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=agendamentos.csv")
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(csv.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppointmentOutput currentAppointment = getUseCase.execute(id);
        assertCanDeleteAppointment(authentication, currentAppointment.estateAgentId());
        deleteUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @GetMapping("/schedules")
    public ResponseEntity<List<ScheduleOutput>> getSchedules() {
        List<ScheduleOutput> output = getSchedulesUseCase.execute();
        return ResponseEntity.ok(output);
    }

    @Override
    @GetMapping("/slots")
    public ResponseEntity<AvailableSlotsOutput> getAvailableSlots(
            @RequestParam Long eventTypeId,
            @RequestParam String start,
            @RequestParam String end
    ) {
        var query = new GetAvailableSlotsQuery(eventTypeId, start, end);
        AvailableSlotsOutput output = getAvailableSlotsUseCase.execute(query);
        return ResponseEntity.ok(output);
    }

    @Override
    @GetMapping("/report")
    public ResponseEntity<Page<AppointmentReportOutput>> report(
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) Long estateAgentId,
            @RequestParam(required = false) Long estateId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String estateTypeKey,
            @RequestParam(required = false) String startDateTime,
            @RequestParam(required = false) String endDateTime,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            Authentication authentication
    ) {
        Long effectiveClientId = isClient(authentication) ? currentUserId(authentication) : clientId;
        Long effectiveEstateAgentId;
        if (isClient(authentication)) {
            effectiveEstateAgentId = null;
        } else if (isBroker(authentication)) {
            effectiveEstateAgentId = currentUserId(authentication);
        } else {
            effectiveEstateAgentId = estateAgentId;
        }

        var query = new ReportAppointmentsQuery(
                effectiveClientId,
                effectiveEstateAgentId,
                estateId,
                status,
                estateTypeKey,
                startDateTime,
                endDateTime,
                page,
                size
        );

        return ResponseEntity.ok(reportUseCase.execute(query));
    }

    private void assertCanCreateAppointment(Authentication authentication, Long clientId, Long estateAgentId) {
        if (isAdmin(authentication)) {
            return;
        }

        Long currentUserId = currentUserId(authentication);
        if (isClient(authentication) && !currentUserId.equals(clientId)) {
            throw new ResponseStatusException(FORBIDDEN, "Clientes só podem criar agendamentos em seu próprio nome");
        }

        if (isBroker(authentication) && !currentUserId.equals(estateAgentId)) {
            throw new ResponseStatusException(FORBIDDEN, "Corretores só podem criar agendamentos vinculados a si mesmos");
        }
    }

    private void assertCanViewAppointment(Authentication authentication, Long clientId, Long estateAgentId) {
        if (isAdmin(authentication)) {
            return;
        }

        Long currentUserId = currentUserId(authentication);
        if (isClient(authentication) && !currentUserId.equals(clientId)) {
            throwNotFound();
        }

        if (isBroker(authentication) && !currentUserId.equals(estateAgentId)) {
            throwNotFound();
        }
    }

    private void assertCanMutateAppointment(Authentication authentication, Long clientId, Long estateAgentId) {
        if (isAdmin(authentication)) {
            return;
        }

        Long currentUserId = currentUserId(authentication);
        if (isClient(authentication) && !currentUserId.equals(clientId)) {
            throwNotFound();
        }

        if (isBroker(authentication) && !currentUserId.equals(estateAgentId)) {
            throwNotFound();
        }
    }

    private void assertCanDeleteAppointment(Authentication authentication, Long estateAgentId) {
        if (isAdmin(authentication)) {
            return;
        }

        if (isClient(authentication)) {
            throwNotFound();
        }

        if (!isBroker(authentication)) {
            throwNotFound();
        }

        Long currentUserId = currentUserId(authentication);
        if (!currentUserId.equals(estateAgentId)) {
            throwNotFound();
        }
    }

    private void assertCanConfirmOrConclude(Authentication authentication, Long estateAgentId) {
        if (isAdmin(authentication)) {
            return;
        }

        if (!isBroker(authentication)) {
            throwNotFound();
        }

        assertBrokerOwnAppointment(authentication, estateAgentId);
    }

    private void assertBrokerOwnAppointment(Authentication authentication, Long estateAgentId) {
        if (isBroker(authentication) && !currentUserId(authentication).equals(estateAgentId)) {
            throwNotFound();
        }
    }

    private void throwNotFound() {
        throw new ResponseStatusException(NOT_FOUND, "Agendamento não encontrado");
    }

    private boolean isBroker(Authentication authentication) {
        return hasRole(authentication, "ROLE_CORRETOR");
    }

    private boolean isClient(Authentication authentication) {
        return hasRole(authentication, "ROLE_CLIENTE");
    }

    private boolean isAdmin(Authentication authentication) {
        return hasRole(authentication, "ROLE_ADMINISTRADOR");
    }

    private Long currentUserId(Authentication authentication) {
        try {
            return Long.valueOf(authentication.getName());
        } catch (RuntimeException ex) {
            throw new ResponseStatusException(FORBIDDEN, "Usuário autenticado sem ID válido");
        }
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> role.equals(authority.getAuthority()));
    }

}
