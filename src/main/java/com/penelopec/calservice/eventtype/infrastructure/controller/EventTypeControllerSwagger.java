package com.penelopec.calservice.eventtype.infrastructure.controller;

import com.penelopec.calservice.eventtype.application.output.EventTypeOutput;
import com.penelopec.calservice.eventtype.infrastructure.controller.dto.ApiErrorResponse;
import com.penelopec.calservice.eventtype.infrastructure.controller.dto.CreateEventTypeRequest;
import com.penelopec.calservice.eventtype.infrastructure.controller.dto.UpdateEventTypeRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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

import java.util.List;

@Tag(name = "Tipos de Evento", description = "Endpoints para criação, consulta, atualização e exclusão de tipos de evento integrados ao Cal.com")
@SecurityRequirement(name = "bearerAuth")
public interface EventTypeControllerSwagger {

  @Operation(
    summary = "Criar tipo de evento",
    description = "Cria um novo tipo de evento no Cal.com e persiste o vínculo com o imóvel no banco de dados local. "
      + "O título informado é utilizado para gerar automaticamente o **slug** do evento. "
      + "Campos opcionais: **lengthInMinutes** (padrão: 60), **minimumBookingNotice** (padrão: 120), **hidden** (padrão: false)."
  )
  @ApiResponses({
    @ApiResponse(
      responseCode = "201",
      description = "Tipo de evento criado com sucesso",
      headers = @Header(
        name = "Location",
        description = "URI do recurso criado",
        schema = @Schema(type = "string", example = "/event-types/123")
      ),
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = EventTypeOutput.class),
        examples = @ExampleObject(
          name = "Exemplo de resposta",
          value = "{\"id\": 123, \"title\": \"Visita ao Empreendimento Parque das Flores\", \"slug\": \"visita-ao-empreendimento-parque-das-flores\", \"description\": \"Visita presencial ao empreendimento\", \"lengthInMinutes\": 60, \"minimumBookingNotice\": 120, \"hidden\": false, \"estateId\": 42}"
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
          value = "{\"status\": 422, \"error\": \"Unprocessable Entity\", \"message\": \"Erro de validação\", \"path\": \"/event-types\", \"timestamp\": \"2026-03-07T14:30:00Z\", \"fieldErrors\": [{\"field\": \"title\", \"message\": \"Título é obrigatório\"}]}"
        )
      )
    ),
    @ApiResponse(
      responseCode = "401",
      description = "Token JWT ausente ou inválido",
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = ApiErrorResponse.class)
      )
    ),
    @ApiResponse(
      responseCode = "500",
      description = "Erro interno — falha na comunicação com o Cal.com ou no processamento",
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = ApiErrorResponse.class)
      )
    )
  })
  ResponseEntity<EventTypeOutput> create(@Valid @RequestBody CreateEventTypeRequest request);

  @Operation(
    summary = "Buscar tipo de evento por ID",
    description = "Retorna os dados de um tipo de evento específico consultando diretamente a API do Cal.com. "
      + "Caso o tipo de evento não exista, retorna **404 Not Found**."
  )
  @ApiResponses({
    @ApiResponse(
      responseCode = "200",
      description = "Tipo de evento encontrado",
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = EventTypeOutput.class),
        examples = @ExampleObject(
          name = "Exemplo de resposta",
          value = "{\"id\": 123, \"title\": \"Visita ao Empreendimento Parque das Flores\", \"slug\": \"visita-ao-empreendimento-parque-das-flores\", \"description\": \"Visita presencial\", \"lengthInMinutes\": 60, \"minimumBookingNotice\": 120, \"hidden\": false, \"estateId\": null}"
        )
      )
    ),
    @ApiResponse(
      responseCode = "404",
      description = "Tipo de evento não encontrado no Cal.com",
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = ApiErrorResponse.class),
        examples = @ExampleObject(
          name = "Não encontrado",
          value = "{\"status\": 404, \"error\": \"Not Found\", \"message\": \"EventType não encontrado no Cal.com: 999\", \"path\": \"/event-types/999\", \"timestamp\": \"2026-03-07T14:30:00Z\"}"
        )
      )
    ),
    @ApiResponse(
      responseCode = "401",
      description = "Token JWT ausente ou inválido",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
    )
  })
  ResponseEntity<EventTypeOutput> getById(
    @Parameter(description = "ID do tipo de evento no Cal.com", example = "123", required = true)
    @PathVariable Long eventTypeId
  );

  @Operation(
    summary = "Listar todos os tipos de evento",
    description = "Retorna a lista completa de tipos de evento cadastrados no Cal.com para o usuário autenticado. "
      + "Caso não haja nenhum tipo de evento, retorna uma **lista vazia** com status **200**."
  )
  @ApiResponses({
    @ApiResponse(
      responseCode = "200",
      description = "Lista de tipos de evento retornada com sucesso (pode estar vazia)",
      content = @Content(
        mediaType = "application/json",
        array = @ArraySchema(schema = @Schema(implementation = EventTypeOutput.class)),
        examples = {
          @ExampleObject(
            name = "Lista com resultados",
            value = "[{\"id\": 123, \"title\": \"Visita ao Empreendimento Parque das Flores\", \"slug\": \"visita-ao-empreendimento-parque-das-flores\", \"description\": \"Visita presencial\", \"lengthInMinutes\": 60, \"minimumBookingNotice\": 120, \"hidden\": false, \"estateId\": null}, {\"id\": 456, \"title\": \"Tour Virtual Residencial Primavera\", \"slug\": \"tour-virtual-residencial-primavera\", \"description\": null, \"lengthInMinutes\": 60, \"minimumBookingNotice\": 120, \"hidden\": false, \"estateId\": null}]"
          ),
          @ExampleObject(
            name = "Lista vazia",
            value = "[]"
          )
        }
      )
    ),
    @ApiResponse(
      responseCode = "401",
      description = "Token JWT ausente ou inválido",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
    )
  })
  ResponseEntity<List<EventTypeOutput>> listAll();

  @Operation(
    summary = "Atualizar tipo de evento",
    description = "Atualiza parcialmente um tipo de evento existente. "
      + "Campos alteráveis: **title**, **description**, **lengthInMinutes** e **minimumBookingNotice**. "
      + "Apenas os campos informados serão atualizados. "
      + "O tipo de evento é atualizado tanto no banco de dados local quanto na API do Cal.com."
  )
  @ApiResponses({
    @ApiResponse(
      responseCode = "200",
      description = "Tipo de evento atualizado com sucesso",
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = EventTypeOutput.class),
        examples = @ExampleObject(
          name = "Exemplo de resposta",
          value = "{\"id\": 123, \"title\": \"Visita ao Empreendimento Parque das Flores\", \"slug\": \"visita-ao-empreendimento-parque-das-flores\", \"description\": \"Descrição atualizada\", \"lengthInMinutes\": 60, \"minimumBookingNotice\": 120, \"hidden\": false, \"estateId\": 42}"
        )
      )
    ),
    @ApiResponse(
      responseCode = "404",
      description = "Tipo de evento não encontrado no banco de dados local",
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = ApiErrorResponse.class),
        examples = @ExampleObject(
          name = "Não encontrado",
          value = "{\"status\": 404, \"error\": \"Not Found\", \"message\": \"EventType não encontrado: 999\", \"path\": \"/event-types/999\", \"timestamp\": \"2026-03-07T14:30:00Z\"}"
        )
      )
    ),
    @ApiResponse(
      responseCode = "422",
      description = "Erro de validação nos campos enviados",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
    ),
    @ApiResponse(
      responseCode = "401",
      description = "Token JWT ausente ou inválido",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
    ),
    @ApiResponse(
      responseCode = "500",
      description = "Erro interno — falha na comunicação com o Cal.com",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
    )
  })
  ResponseEntity<EventTypeOutput> update(
    @Parameter(description = "ID do tipo de evento a ser atualizado", example = "123", required = true)
    @PathVariable Long eventTypeId,
    @Valid @RequestBody UpdateEventTypeRequest request
  );

  @Operation(
    summary = "Excluir tipo de evento",
    description = "Remove um tipo de evento da plataforma Cal.com. "
      + "Após a exclusão, o tipo de evento não estará mais disponível para agendamentos. "
      + "Retorna **204 No Content** em caso de sucesso."
  )
  @ApiResponses({
    @ApiResponse(
      responseCode = "204",
      description = "Tipo de evento excluído com sucesso",
      content = @Content
    ),
    @ApiResponse(
      responseCode = "404",
      description = "Tipo de evento não encontrado",
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = ApiErrorResponse.class)
      )
    ),
    @ApiResponse(
      responseCode = "401",
      description = "Token JWT ausente ou inválido",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
    ),
    @ApiResponse(
      responseCode = "500",
      description = "Erro interno — falha na comunicação com o Cal.com",
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = ApiErrorResponse.class),
        examples = @ExampleObject(
          name = "Erro de exclusão",
          value = "{\"status\": 500, \"error\": \"Internal Server Error\", \"message\": \"Falha ao deletar EventType 123\", \"path\": \"/event-types/123\", \"timestamp\": \"2026-03-07T14:30:00Z\"}"
        )
      )
    )
  })
  ResponseEntity<Void> delete(
    @Parameter(description = "ID do tipo de evento a ser excluído", example = "123", required = true)
    @PathVariable Long eventTypeId
  );

  @Operation(
    summary = "Alternar visibilidade do tipo de evento",
    description = "Alterna o estado de visibilidade (hidden) do tipo de evento. "
      + "Se o evento está visível, será ocultado; se está oculto, será reativado. "
      + "A alteração é sincronizada com o Cal.com e persistida no banco de dados local."
  )
  @ApiResponses({
    @ApiResponse(
      responseCode = "200",
      description = "Visibilidade alternada com sucesso",
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = EventTypeOutput.class),
        examples = @ExampleObject(
          name = "Evento ocultado",
          value = "{\"id\": 123, \"title\": \"Visita ao Empreendimento Parque das Flores\", \"slug\": \"visita-ao-empreendimento-parque-das-flores\", \"description\": \"Visita presencial\", \"lengthInMinutes\": 60, \"minimumBookingNotice\": 120, \"hidden\": true, \"estateId\": 42}"
        )
      )
    ),
    @ApiResponse(
      responseCode = "404",
      description = "Tipo de evento não encontrado no banco de dados local",
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = ApiErrorResponse.class)
      )
    ),
    @ApiResponse(
      responseCode = "401",
      description = "Token JWT ausente ou inválido",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
    ),
    @ApiResponse(
      responseCode = "500",
      description = "Erro interno — falha na comunicação com o Cal.com",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
    )
  })
  ResponseEntity<EventTypeOutput> toggleVisibility(
    @Parameter(description = "ID do tipo de evento cuja visibilidade será alternada", example = "123", required = true)
    @PathVariable Long eventTypeId
  );
}
