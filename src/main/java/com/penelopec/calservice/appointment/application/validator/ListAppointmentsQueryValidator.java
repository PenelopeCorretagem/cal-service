package com.penelopec.calservice.appointment.application.validator;

import com.penelopec.calservice.appointment.application.query.ListAppointmentsQuery;
import com.penelopec.calservice.appointment.application.util.AppointmentDateTimeParser;
import com.penelopec.calservice.appointment.domain.error.AppointmentValidationCode;
import com.penelopec.calservice.appointment.domain.valueobject.Status;
import com.penelopec.calservice.shared.validation.QueryValidator;
import com.penelopec.calservice.shared.validation.ValidationResult;

public class ListAppointmentsQueryValidator implements QueryValidator<ListAppointmentsQuery> {

  @Override
  public ValidationResult validate(ListAppointmentsQuery query) {
    int page = query.page() == null ? 0 : query.page();
    int size = query.size() == null ? 20 : query.size();

    var result = new ValidationResult()
      .addErrorIf(page < 0,
        "page", AppointmentValidationCode.LIST_PAGE_INVALID)
      .addErrorIf(size <= 0 || size > 100,
        "size", AppointmentValidationCode.LIST_SIZE_INVALID);

    if (query.status() != null && !query.status().isBlank()) {
      try {
        Status.valueOf(query.status().trim().toUpperCase());
      } catch (IllegalArgumentException e) {
        result.addError("status", AppointmentValidationCode.LIST_STATUS_INVALID);
      }
    }

    if (query.startDateTime() != null && !query.startDateTime().isBlank()) {
      result.addErrorIf(
        AppointmentDateTimeParser.parseOptional(query.startDateTime()).isEmpty(),
        "startDateTime", AppointmentValidationCode.LIST_START_DATETIME_INVALID);
    }

    if (query.endDateTime() != null && !query.endDateTime().isBlank()) {
      result.addErrorIf(
        AppointmentDateTimeParser.parseOptional(query.endDateTime()).isEmpty(),
        "endDateTime", AppointmentValidationCode.LIST_END_DATETIME_INVALID);
    }

    return result;
  }
}
