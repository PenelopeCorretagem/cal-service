package com.penelopec.calservice.eventtype.infrastructure.error;

import com.penelopec.calservice.eventtype.domain.error.EventTypeError;
import com.penelopec.calservice.shared.error.http.ErrorHttpStatusMapping;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * Registra os mapeamentos de {@link EventTypeError} no {@link ErrorHttpStatusMapping}.
 * Executado automaticamente pelo Spring na inicialização do contexto.
 * Co-localizado com o bounded context EventType — o framework shared não precisa
 * conhecer nenhum erro deste domínio.
 */
@Component
public class EventTypeHttpStatusRegistrar {

  public EventTypeHttpStatusRegistrar() {
    ErrorHttpStatusMapping.register(EventTypeError.NOT_FOUND.code(),                  HttpStatus.NOT_FOUND);
    ErrorHttpStatusMapping.register(EventTypeError.CREATION_FAILED.code(),            HttpStatus.BAD_GATEWAY);
    ErrorHttpStatusMapping.register(EventTypeError.UPDATE_FAILED.code(),              HttpStatus.BAD_GATEWAY);
    ErrorHttpStatusMapping.register(EventTypeError.DELETION_FAILED.code(),            HttpStatus.BAD_GATEWAY);
    ErrorHttpStatusMapping.register(EventTypeError.INTEGRATION_UNAVAILABLE.code(),    HttpStatus.BAD_GATEWAY);
    ErrorHttpStatusMapping.register(EventTypeError.EXTERNAL_USER_FETCH_FAILED.code(), HttpStatus.BAD_GATEWAY);
    ErrorHttpStatusMapping.register(EventTypeError.SYNC_FAILED.code(),                HttpStatus.INTERNAL_SERVER_ERROR);
    ErrorHttpStatusMapping.register(EventTypeError.INVALID_TITLE.code(),              HttpStatus.valueOf(422));
    ErrorHttpStatusMapping.register(EventTypeError.INVALID_DURATION.code(),           HttpStatus.valueOf(422));
    ErrorHttpStatusMapping.register(EventTypeError.INVALID_BOOKING_NOTICE.code(),     HttpStatus.valueOf(422));
    ErrorHttpStatusMapping.register(EventTypeError.INVALID_EXTERNAL_ID.code(),        HttpStatus.valueOf(422));
    ErrorHttpStatusMapping.register(EventTypeError.ESTATE_ID_REQUIRED.code(),         HttpStatus.valueOf(422));
  }
}
