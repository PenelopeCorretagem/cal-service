package com.penelopec.calservice.appointment.application.util;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

public final class AppointmentDateTimeParser {

  private AppointmentDateTimeParser() {
  }

  public static LocalDateTime parseRequired(String rawDateTime, String fieldName) {
    if (rawDateTime == null || rawDateTime.isBlank()) {
      throw new IllegalArgumentException(fieldName + " é obrigatório");
    }

    return parseOptional(rawDateTime)
      .orElseThrow(() -> new IllegalArgumentException("data/hora inválida para " + fieldName + ": " + rawDateTime));
  }

  public static java.util.Optional<LocalDateTime> parseOptional(String rawDateTime) {
    if (rawDateTime == null || rawDateTime.isBlank()) {
      return java.util.Optional.empty();
    }

    try {
      return java.util.Optional.of(LocalDateTime.parse(rawDateTime));
    } catch (DateTimeParseException ignored) {
      try {
        return java.util.Optional.of(OffsetDateTime.parse(rawDateTime).toLocalDateTime());
      } catch (DateTimeParseException ignoredToo) {
        return java.util.Optional.empty();
      }
    }
  }
}
