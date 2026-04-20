package com.penelopec.calservice.shared.error.http;

import com.penelopec.calservice.shared.error.core.CoreError;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * Registra os mapeamentos de {@link CoreError} no {@link ErrorHttpStatusMapping}.
 * Executado automaticamente pelo Spring na inicialização do contexto.
 */
@Component
public class CoreHttpStatusRegister {

  public CoreHttpStatusRegister() {
    ErrorHttpStatusMapping.register(CoreError.INTERNAL_ERROR.code(),       HttpStatus.INTERNAL_SERVER_ERROR);
    ErrorHttpStatusMapping.register(CoreError.BAD_REQUEST.code(),          HttpStatus.BAD_REQUEST);
    ErrorHttpStatusMapping.register(CoreError.VALIDATION_ERROR.code(),     HttpStatus.BAD_REQUEST);
    ErrorHttpStatusMapping.register(CoreError.MISSING_PARAMETER.code(),    HttpStatus.BAD_REQUEST);
    ErrorHttpStatusMapping.register(CoreError.TYPE_MISMATCH.code(),        HttpStatus.BAD_REQUEST);
    ErrorHttpStatusMapping.register(CoreError.UNREADABLE_PAYLOAD.code(),   HttpStatus.BAD_REQUEST);
    ErrorHttpStatusMapping.register(CoreError.UNAUTHORIZED.code(),         HttpStatus.UNAUTHORIZED);
    ErrorHttpStatusMapping.register(CoreError.FORBIDDEN.code(),            HttpStatus.FORBIDDEN);
    ErrorHttpStatusMapping.register(CoreError.NOT_FOUND.code(),            HttpStatus.NOT_FOUND);
    ErrorHttpStatusMapping.register(CoreError.METHOD_NOT_ALLOWED.code(),   HttpStatus.METHOD_NOT_ALLOWED);
    ErrorHttpStatusMapping.register(CoreError.CONFLICT.code(),             HttpStatus.CONFLICT);
    ErrorHttpStatusMapping.register(CoreError.UNPROCESSABLE_ENTITY.code(), HttpStatus.valueOf(422));
    ErrorHttpStatusMapping.register(CoreError.TOO_MANY_REQUESTS.code(),    HttpStatus.TOO_MANY_REQUESTS);
    ErrorHttpStatusMapping.register(CoreError.SERVICE_UNAVAILABLE.code(),  HttpStatus.SERVICE_UNAVAILABLE);
    ErrorHttpStatusMapping.register(CoreError.GATEWAY_TIMEOUT.code(),      HttpStatus.GATEWAY_TIMEOUT);
    ErrorHttpStatusMapping.register(CoreError.AUTH_GATEWAY_FAILED.code(),  HttpStatus.BAD_GATEWAY);
  }
}
