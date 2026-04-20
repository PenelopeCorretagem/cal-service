package com.penelopec.calservice.shared.error.core;

/**
 * Classifica a camada de origem de um erro.
 * Substitui a necessidade de interfaces separadas (ErrorContract vs ValidationCode)
 * para distinguir o tipo de erro pelo contexto onde ele ocorre.
 */
public enum ErrorType {

  /** Erros genéricos da plataforma, não vinculados a nenhum domínio. */
  CORE,

  /** Invariantes de domínio: NOT_FOUND, transições de estado inválidas, regras de entidade. */
  DOMAIN,

  /** Falhas em integrações externas: Cal.com, monolito, serviços de autenticação. */
  GATEWAY,

  /** Validação de input: commands e queries inválidos. */
  VALIDATION,

  /** Pré-condições de orquestração da camada de aplicação não satisfeitas. */
  APPLICATION
}
