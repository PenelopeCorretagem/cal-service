package com.penelopec.calservice.appointment.infrastructure.controller;

import com.penelopec.calservice.appointment.application.output.AppointmentOutput;
import com.penelopec.calservice.appointment.application.output.AvailableSlotsOutput;
import com.penelopec.calservice.appointment.application.output.ScheduleOutput;
import com.penelopec.calservice.appointment.infrastructure.controller.dto.CancelAppointmentRequest;
import com.penelopec.calservice.appointment.infrastructure.controller.dto.CreateAppointmentRequest;
import com.penelopec.calservice.appointment.infrastructure.controller.dto.RescheduleAppointmentRequest;
import com.penelopec.calservice.shared.error.core.ApiErrorResponse;
import com.penelopec.calservice.shared.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Agendamentos", description = "Endpoints para criação, consulta, reagendamento, cancelamento e exclusão de agendamentos integrados ao Cal.com")
@SecurityRequirement(name = "bearerAuth")
public interface AppointmentControllerSwagger {

  // ──────────────────────────────────────────────
  // POST /appointments
  // ──────────────────────────────────────────────

  @Operation(
    summary = "Criar agendamento",
    description = "Cria um novo agendamento no Cal.com e persiste o vínculo no banco de dados local. "
      + "O backend é o único responsável pela comunicação com o Cal.com — o frontend apenas envia a requisição para esta API. "
      + "A duração do agendamento é fixa (60 minutos), calculada automaticamente a partir do startDateTime. "
      + "Após a criação, o **bookingUid** retornado identifica o agendamento no Cal.com."
  )
  @ApiResponses({
    @ApiResponse(
      responseCode = "201",
      description = "Agendamento criado com sucesso",
      headers = @Header(
        name = "Location",
        description = "URI do recurso criado",
        schema = @Schema(type = "string", example = "/appointments/1")
      ),
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = AppointmentOutput.class),
        examples = @ExampleObject(
          name = "Agendamento criado",
          value = """
            {
              "id": 1,
              "bookingUid": "bk_abc123",
              "eventTypeId": 100,
              "clientId": 10,
              "estateAgentId": 20,
              "durationMinutes": 60,
              "status": "PENDING",
              "startDateTime": "2026-04-10T14:00:00",
              "endDateTime": "2026-04-10T15:00:00",
              "attendeeName": "Maria Silva",
              "attendeeEmail": "maria@email.com",
              "notes": "Primeira visita ao empreendimento",
              "reason": null,
              "createdAt": "2026-03-22T10:00:00",
              "updatedAt": "2026-03-22T10:00:00"
            }"""
        )
      )
    ),
    @ApiResponse(
      responseCode = "422",
      description = "Erro de validação nos campos enviados",
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = ApiErrorResponse.class),
        examples = @ExampleObject(
          name = "Erro de validação",
          value = """
            {
              "timestamp": "2026-03-22T10:00:00Z",
              "status": 422,
              "code": "CORE-VALIDATION",
              "message": "Dados inválidos na requisição.",
              "path": "/appointments",
              "severity": "WARN",
              "violations": [
                {
                  "field": "startDateTime",
                  "message": "startDateTime é obrigatório",
                  "code": "NotNull"
                }
              ]
            }"""
        )
      )
    ),
    @ApiResponse(
      responseCode = "409",
      description = "Conflito de horário para o corretor",
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = ApiErrorResponse.class),
        examples = @ExampleObject(
          name = "Conflito de horário",
          value = """
            {
              "timestamp": "2026-03-22T10:00:00Z",
              "status": 409,
              "code": "APT-SCHEDULE-CONFLICT",
              "message": "Já existe um agendamento ativo para este corretor na data e horário informados.",
              "path": "/appointments",
              "severity": "WARN"
            }"""
        )
      )
    ),
    @ApiResponse(
      responseCode = "401",
      description = "Token JWT ausente ou inválido",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
    ),
    @ApiResponse(
      responseCode = "502",
      description = "Falha na comunicação com o Cal.com",
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = ApiErrorResponse.class),
        examples = @ExampleObject(
          name = "Erro de integração",
          value = """
            {
              "timestamp": "2026-03-22T10:00:00Z",
              "status": 502,
              "code": "APT-BOOKING-CREATE-FAILED",
              "message": "Falha ao criar booking no serviço externo.",
              "path": "/appointments",
              "severity": "ERROR"
            }"""
        )
      )
    )
  })
  ResponseEntity<AppointmentOutput> create(@Valid @RequestBody CreateAppointmentRequest request);

  // ──────────────────────────────────────────────
  // GET /appointments/{id}
  // ──────────────────────────────────────────────

  @Operation(
    summary = "Buscar agendamento por ID",
    description = "Retorna os dados de um agendamento específico a partir do identificador local. "
      + "Os dados são consultados diretamente do banco de dados local."
  )
  @ApiResponses({
    @ApiResponse(
      responseCode = "200",
      description = "Agendamento encontrado",
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = AppointmentOutput.class),
        examples = @ExampleObject(
          name = "Agendamento encontrado",
          value = """
            {
              "id": 1,
              "bookingUid": "bk_abc123",
              "eventTypeId": 100,
              "clientId": 10,
              "estateAgentId": 20,
              "durationMinutes": 60,
              "status": "CONFIRMED",
              "startDateTime": "2026-04-10T14:00:00",
              "endDateTime": "2026-04-10T15:00:00",
              "attendeeName": "Maria Silva",
              "attendeeEmail": "maria@email.com",
              "notes": "Primeira visita ao empreendimento",
              "reason": null,
              "createdAt": "2026-03-22T10:00:00",
              "updatedAt": "2026-03-22T10:00:00"
            }"""
        )
      )
    ),
    @ApiResponse(
      responseCode = "404",
      description = "Agendamento não encontrado",
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = ApiErrorResponse.class),
        examples = @ExampleObject(
          name = "Não encontrado",
          value = """
            {
              "timestamp": "2026-03-22T10:00:00Z",
              "status": 404,
              "code": "APT-NOT-FOUND",
              "message": "Agendamento não encontrado: 999.",
              "path": "/appointments/999",
              "severity": "WARN"
            }"""
        )
      )
    ),
    @ApiResponse(
      responseCode = "401",
      description = "Token JWT ausente ou inválido",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
    )
  })
  ResponseEntity<AppointmentOutput> getById(
    @Parameter(description = "ID do agendamento", example = "1", required = true)
    @PathVariable Long id
  );

  // ──────────────────────────────────────────────
  // GET /appointments
  // ──────────────────────────────────────────────

  @Operation(
    summary = "Listar agendamentos",
    description = "Retorna agendamentos com filtros opcionais e **paginação obrigatória**. "
      + "Valores padrão: page=0, size=20 (máximo 100). "
      + "É possível filtrar por empreendimento (estateId) via tipo de evento associado."
  )
  @ApiResponses({
    @ApiResponse(
      responseCode = "200",
      description = "Lista paginada de agendamentos retornada com sucesso",
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = Page.class),
        examples = @ExampleObject(
          name = "Lista paginada",
          value = """
            {
              "content": [
                {
                  "id": 1,
                  "bookingUid": "bk_abc123",
                  "eventTypeId": 100,
                  "clientId": 10,
                  "estateAgentId": 20,
                  "durationMinutes": 60,
                  "status": "PENDING",
                  "startDateTime": "2026-04-10T14:00:00",
                  "endDateTime": "2026-04-10T15:00:00",
                  "attendeeName": "Maria Silva",
                  "attendeeEmail": "maria@email.com",
                  "notes": "Primeira visita",
                  "reason": null,
                  "createdAt": "2026-03-22T10:00:00",
                  "updatedAt": "2026-03-22T10:00:00"
                }
              ],
              "page": 0,
              "size": 20,
              "totalElements": 1,
              "totalPages": 1
            }"""
        )
      )
    ),
    @ApiResponse(
      responseCode = "401",
      description = "Token JWT ausente ou inválido",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
    )
  })
  ResponseEntity<Page<AppointmentOutput>> listAll(
    @Parameter(description = "Filtra por cliente", example = "10") @RequestParam(required = false) Long clientId,
    @Parameter(description = "Filtra por corretor", example = "20") @RequestParam(required = false) Long estateAgentId,
    @Parameter(description = "Filtra por empreendimento (via tipo de evento)", example = "30") @RequestParam(required = false) Long estateId,
    @Parameter(description = "Filtra por status (PENDING, CONFIRMED, CANCELLED, CONCLUDED)", example = "PENDING") @RequestParam(required = false) String status,
    @Parameter(description = "Data/hora inicial (ISO-8601)", example = "2026-04-10T14:00:00") @RequestParam(required = false) String startDateTime,
    @Parameter(description = "Data/hora final (ISO-8601)", example = "2026-04-10T18:00:00") @RequestParam(required = false) String endDateTime,
    @Parameter(description = "Página (base 0, padrão 0)", example = "0") @RequestParam(defaultValue = "0") Integer page,
    @Parameter(description = "Tamanho da página (padrão 20, máximo 100)", example = "20") @RequestParam(defaultValue = "20") Integer size
  );

  // ──────────────────────────────────────────────
  // PATCH /appointments/{id}/reschedule
  // ──────────────────────────────────────────────

  @Operation(
    summary = "Reagendar agendamento",
    description = "Reagenda um agendamento existente para um novo horário. "
      + "A duração permanece fixa em 60 minutos e o backend passa a usar o novo bookingUid retornado pelo Cal.com. "
      + "A API atualiza o booking no Cal.com e persiste a alteração localmente. "
      + "Só é permitido reagendar agendamentos com status **não terminal** (PENDING ou CONFIRMED). "
      + "O motivo do reagendamento é armazenado para auditoria."
  )
  @ApiResponses({
    @ApiResponse(
      responseCode = "200",
      description = "Agendamento reagendado com sucesso",
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = AppointmentOutput.class),
        examples = @ExampleObject(
          name = "Agendamento reagendado",
          value = """
            {
              "id": 1,
              "bookingUid": "bk_def456",
              "eventTypeId": 100,
              "clientId": 10,
              "estateAgentId": 20,
              "durationMinutes": 60,
              "status": "PENDING",
              "startDateTime": "2026-04-12T16:00:00",
              "endDateTime": "2026-04-12T17:00:00",
              "attendeeName": "Maria Silva",
              "attendeeEmail": "maria@email.com",
              "notes": "Primeira visita",
              "reason": "Conflito de agenda",
              "createdAt": "2026-03-22T10:00:00",
              "updatedAt": "2026-03-22T15:00:00"
            }"""
        )
      )
    ),
    @ApiResponse(
      responseCode = "404",
      description = "Agendamento não encontrado",
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = ApiErrorResponse.class),
        examples = @ExampleObject(
          name = "Não encontrado",
          value = """
            {
              "timestamp": "2026-03-22T10:00:00Z",
              "status": 404,
              "code": "APT-NOT-FOUND",
              "message": "Agendamento não encontrado: 999.",
              "path": "/appointments/999/reschedule",
              "severity": "WARN"
            }"""
        )
      )
    ),
    @ApiResponse(
      responseCode = "409",
      description = "Conflito de estado do agendamento",
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = ApiErrorResponse.class),
        examples = @ExampleObject(
          name = "Status terminal",
          value = """
            {
              "timestamp": "2026-03-22T10:00:00Z",
              "status": 409,
              "code": "APT-INVALID-STATUS-TRANSITION",
              "message": "Não é possível reagendar agendamento com status CANCELLED",
              "path": "/appointments/1/reschedule",
              "severity": "WARN"
            }"""
        )
      )
    ),
    @ApiResponse(
      responseCode = "401",
      description = "Token JWT ausente ou inválido",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
    ),
    @ApiResponse(
      responseCode = "502",
      description = "Falha na comunicação com o Cal.com",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
    )
  })
  ResponseEntity<AppointmentOutput> reschedule(
    @Parameter(description = "ID do agendamento a ser reagendado", example = "1", required = true)
    @PathVariable Long id,
    @Valid @RequestBody RescheduleAppointmentRequest request
  );

  // ──────────────────────────────────────────────
  // POST /appointments/{id}/cancel
  // ──────────────────────────────────────────────

  @Operation(
    summary = "Cancelar agendamento",
    description = "Cancela um agendamento existente no Cal.com e atualiza o status local para **CANCELLED**. "
      + "Só é permitido cancelar agendamentos com status **não terminal** (PENDING ou CONFIRMED). "
      + "O motivo do cancelamento é armazenado para auditoria."
  )
  @ApiResponses({
    @ApiResponse(
      responseCode = "200",
      description = "Agendamento cancelado com sucesso",
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = AppointmentOutput.class),
        examples = @ExampleObject(
          name = "Agendamento cancelado",
          value = """
            {
              "id": 1,
              "bookingUid": "bk_abc123",
              "eventTypeId": 100,
              "clientId": 10,
              "estateAgentId": 20,
              "durationMinutes": 60,
              "status": "CANCELLED",
              "startDateTime": "2026-04-10T14:00:00",
              "endDateTime": "2026-04-10T15:00:00",
              "attendeeName": "Maria Silva",
              "attendeeEmail": "maria@email.com",
              "notes": "Primeira visita",
              "reason": "Cliente desistiu da visita",
              "createdAt": "2026-03-22T10:00:00",
              "updatedAt": "2026-03-22T16:00:00"
            }"""
        )
      )
    ),
    @ApiResponse(
      responseCode = "404",
      description = "Agendamento não encontrado",
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = ApiErrorResponse.class),
        examples = @ExampleObject(
          name = "Não encontrado",
          value = """
            {
              "timestamp": "2026-03-22T10:00:00Z",
              "status": 404,
              "code": "APT-NOT-FOUND",
              "message": "Agendamento não encontrado: 999.",
              "path": "/appointments/999/cancel",
              "severity": "WARN"
            }"""
        )
      )
    ),
    @ApiResponse(
      responseCode = "409",
      description = "Conflito de estado do agendamento",
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = ApiErrorResponse.class),
        examples = @ExampleObject(
          name = "Status terminal",
          value = """
            {
              "timestamp": "2026-03-22T10:00:00Z",
              "status": 409,
              "code": "APT-INVALID-STATUS-TRANSITION",
              "message": "Não é possível cancelar agendamento com status CONCLUDED",
              "path": "/appointments/1/cancel",
              "severity": "WARN"
            }"""
        )
      )
    ),
    @ApiResponse(
      responseCode = "401",
      description = "Token JWT ausente ou inválido",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
    ),
    @ApiResponse(
      responseCode = "502",
      description = "Falha na comunicação com o Cal.com",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
    )
  })
  ResponseEntity<AppointmentOutput> cancel(
    @Parameter(description = "ID do agendamento a ser cancelado", example = "1", required = true)
    @PathVariable Long id,
    @RequestBody(required = false) CancelAppointmentRequest request
  );

  // ──────────────────────────────────────────────
  // POST /appointments/{id}/confirm
  // ──────────────────────────────────────────────

  @Operation(
    summary = "Confirmar agendamento",
    description = "Confirma um agendamento com status **PENDING**. "
      + "Agendamentos com status terminal (CANCELLED ou CONCLUDED) não podem ser confirmados."
  )
  @ApiResponses({
    @ApiResponse(
      responseCode = "200",
      description = "Agendamento confirmado com sucesso",
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = AppointmentOutput.class),
        examples = @ExampleObject(
          name = "Agendamento confirmado",
          value = """
            {
              "id": 1,
              "bookingUid": "bk_abc123",
              "eventTypeId": 100,
              "clientId": 10,
              "estateAgentId": 20,
              "durationMinutes": 60,
              "status": "CONFIRMED",
              "startDateTime": "2026-04-10T14:00:00",
              "endDateTime": "2026-04-10T15:00:00",
              "attendeeName": "Maria Silva",
              "attendeeEmail": "maria@email.com",
              "notes": "Primeira visita",
              "reason": null,
              "createdAt": "2026-03-22T10:00:00",
              "updatedAt": "2026-03-22T11:00:00"
            }"""
        )
      )
    ),
    @ApiResponse(
      responseCode = "404",
      description = "Agendamento não encontrado",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
    ),
    @ApiResponse(
      responseCode = "409",
      description = "Conflito de estado do agendamento",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
    ),
    @ApiResponse(
      responseCode = "401",
      description = "Token JWT ausente ou inválido",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
    )
  })
  ResponseEntity<AppointmentOutput> confirm(
    @Parameter(description = "ID do agendamento a ser confirmado", example = "1", required = true)
    @PathVariable Long id
  );

  // ──────────────────────────────────────────────
  // POST /appointments/{id}/conclude
  // ──────────────────────────────────────────────

  @Operation(
    summary = "Concluir agendamento",
    description = "Conclui um agendamento. "
      + "Agendamentos com status **CANCELLED** não podem ser concluídos."
  )
  @ApiResponses({
    @ApiResponse(
      responseCode = "200",
      description = "Agendamento concluído com sucesso",
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = AppointmentOutput.class),
        examples = @ExampleObject(
          name = "Agendamento concluído",
          value = """
            {
              "id": 1,
              "bookingUid": "bk_abc123",
              "eventTypeId": 100,
              "clientId": 10,
              "estateAgentId": 20,
              "durationMinutes": 60,
              "status": "CONCLUDED",
              "startDateTime": "2026-04-10T14:00:00",
              "endDateTime": "2026-04-10T15:00:00",
              "attendeeName": "Maria Silva",
              "attendeeEmail": "maria@email.com",
              "notes": "Primeira visita",
              "reason": null,
              "createdAt": "2026-03-22T10:00:00",
              "updatedAt": "2026-04-10T15:30:00"
            }"""
        )
      )
    ),
    @ApiResponse(
      responseCode = "404",
      description = "Agendamento não encontrado",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
    ),
    @ApiResponse(
      responseCode = "409",
      description = "Conflito de estado do agendamento",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
    ),
    @ApiResponse(
      responseCode = "401",
      description = "Token JWT ausente ou inválido",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
    )
  })
  ResponseEntity<AppointmentOutput> conclude(
    @Parameter(description = "ID do agendamento a ser concluído", example = "1", required = true)
    @PathVariable Long id
  );

  // ──────────────────────────────────────────────
  // DELETE /appointments/{id}
  // ──────────────────────────────────────────────

  @Operation(
    summary = "Excluir agendamento",
    description = "Exclui um agendamento do banco de dados local. "
      + "A operação é restrita a usuários com perfil ADMINISTRADOR. "
      + "Se houver um **bookingUid** vinculado, também cancela o booking no Cal.com."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Agendamento excluído com sucesso"),
    @ApiResponse(
      responseCode = "404",
      description = "Agendamento não encontrado",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
    ),
    @ApiResponse(
      responseCode = "401",
      description = "Token JWT ausente ou inválido",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
    ),
    @ApiResponse(
      responseCode = "403",
      description = "Usuário sem permissão para excluir agendamento",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
    ),
    @ApiResponse(
      responseCode = "502",
      description = "Falha na comunicação com o Cal.com",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
    )
  })
  ResponseEntity<Void> delete(
    @Parameter(description = "ID do agendamento a ser excluído", example = "1", required = true)
    @PathVariable Long id
  );

  // ──────────────────────────────────────────────
  // GET /appointments/schedules
  // ──────────────────────────────────────────────

  @Operation(
    summary = "Buscar horários de trabalho (schedules)",
    description = "Retorna todos os schedules (horários de trabalho) configurados no Cal.com. "
      + "Cada schedule contém as regras de disponibilidade por dia da semana e possíveis overrides por data específica."
  )
  @ApiResponses({
    @ApiResponse(
      responseCode = "200",
      description = "Schedules retornados com sucesso",
      content = @Content(
        mediaType = "application/json",
        examples = @ExampleObject(
          name = "Schedules",
          value = """
            [
              {
                "id": 254,
                "name": "Horário comercial",
                "timeZone": "America/Sao_Paulo",
                "availability": [
                  { "days": ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday"], "startTime": "09:00", "endTime": "18:00" }
                ],
                "isDefault": true,
                "overrides": []
              }
            ]"""
        )
      )
    ),
    @ApiResponse(
      responseCode = "401",
      description = "Token JWT ausente ou inválido",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
    ),
    @ApiResponse(
      responseCode = "502",
      description = "Falha na comunicação com o Cal.com",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
    )
  })
  ResponseEntity<List<ScheduleOutput>> getSchedules();

  // ──────────────────────────────────────────────
  // GET /appointments/slots
  // ──────────────────────────────────────────────

  @Operation(
    summary = "Buscar horários disponíveis (slots)",
    description = "Retorna os horários disponíveis para agendamento em um tipo de evento, dentro de um período. "
      + "Os horários retornados já consideram o schedule configurado no Cal.com e agendamentos existentes."
  )
  @ApiResponses({
    @ApiResponse(
      responseCode = "200",
      description = "Slots disponíveis retornados com sucesso",
      content = @Content(
        mediaType = "application/json",
        examples = @ExampleObject(
          name = "Slots disponíveis",
          value = """
            {
              "slots": {
                "2026-04-28": ["2026-04-28T09:00:00-03:00", "2026-04-28T10:00:00-03:00", "2026-04-28T14:00:00-03:00"],
                "2026-04-29": ["2026-04-29T09:00:00-03:00", "2026-04-29T11:00:00-03:00"]
              }
            }"""
        )
      )
    ),
    @ApiResponse(
      responseCode = "401",
      description = "Token JWT ausente ou inválido",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
    ),
    @ApiResponse(
      responseCode = "502",
      description = "Falha na comunicação com o Cal.com",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
    )
  })
  ResponseEntity<AvailableSlotsOutput> getAvailableSlots(
    @Parameter(description = "ID do tipo de evento", example = "100", required = true) @RequestParam Long eventTypeId,
    @Parameter(description = "Data início (ISO-8601, ex: 2026-04-28)", example = "2026-04-28", required = true) @RequestParam String start,
    @Parameter(description = "Data fim (ISO-8601, ex: 2026-04-30)", example = "2026-04-30", required = true) @RequestParam String end
  );
}
