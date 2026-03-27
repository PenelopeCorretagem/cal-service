package com.penelopec.calservice.eventtype.infrastructure.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Payload para criação de um novo tipo de evento")
public record CreateEventTypeRequest(

  @Schema(
    description = "Título do tipo de evento — será exibido na página de agendamento",
    example = "Visita ao Empreendimento Parque das Flores",
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  @NotBlank(message = "Título é obrigatório")
  @Size(min = 3, max = 100, message = "Título deve ter entre 3 e 100 caracteres")
  String title,

  @Schema(
    description = "Descrição detalhada do tipo de evento (opcional)",
    example = "Agendamento de visita presencial ao empreendimento para conhecer as unidades disponíveis",
    requiredMode = Schema.RequiredMode.NOT_REQUIRED
  )
  @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
  String description,

  @Schema(
    description = "Duração do evento em minutos (padrão: 60)",
    example = "30",
    requiredMode = Schema.RequiredMode.NOT_REQUIRED
  )
  @Positive(message = "Duração deve ser maior que zero")
  Integer lengthInMinutes,

  @Schema(
    description = "Antecedência mínima para agendamento em minutos (padrão: 120)",
    example = "60",
    requiredMode = Schema.RequiredMode.NOT_REQUIRED
  )
  @PositiveOrZero(message = "Antecedência mínima não pode ser negativa")
  Integer minimumBookingNotice,

  @Schema(
    description = "Se o tipo de evento deve ser criado como oculto (padrão: false)",
    example = "false",
    requiredMode = Schema.RequiredMode.NOT_REQUIRED
  )
  Boolean hidden,

  @Schema(
    description = "ID do imóvel/empreendimento vinculado a este tipo de evento",
    example = "42",
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  @NotNull(message = "ID do imóvel é obrigatório")
  Long estateId
) {
}
