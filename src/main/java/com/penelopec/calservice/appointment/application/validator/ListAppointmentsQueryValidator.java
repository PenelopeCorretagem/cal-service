package com.penelopec.calservice.appointment.application.validator;

import com.penelopec.calservice.appointment.application.query.ListAppointmentsQuery;
import com.penelopec.calservice.appointment.application.util.AppointmentDateTimeParser;
import com.penelopec.calservice.appointment.domain.error.AppointmentError;
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
        "page", AppointmentError.LIST_PAGE_INVALID)
      .addErrorIf(size <= 0 || size > 100,
        "size", AppointmentError.LIST_SIZE_INVALID);

    if (query.status() != null && !query.status().isBlank()) {
      String rawStatus = query.status().trim();
      String normalizedStatus = rawStatus.toUpperCase();
      try {
        Status.valueOf(normalizedStatus);
      } catch (IllegalArgumentException e) {
        result.addError("status", AppointmentError.LIST_STATUS_INVALID, rawStatus);
      }
    }

    if (query.startDateTime() != null && !query.startDateTime().isBlank()) {
      result.addErrorIf(
        AppointmentDateTimeParser.parseOptional(query.startDateTime()).isEmpty(),
        "startDateTime", AppointmentError.LIST_START_DATETIME_INVALID);
    }

    if (query.endDateTime() != null && !query.endDateTime().isBlank()) {
      result.addErrorIf(
        AppointmentDateTimeParser.parseOptional(query.endDateTime()).isEmpty(),
        "endDateTime", AppointmentError.LIST_END_DATETIME_INVALID);
    }

    return result;
  }
}
