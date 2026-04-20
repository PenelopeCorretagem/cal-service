package com.penelopec.calservice.shared.error.core;

/**
 * Exceção da camada de Domínio.
 * Lançada por entidades e regras de negócio do domínio:
 * invariantes de entidade, transições de estado inválidas e recursos não encontrados.
 */
public class DomainException extends RuntimeException {

  private final ErrorContract error;
  private final Object[] args;

  public DomainException(ErrorContract error, Object... args) {
    super(error.format(args));
    this.error = error;
    this.args  = args;
  }

  public DomainException(ErrorContract error, Throwable cause, Object... args) {
    super(error.format(args), cause);
    this.error = error;
    this.args  = args;
  }

  public ErrorContract error() { return error; }
  public Object[]      args()  { return args; }
}
