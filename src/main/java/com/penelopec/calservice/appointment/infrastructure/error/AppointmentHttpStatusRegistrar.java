package com.penelopec.calservice.appointment.infrastructure.error;

import com.penelopec.calservice.appointment.domain.error.AppointmentError;
import com.penelopec.calservice.shared.error.http.ErrorHttpStatusMapping;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * Registra os mapeamentos de {@link AppointmentError} no {@link ErrorHttpStatusMapping}.
 * Executado automaticamente pelo Spring na inicialização do contexto.
 * Co-localizado com o bounded context Appointment — o framework shared não precisa
 * conhecer nenhum erro deste domínio.
 */
@Component
public class AppointmentHttpStatusRegistrar {

  public AppointmentHttpStatusRegistrar() {
    ErrorHttpStatusMapping.register(AppointmentError.NOT_FOUND.code(),                 HttpStatus.NOT_FOUND);
    ErrorHttpStatusMapping.register(AppointmentError.BOOKING_CREATE_FAILED.code(),     HttpStatus.BAD_GATEWAY);
    ErrorHttpStatusMapping.register(AppointmentError.BOOKING_IN_PAST.code(),           HttpStatus.valueOf(422));
    ErrorHttpStatusMapping.register(AppointmentError.BOOKING_SLOT_UNAVAILABLE.code(),  HttpStatus.CONFLICT);
    ErrorHttpStatusMapping.register(AppointmentError.BOOKING_RESCHEDULE_FAILED.code(), HttpStatus.BAD_GATEWAY);
    ErrorHttpStatusMapping.register(AppointmentError.BOOKING_CANCEL_FAILED.code(),     HttpStatus.BAD_GATEWAY);
    ErrorHttpStatusMapping.register(AppointmentError.BOOKING_FETCH_FAILED.code(),      HttpStatus.BAD_GATEWAY);
    ErrorHttpStatusMapping.register(AppointmentError.SCHEDULE_FETCH_FAILED.code(),     HttpStatus.BAD_GATEWAY);
    ErrorHttpStatusMapping.register(AppointmentError.SLOTS_FETCH_FAILED.code(),        HttpStatus.BAD_GATEWAY);
    ErrorHttpStatusMapping.register(AppointmentError.INVALID_STATUS_TRANSITION.code(), HttpStatus.CONFLICT);
    ErrorHttpStatusMapping.register(AppointmentError.SCHEDULE_CONFLICT.code(),         HttpStatus.CONFLICT);
    ErrorHttpStatusMapping.register(AppointmentError.INVALID_DATES.code(),             HttpStatus.valueOf(422));
    ErrorHttpStatusMapping.register(AppointmentError.MISSING_DATETIMES.code(),         HttpStatus.valueOf(422));
    ErrorHttpStatusMapping.register(AppointmentError.MISSING_BOOKING_UID.code(),       HttpStatus.CONFLICT);
  }
}
