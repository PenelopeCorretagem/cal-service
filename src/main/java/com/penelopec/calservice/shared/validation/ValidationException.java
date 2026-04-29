package com.penelopec.calservice.shared.validation;

import java.util.List;

/**
 * Exceção de validação da camada de Application.
 * Carrega a lista completa de erros — nunca apenas o primeiro.
 * Lançada por CommandValidator e QueryValidator ao final da validação.
 * Mapeada para 422 Unprocessable Entity no GlobalExceptionHandler.
 */
public class ValidationException extends RuntimeException {

  private final List<ValidationError> errors;

  public ValidationException(List<ValidationError> errors) {
    super("Validação falhou: " + errors.size() + " erro(s)");
    this.errors = List.copyOf(errors);
  }

  public List<ValidationError> getErrors() {
    return errors;
  }
}
