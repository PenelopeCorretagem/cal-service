package com.penelopec.calservice.eventtype.domain.error;

import com.penelopec.calservice.shared.error.core.ErrorContract;
import com.penelopec.calservice.shared.error.core.ErrorSeverity;
import com.penelopec.calservice.shared.error.core.ErrorType;

public enum EventTypeValidationCode implements ErrorContract {

  TITLE_REQUIRED(
    "ET-VAL-TITLE-REQUIRED",
    "Título do EventType é obrigatório."
  ),

  ESTATE_ID_REQUIRED(
    "ET-VAL-ESTATE-ID-REQUIRED",
    "ID do imóvel é obrigatório."
  ),

  EVENT_TYPE_ID_REQUIRED(
    "ET-VAL-EVENT-TYPE-ID-REQUIRED",
    "ID do EventType é obrigatório."
  ),

  ESTATE_CHANGED_ID_REQUIRED(
    "ET-VAL-ESTATE-CHANGED-ID-REQUIRED",
    "ID do imóvel (estateId) é obrigatório para processar a alteração."
  );

  private final String code;
  private final String messageTemplate;

  EventTypeValidationCode(String code, String messageTemplate) {
    this.code = code;
    this.messageTemplate = messageTemplate;
  }

  @Override public String code()            { return code; }
  @Override public String messageTemplate() { return messageTemplate; }
  @Override public ErrorType type()         { return ErrorType.VALIDATION; }
  @Override public ErrorSeverity severity() { return ErrorSeverity.WARN; }
}
