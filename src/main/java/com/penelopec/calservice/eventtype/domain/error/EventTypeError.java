package com.penelopec.calservice.eventtype.domain.error;

import com.penelopec.calservice.shared.error.core.ErrorContract;
import com.penelopec.calservice.shared.error.core.ErrorSeverity;
import com.penelopec.calservice.shared.error.core.ErrorType;

public enum EventTypeError implements ErrorContract {

    NOT_FOUND(
        "ET-NOT-FOUND",
        "EventType não encontrado: %s.",
        ErrorType.DOMAIN,
        ErrorSeverity.WARN
    ),

    CREATION_FAILED(
        "ET-CREATION-FAILED",
        "Falha ao criar EventType no serviço externo.",
        ErrorType.GATEWAY,
        ErrorSeverity.ERROR
    ),

    UPDATE_FAILED(
        "ET-UPDATE-FAILED",
        "Falha ao atualizar EventType no serviço externo.",
        ErrorType.GATEWAY,
        ErrorSeverity.ERROR
    ),

    DELETION_FAILED(
        "ET-DELETION-FAILED",
        "Falha ao deletar EventType no serviço externo.",
        ErrorType.GATEWAY,
        ErrorSeverity.ERROR
    ),

    INTEGRATION_UNAVAILABLE(
        "ET-INTEGRATION-UNAVAILABLE",
        "Serviço externo de agendamentos indisponível.",
        ErrorType.GATEWAY,
        ErrorSeverity.ERROR
    ),

    EXTERNAL_USER_FETCH_FAILED(
        "ET-USER-FETCH-FAILED",
        "Não foi possível obter o usuário autenticado do serviço externo.",
        ErrorType.GATEWAY,
        ErrorSeverity.ERROR
    ),

    SYNC_FAILED(
        "ET-SYNC-FAILED",
        "Falha na sincronização de EventTypes.",
        ErrorType.GATEWAY,
        ErrorSeverity.ERROR
    ),

    INVALID_TITLE(
        "ET-INVALID-TITLE",
        "Título do EventType não pode ser vazio.",
        ErrorType.DOMAIN,
        ErrorSeverity.WARN
    ),

    INVALID_DURATION(
        "ET-INVALID-DURATION",
        "Duração do evento deve ser maior que zero.",
        ErrorType.DOMAIN,
        ErrorSeverity.WARN
    ),

    INVALID_BOOKING_NOTICE(
        "ET-INVALID-BOOKING-NOTICE",
        "Antecedência mínima não pode ser negativa.",
        ErrorType.DOMAIN,
        ErrorSeverity.WARN
    ),

    INVALID_EXTERNAL_ID(
        "ET-INVALID-EXTERNAL-ID",
        "ID externo inválido ou EventType já possui ID atribuído.",
        ErrorType.DOMAIN,
        ErrorSeverity.WARN
    ),

    ESTATE_ID_REQUIRED(
        "ET-ESTATE-ID-REQUIRED",
        "ID do imóvel não pode ser nulo.",
        ErrorType.DOMAIN,
        ErrorSeverity.WARN
    ),

    VALIDATION_TITLE_REQUIRED(
        "ET-VAL-TITLE-REQUIRED",
        "Título é obrigatório.",
        ErrorType.VALIDATION,
        ErrorSeverity.WARN
    ),

    VALIDATION_ESTATE_ID_REQUIRED(
        "ET-VAL-ESTATE-ID-REQUIRED",
        "estateId é obrigatório.",
        ErrorType.VALIDATION,
        ErrorSeverity.WARN
    ),

    VALIDATION_EVENT_TYPE_ID_REQUIRED(
        "ET-VAL-EVENT-TYPE-ID-REQUIRED",
        "eventTypeId é obrigatório.",
        ErrorType.VALIDATION,
        ErrorSeverity.WARN
    ),

    VALIDATION_ESTATE_CHANGED_ID_REQUIRED(
        "ET-VAL-ESTATE-CHANGED-ID-REQUIRED",
        "estateId é obrigatório para processar evento de imóvel.",
        ErrorType.VALIDATION,
        ErrorSeverity.WARN
    );

    private final String code;
    private final String messageTemplate;
    private final ErrorType type;
    private final ErrorSeverity severity;

    EventTypeError(String code, String messageTemplate, ErrorType type, ErrorSeverity severity) {
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
