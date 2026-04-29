package com.penelopec.calservice.auth.application.command;

public record ValidateTokenCommand(String token) {

  public ValidateTokenCommand {
    if (token == null || token.isBlank()) {
      throw new IllegalArgumentException("Token é obrigatório");
    }
  }
}
