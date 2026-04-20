package com.penelopec.calservice.shared.error.core;

/**
 * Contrato único para todos os erros do sistema, independente da camada de origem.
 * Substitui a separação anterior entre ErrorContract (erros de negócio)
 * e ValidationCode (erros de validação). O {@link ErrorType} classifica a camada.
 */
public interface ErrorContract {

  String code();

  String messageTemplate();

  ErrorType type();

  default ErrorSeverity severity() {
    return ErrorSeverity.ERROR;
  }

  default String format(Object... args) {
    return args.length == 0
      ? messageTemplate()
      : messageTemplate().formatted(args);
  }
}
