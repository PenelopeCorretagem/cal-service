package com.penelopec.calservice.shared.http.executor;

import com.penelopec.calservice.shared.http.exception.RemoteNotFoundException;
import com.penelopec.calservice.shared.http.exception.RemoteServiceException;
import com.penelopec.calservice.shared.http.exception.RemoteUnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;

import java.util.function.Supplier;

@Component
public class RestExecutor {

  private static final Logger log = LoggerFactory.getLogger(RestExecutor.class);

  /**
   * Executa chamada HTTP e traduz qualquer falha para exceptions padronizadas.
   *
   * @param system   nome do sistema externo (ex: "CALCOM", "VIVA_REAL")
   * @param supplier chamada RestClient encapsulada
   */
  public <T> T execute(String system, Supplier<T> supplier) {
    try {
      T result = supplier.get();

      if (result == null) {
        throw new RemoteServiceException(system, "Resposta nula inesperada", null);
      }

      return result;

    } catch (RemoteServiceException e) {
      throw e; // já tratada, só repropaga

    } catch (RestClientResponseException e) {
      log.error("[{}] Erro HTTP {}: {}", system, e.getStatusCode().value(), e.getMessage());
      throw mapHttpException(system, e);

    } catch (Exception e) {
      log.error("[{}] Erro inesperado na comunicação", system, e);
      throw new RemoteServiceException(system, "Erro inesperado na comunicação", e);
    }
  }

  /**
   * Executa chamada HTTP permitindo retorno nulo sem lançar exceção.
   * Use quando null é um retorno válido (ex: recurso opcional, lista vazia).
   *
   * @param system   nome do sistema externo
   * @param supplier chamada RestClient encapsulada
   */
  public <T> T executeOrNull(String system, Supplier<T> supplier) {
    try {
      return supplier.get();

    } catch (RemoteServiceException e) {
      throw e;

    } catch (RestClientResponseException e) {
      log.error("[{}] Erro HTTP {}: {}", system, e.getStatusCode().value(), e.getMessage());
      throw mapHttpException(system, e);

    } catch (Exception e) {
      log.error("[{}] Erro inesperado na comunicação", system, e);
      throw new RemoteServiceException(system, "Erro inesperado na comunicação", e);
    }
  }

  /**
   * Versão para chamadas sem retorno (DELETE, por exemplo).
   */
  public void executeVoid(String system, Runnable runnable) {
    execute(system, () -> {
      runnable.run();
      return Boolean.TRUE;
    });
  }

  private RemoteServiceException mapHttpException(String system, RestClientResponseException e) {
    return switch (e.getStatusCode().value()) {
      case 404 -> new RemoteNotFoundException(system, e.getMessage());
      case 401, 403 -> new RemoteUnauthorizedException(system);
      default -> new RemoteServiceException(system, e.getStatusCode().value(), e.getMessage());
    };
  }
}