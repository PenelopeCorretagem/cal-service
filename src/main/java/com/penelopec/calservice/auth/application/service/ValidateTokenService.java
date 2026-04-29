package com.penelopec.calservice.auth.application.service;

import com.penelopec.calservice.auth.application.command.ValidateTokenCommand;
import com.penelopec.calservice.auth.application.output.ValidateTokenOutput;
import com.penelopec.calservice.auth.application.port.in.ValidateTokenUseCase;
import com.penelopec.calservice.auth.domain.gateway.AuthGateway;

public class ValidateTokenService implements ValidateTokenUseCase {

  private final AuthGateway authGateway;

  public ValidateTokenService(AuthGateway authGateway) {
    this.authGateway = authGateway;
  }

  @Override
  public ValidateTokenOutput execute(ValidateTokenCommand command) {
    return authGateway.validateToken(command.token());
  }
}
