package com.penelopec.calservice.appointment.domain.error;

import com.penelopec.calservice.shared.error.core.ErrorContract;
import com.penelopec.calservice.shared.error.core.ErrorSeverity;
import com.penelopec.calservice.shared.error.core.ErrorType;

public enum AppointmentError implements ErrorContract {

    NOT_FOUND(
        "APT-NOT-FOUND",
        "Agendamento não encontrado: %s.",
        ErrorType.DOMAIN,
        ErrorSeverity.WARN
    ),

    BOOKING_CREATE_FAILED(
        "APT-BOOKING-CREATE-FAILED",
        "Falha ao criar booking no serviço externo.",
        ErrorType.GATEWAY,
        ErrorSeverity.ERROR
    ),

    BOOKING_RESCHEDULE_FAILED(
        "APT-BOOKING-RESCHEDULE-FAILED",
        "Falha ao reagendar booking no serviço externo.",
        ErrorType.GATEWAY,
        ErrorSeverity.ERROR
    ),

    BOOKING_CANCEL_FAILED(
        "APT-BOOKING-CANCEL-FAILED",
        "Falha ao cancelar booking no serviço externo.",
        ErrorType.GATEWAY,
        ErrorSeverity.ERROR
    ),

    BOOKING_FETCH_FAILED(
        "APT-BOOKING-FETCH-FAILED",
        "Falha ao buscar booking no serviço externo.",
        ErrorType.GATEWAY,
        ErrorSeverity.ERROR
    ),

    INVALID_STATUS_TRANSITION(
        "APT-INVALID-STATUS-TRANSITION",
        "Transição de status inválida: operação não permitida para o status '%s'.",
        ErrorType.DOMAIN,
        ErrorSeverity.WARN
    ),

    SCHEDULE_CONFLICT(
        "APT-SCHEDULE-CONFLICT",
        "Já existe um agendamento ativo para este corretor na data e horário informados.",
        ErrorType.DOMAIN,
        ErrorSeverity.WARN
    ),

    INVALID_DATES(
        "APT-INVALID-DATES",
        "Data fim deve ser posterior à data início.",
        ErrorType.DOMAIN,
        ErrorSeverity.WARN
    ),

    MISSING_DATETIMES(
        "APT-MISSING-DATETIMES",
        "Datas de início e fim são obrigatórias.",
        ErrorType.DOMAIN,
        ErrorSeverity.WARN
    ),

    MISSING_BOOKING_UID(
        "APT-MISSING-BOOKING-UID",
        "Agendamento id=%s não possui bookingUid — não é possível realizar a operação remota.",
        ErrorType.APPLICATION,
        ErrorSeverity.ERROR
    ),

    APPOINTMENT_ID_REQUIRED(
        "APPT-VAL-APPOINTMENT-ID-REQUIRED",
        "ID do agendamento é obrigatório.",
        ErrorType.VALIDATION,
        ErrorSeverity.WARN
    ),

    EVENT_TYPE_ID_REQUIRED(
        "APPT-VAL-EVENT-TYPE-ID-REQUIRED",
        "ID do tipo de evento é obrigatório.",
        ErrorType.VALIDATION,
        ErrorSeverity.WARN
    ),

    START_DATETIME_REQUIRED(
        "APPT-VAL-START-DT-REQUIRED",
        "Data/hora de início é obrigatória.",
        ErrorType.VALIDATION,
        ErrorSeverity.WARN
    ),

    START_DATETIME_INVALID(
        "APPT-VAL-START-DT-INVALID",
        "Formato de data/hora inválido para startDateTime. Use ISO-8601 (ex: 2026-04-19T10:00:00).",
        ErrorType.VALIDATION,
        ErrorSeverity.WARN
    ),

    END_DATETIME_REQUIRED(
        "APPT-VAL-END-DT-REQUIRED",
        "Data/hora de fim é obrigatória.",
        ErrorType.VALIDATION,
        ErrorSeverity.WARN
    ),

    END_DATETIME_INVALID(
        "APPT-VAL-END-DT-INVALID",
        "Formato de data/hora inválido para endDateTime. Use ISO-8601 (ex: 2026-04-19T11:00:00).",
        ErrorType.VALIDATION,
        ErrorSeverity.WARN
    ),

    ATTENDEE_NAME_REQUIRED(
        "APPT-VAL-ATTENDEE-NAME-REQUIRED",
        "Nome do participante é obrigatório.",
        ErrorType.VALIDATION,
        ErrorSeverity.WARN
    ),

    ATTENDEE_EMAIL_REQUIRED(
        "APPT-VAL-ATTENDEE-EMAIL-REQUIRED",
        "E-mail do participante é obrigatório.",
        ErrorType.VALIDATION,
        ErrorSeverity.WARN
    ),

    LIST_PAGE_INVALID(
        "APPT-VAL-PAGE-INVALID",
        "page deve ser maior ou igual a zero.",
        ErrorType.VALIDATION,
        ErrorSeverity.WARN
    ),

    LIST_SIZE_INVALID(
        "APPT-VAL-SIZE-INVALID",
        "size deve ser entre 1 e 100.",
        ErrorType.VALIDATION,
        ErrorSeverity.WARN
    ),

    LIST_STATUS_INVALID(
        "APPT-VAL-STATUS-INVALID",
        "status inválido: '%s'.",
        ErrorType.VALIDATION,
        ErrorSeverity.WARN
    ),

    LIST_START_DATETIME_INVALID(
        "APPT-VAL-LIST-START-DT-INVALID",
        "Formato de data/hora inválido para startDateTime. Use ISO-8601 (ex: 2026-04-19T10:00:00).",
        ErrorType.VALIDATION,
        ErrorSeverity.WARN
    ),

    LIST_END_DATETIME_INVALID(
        "APPT-VAL-LIST-END-DT-INVALID",
        "Formato de data/hora inválido para endDateTime. Use ISO-8601 (ex: 2026-04-19T11:00:00).",
        ErrorType.VALIDATION,
        ErrorSeverity.WARN
    );

    private final String code;
    private final String messageTemplate;
    private final ErrorType type;
    private final ErrorSeverity severity;

    AppointmentError(String code, String messageTemplate, ErrorType type, ErrorSeverity severity) {
        this.code = code;
        this.messageTemplate = messageTemplate;
        this.type = type;
        this.severity = severity;
    }

    @Override public String code()            { return code; }
    @Override public String messageTemplate() { return messageTemplate; }
    @Override public ErrorType type()         { return type; }
    @Override public ErrorSeverity severity() { return severity; }
}
