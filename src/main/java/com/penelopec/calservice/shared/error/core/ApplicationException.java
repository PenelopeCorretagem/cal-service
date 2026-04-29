package com.penelopec.calservice.shared.error.core;

/**
 * Exceção da camada de Application para fluxos específicos de orquestração.
 * Lançada por use cases quando uma pré-condição de negócio não é satisfeita,
 * mas não constitui uma violação de invariante do domínio nem uma falha de gateway.
 * Exemplo: agendamento sem bookingUid ao tentar executar operação remota.
 */
public class ApplicationException extends RuntimeException {

  private final ErrorContract error;
  private final Object[] args;

  public ApplicationException(ErrorContract error, Object... args) {
    super(error.format(args));
    this.error = error;
    this.args  = args;
  }

  public ApplicationException(ErrorContract error, Throwable cause, Object... args) {
    super(error.format(args), cause);
    this.error = error;
    this.args  = args;
  }

  public ErrorContract error() { return error; }
  public Object[]      args()  { return args; }
}
