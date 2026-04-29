package com.penelopec.calservice.auth.application.service;

import com.penelopec.calservice.auth.application.command.LoginCommand;
import com.penelopec.calservice.auth.application.output.LoginOutput;
import com.penelopec.calservice.auth.application.port.in.AuthenticateUseCase;
import com.penelopec.calservice.auth.domain.gateway.AuthGateway;

public class AuthenticateService implements AuthenticateUseCase {

  private final AuthGateway authGateway;

  public AuthenticateService(AuthGateway authGateway) {
    this.authGateway = authGateway;
  }

  @Override
  public LoginOutput execute(LoginCommand command) {
    return authGateway.login(command.email(), command.password());
  }
}
