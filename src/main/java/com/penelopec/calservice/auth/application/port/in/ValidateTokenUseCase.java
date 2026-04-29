package com.penelopec.calservice.auth.application.port.in;

import com.penelopec.calservice.auth.application.command.ValidateTokenCommand;
import com.penelopec.calservice.auth.application.output.ValidateTokenOutput;

public interface ValidateTokenUseCase {
  ValidateTokenOutput execute(ValidateTokenCommand command);
}
