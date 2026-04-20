package com.penelopec.calservice.auth.infrastructure.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta da validação do token JWT com os dados do usuário autenticado")
public record ValidateTokenResponse(
    @Schema(description = "E-mail do usuário associado ao token", example = "usuario@penelope.com.br")
    String email,

    @Schema(description = "Nível de acesso do usuário", example = "ADMIN")
    String accessLevel
) {
}

