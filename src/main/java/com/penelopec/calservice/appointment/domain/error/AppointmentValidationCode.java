package com.penelopec.calservice.appointment.domain.error;

import com.penelopec.calservice.shared.error.core.ErrorContract;
import com.penelopec.calservice.shared.error.core.ErrorSeverity;
import com.penelopec.calservice.shared.error.core.ErrorType;

public enum AppointmentValidationCode implements ErrorContract {

  APPOINTMENT_ID_REQUIRED(
    "APPT-VAL-APPOINTMENT-ID-REQUIRED",
    "ID do agendamento é obrigatório."
  ),

  EVENT_TYPE_ID_REQUIRED(
    "APPT-VAL-EVENT-TYPE-ID-REQUIRED",
    "ID do tipo de evento é obrigatório."
  ),

  START_DATETIME_REQUIRED(
    "APPT-VAL-START-DT-REQUIRED",
    "Data/hora de início é obrigatória."
  ),

  START_DATETIME_INVALID(
    "APPT-VAL-START-DT-INVALID",
    "Formato de data/hora inválido para startDateTime. Use ISO-8601 (ex: 2026-04-19T10:00:00)."
  ),

  END_DATETIME_REQUIRED(
    "APPT-VAL-END-DT-REQUIRED",
    "Data/hora de fim é obrigatória."
  ),

  END_DATETIME_INVALID(
    "APPT-VAL-END-DT-INVALID",
    "Formato de data/hora inválido para endDateTime. Use ISO-8601 (ex: 2026-04-19T11:00:00)."
  ),

  ATTENDEE_NAME_REQUIRED(
    "APPT-VAL-ATTENDEE-NAME-REQUIRED",
    "Nome do participante é obrigatório."
  ),

  ATTENDEE_EMAIL_REQUIRED(
    "APPT-VAL-ATTENDEE-EMAIL-REQUIRED",
    "E-mail do participante é obrigatório."
  ),

  LIST_PAGE_INVALID(
    "APPT-VAL-PAGE-INVALID",
    "page deve ser maior ou igual a zero."
  ),

  LIST_SIZE_INVALID(
    "APPT-VAL-SIZE-INVALID",
    "size deve ser entre 1 e 100."
  ),

  LIST_STATUS_INVALID(
    "APPT-VAL-STATUS-INVALID",
    "status inválido: '%s'."
  ),

  LIST_START_DATETIME_INVALID(
    "APPT-VAL-LIST-START-DT-INVALID",
    "Formato de data/hora inválido para startDateTime. Use ISO-8601 (ex: 2026-04-19T10:00:00)."
  ),

  LIST_END_DATETIME_INVALID(
    "APPT-VAL-LIST-END-DT-INVALID",
    "Formato de data/hora inválido para endDateTime. Use ISO-8601 (ex: 2026-04-19T11:00:00)."
  );

  private final String code;
  private final String messageTemplate;

  AppointmentValidationCode(String code, String messageTemplate) {
    this.code = code;
    this.messageTemplate = messageTemplate;
  }

  @Override public String code()            { return code; }
  @Override public String messageTemplate() { return messageTemplate; }
  @Override public ErrorType type()         { return ErrorType.VALIDATION; }
  @Override public ErrorSeverity severity() { return ErrorSeverity.WARN; }
}
