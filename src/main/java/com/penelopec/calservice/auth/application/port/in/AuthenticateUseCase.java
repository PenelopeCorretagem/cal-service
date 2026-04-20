package com.penelopec.calservice.auth.application.port.in;

import com.penelopec.calservice.auth.application.command.LoginCommand;
import com.penelopec.calservice.auth.application.output.LoginOutput;

public interface AuthenticateUseCase {
  LoginOutput execute(LoginCommand command);
}
