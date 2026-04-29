package com.penelopec.calservice.shared.error.http;

import com.penelopec.calservice.shared.error.core.ErrorContract;
import org.springframework.http.HttpStatus;

import java.util.Objects;

/**
 * Resolve o status HTTP de um {@link ErrorContract} consultando o {@link ErrorHttpStatusMapping}.
 * Único ponto de acesso ao mapeamento — controllers e handlers não interagem com o registry diretamente.
 */
public final class HttpStatusResolver {

  private HttpStatusResolver() {}

  public static HttpStatus resolve(ErrorContract error) {
    if (error == null) {
      return HttpStatus.INTERNAL_SERVER_ERROR;
    }
    return ErrorHttpStatusMapping.resolve(error.code());
  }

  public static HttpStatus resolve(int statusCode) {
    return Objects.requireNonNullElse(
      HttpStatus.resolve(statusCode),
      HttpStatus.INTERNAL_SERVER_ERROR
    );
  }
}
