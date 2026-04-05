package com.penelopec.calservice.eventtype.application.port.in;

import com.penelopec.calservice.eventtype.application.command.HandleEstateChangedCommand;

public interface HandleEstateChangedUseCase {
  void execute(HandleEstateChangedCommand command);
}
