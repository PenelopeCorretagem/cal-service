package com.penelopec.calservice.shared.error.core;

public enum CoreError implements ErrorContract {

  INTERNAL_ERROR(
    "CORE-INTERNAL",
    "Erro interno inesperado.",
    ErrorSeverity.CRITICAL
  ),

  BAD_REQUEST(
    "CORE-BAD-REQUEST",
    "Requisição inválida.",
    ErrorSeverity.WARN
  ),

  VALIDATION_ERROR(
    "CORE-VALIDATION",
    "Dados inválidos na requisição.",
    ErrorSeverity.WARN
  ),

  MISSING_PARAMETER(
    "CORE-MISSING-PARAM",
    "Parâmetro obrigatório não informado.",
    ErrorSeverity.WARN
  ),

  TYPE_MISMATCH(
    "CORE-TYPE-MISMATCH",
    "Tipo de dado inválido na requisição.",
    ErrorSeverity.WARN
  ),

  UNREADABLE_PAYLOAD(
    "CORE-UNREADABLE-PAYLOAD",
    "Corpo da requisição inválido ou malformado.",
    ErrorSeverity.WARN
  ),

  UNAUTHORIZED(
    "CORE-UNAUTHORIZED",
    "Autenticação necessária.",
    ErrorSeverity.WARN
  ),

  FORBIDDEN(
    "CORE-FORBIDDEN",
    "Acesso negado.",
    ErrorSeverity.WARN
  ),

  NOT_FOUND(
    "CORE-NOT-FOUND",
    "Recurso não encontrado.",
    ErrorSeverity.WARN
  ),

  METHOD_NOT_ALLOWED(
    "CORE-METHOD-NOT-ALLOWED",
    "Método HTTP não permitido.",
    ErrorSeverity.WARN
  ),

  CONFLICT(
    "CORE-CONFLICT",
    "Conflito de estado do recurso.",
    ErrorSeverity.WARN
  ),

  UNPROCESSABLE_ENTITY(
    "CORE-UNPROCESSABLE",
    "Não foi possível processar a operação.",
    ErrorSeverity.WARN
  ),

  TOO_MANY_REQUESTS(
    "CORE-TOO-MANY-REQUESTS",
    "Muitas requisições. Tente novamente mais tarde.",
    ErrorSeverity.WARN
  ),

  SERVICE_UNAVAILABLE(
    "CORE-SERVICE-UNAVAILABLE",
    "Serviço temporariamente indisponível.",
    ErrorSeverity.ERROR
  ),

  GATEWAY_TIMEOUT(
    "CORE-GATEWAY-TIMEOUT",
    "Tempo limite excedido em serviço dependente.",
    ErrorSeverity.ERROR
  ),

  AUTH_GATEWAY_FAILED(
    "AUTH-GATEWAY-FAILED",
    "Falha ao conectar com o serviço de autenticação.",
    ErrorSeverity.ERROR
  ),

  USER_GATEWAY_FAILED(
    "USER-GATEWAY-FAILED",
    "Falha ao conectar com o serviço de usuários.",
    ErrorSeverity.ERROR
  );

  private final String code;
  private final String messageTemplate;
  private final ErrorSeverity severity;

  CoreError(String code, String messageTemplate, ErrorSeverity severity) {
    this.code = code;
    this.messageTemplate = messageTemplate;
    this.severity = severity;
  }

  @Override public String code()            { return code; }
  @Override public String messageTemplate() { return messageTemplate; }
  @Override public ErrorType type()         { return ErrorType.CORE; }
  @Override public ErrorSeverity severity() { return severity; }
}
