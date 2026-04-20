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
