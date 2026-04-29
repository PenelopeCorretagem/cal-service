package com.penelopec.calservice.shared.error.core;

/**
 * Exceção da camada de Infrastructure para falhas de integração externa.
 * Lançada por adapters que se comunicam com serviços externos:
 * Cal.com, monolito, serviços de autenticação, etc.
 */
public class GatewayException extends RuntimeException {

  private final ErrorContract error;
  private final Object[] args;

  public GatewayException(ErrorContract error, Object... args) {
    super(error.format(args));
    this.error = error;
    this.args  = args;
  }

  public GatewayException(ErrorContract error, Throwable cause, Object... args) {
    super(error.format(args), cause);
    this.error = error;
    this.args  = args;
  }

  public ErrorContract error() { return error; }
  public Object[]      args()  { return args; }
}
