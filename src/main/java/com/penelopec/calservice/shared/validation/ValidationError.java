package com.penelopec.calservice.shared.validation;

import com.penelopec.calservice.shared.error.core.ErrorContract;

/**
 * Representa um único erro de validação.
 * <p>
 * @param field campo inválido; null para erros globais (regra entre vários campos).
 * @param message mensagem legível por humanos, derivada do ErrorContract.
 * @param code código de erro para internacionalização / frontend (string do enum).
 */
public record ValidationError(
  String field,
  String message,
  String code
) {

  public static ValidationError of(String field, ErrorContract error) {
    return new ValidationError(field, error.messageTemplate(), error.code());
  }

  public static ValidationError of(String field, ErrorContract error, Object... args) {
    return new ValidationError(field, error.format(args), error.code());
  }

  public static ValidationError global(ErrorContract error) {
    return new ValidationError(null, error.messageTemplate(), error.code());
  }

  public static ValidationError global(ErrorContract error, Object... args) {
    return new ValidationError(null, error.format(args), error.code());
  }
}