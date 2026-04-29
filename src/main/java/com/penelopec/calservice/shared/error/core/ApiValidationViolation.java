package com.penelopec.calservice.shared.error.core;

/**
 * Violação de validação no contrato de wire.
 * Formato único para erros de Bean Validation (@Valid) e ApplicationValidationException.
 * <p>
 * field   — campo inválido; null para erros globais.
 * message — mensagem legível por humanos.
 * code    — código tipado para i18n / frontend (ex: "ET-VAL-400-TITLE").
 */
public record ApiValidationViolation(
    String field,
    String message,
    String code
) { }
