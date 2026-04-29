package com.penelopec.calservice.auth.application.command;

public record LoginCommand(String email, String password) {

  public LoginCommand {
    if (email == null || email.isBlank()) {
      throw new IllegalArgumentException("Email é obrigatório");
    }
    if (password == null || password.isBlank()) {
      throw new IllegalArgumentException("Senha é obrigatória");
    }
  }
}
