package com.penelopec.calservice.auth.infrastructure.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta do login com token JWT e dados do usuário autenticado")
public record LoginResponse(
    @Schema(description = "Token JWT para uso nas requisições autenticadas", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    String token,

    @Schema(description = "Identificador único do usuário", example = "42")
    Long userId,

    @Schema(description = "Nível de acesso do usuário", example = "ADMIN")
    String accessLevel
) {
}
