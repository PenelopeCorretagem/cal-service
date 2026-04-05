package com.penelopec.calservice.eventtype.application.command;

public record HandleEstateChangedCommand(Long estateId, boolean hide) {

  public HandleEstateChangedCommand {
    if (estateId == null) {
      throw new IllegalArgumentException("estateId é obrigatório");
    }
  }
}
