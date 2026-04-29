package com.penelopec.calservice.auth.infrastructure.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record ValidateTokenRequest(@NotBlank(message = "Token é obrigatório") String token) {
}
