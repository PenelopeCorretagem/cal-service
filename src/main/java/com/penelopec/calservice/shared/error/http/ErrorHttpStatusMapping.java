package com.penelopec.calservice.shared.error.http;

import org.springframework.http.HttpStatus;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry de mapeamento entre códigos de erro semânticos e status HTTP.
 * Não importa nenhum enum de domínio — é completamente portável.
 * <p>
 * Cada bounded context registra seus mapeamentos via um {@code @Component}
 * registrar na sua própria camada de infraestrutura.
 * Os erros do Core são registrados por {@link CoreHttpStatusRegister}.
 * <p>
 * Fallback: código não registrado retorna 500 INTERNAL_SERVER_ERROR.
 */
public final class ErrorHttpStatusMapping {

  private ErrorHttpStatusMapping() {}

  private static final ConcurrentHashMap<String, HttpStatus> REGISTRY = new ConcurrentHashMap<>();

  public static void register(String code, HttpStatus status) {
    if (code == null || code.isBlank()) {
      throw new IllegalArgumentException("Error code must not be blank");
    }
    if (status == null) {
      throw new IllegalArgumentException("HttpStatus must not be null");
    }

    HttpStatus existing = REGISTRY.putIfAbsent(code, status);
    if (existing != null && existing != status) {
      throw new IllegalStateException(
        "HTTP status mapping already registered for code '" + code
          + "' (existing=" + existing.value() + ", attempted=" + status.value() + ")"
      );
    }
  }

  public static HttpStatus resolve(String errorCode) {
    if (errorCode == null || errorCode.isBlank()) {
      return HttpStatus.INTERNAL_SERVER_ERROR;
    }
    return REGISTRY.getOrDefault(errorCode, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
