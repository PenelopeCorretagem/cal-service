package com.penelopec.calservice.appointment.application.validator;

import com.penelopec.calservice.appointment.application.query.ExportAppointmentsQuery;
import com.penelopec.calservice.appointment.domain.error.AppointmentError;
import com.penelopec.calservice.appointment.domain.valueobject.Status;
import com.penelopec.calservice.shared.validation.QueryValidator;
import com.penelopec.calservice.shared.validation.ValidationResult;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class ExportAppointmentsQueryValidator implements QueryValidator<ExportAppointmentsQuery> {

  @Override
  public ValidationResult validate(ExportAppointmentsQuery query) {
    var result = new ValidationResult();

    if (query.status() != null && !query.status().isBlank()) {
      String rawStatus = query.status().trim();
      try {
        Status.valueOf(rawStatus.toUpperCase());
      } catch (IllegalArgumentException e) {
        result.addError("status", AppointmentError.EXPORT_STATUS_INVALID, rawStatus);
      }
    }

    if (query.startDate() != null && !query.startDate().isBlank()) {
      result.addErrorIf(!isValidDate(query.startDate()),
        "periodoInicio", AppointmentError.EXPORT_START_DATE_INVALID);
    }

    if (query.endDate() != null && !query.endDate().isBlank()) {
      result.addErrorIf(!isValidDate(query.endDate()),
        "periodoFim", AppointmentError.EXPORT_END_DATE_INVALID);
    }

    return result;
  }

  private boolean isValidDate(String raw) {
    try {
      LocalDate.parse(raw.trim());
      return true;
    } catch (DateTimeParseException e) {
      return false;
    }
  }
}
