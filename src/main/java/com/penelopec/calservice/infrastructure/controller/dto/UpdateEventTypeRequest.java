package com.penelopec.calservice.infrastructure.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@Schema(description = "Payload para atualização de um tipo de evento existente")
public record UpdateEventTypeRequest(

        @Schema(
                description = "Novo título do tipo de evento",
                example = "Visita ao Empreendimento Parque das Flores",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @Size(min = 3, max = 100, message = "Título deve ter entre 3 e 100 caracteres")
        String title,

        @Schema(
                description = "Nova descrição do tipo de evento",
                example = "Agendamento de visita virtual ao empreendimento via videoconferência",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
        String description,

        @Schema(
                description = "Nova duração do evento em minutos",
                example = "45",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @Positive(message = "Duração deve ser maior que zero")
        Integer lengthInMinutes,

        @Schema(
                description = "Nova antecedência mínima para agendamento em minutos",
                example = "90",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @PositiveOrZero(message = "Antecedência mínima não pode ser negativa")
        Integer minimumBookingNotice
) {}
