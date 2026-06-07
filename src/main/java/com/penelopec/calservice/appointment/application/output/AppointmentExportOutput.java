package com.penelopec.calservice.appointment.application.output;

import java.time.LocalDateTime;

public record AppointmentExportOutput(
  Long id,
  String advertisementName,
  String brokerName,
  String clientName,
  String status,
  LocalDateTime startDateTime,
  LocalDateTime endDateTime,
  String attendeeName,
  String attendeeEmail,
  String notes,
  String reason,
  LocalDateTime createdAt,
  LocalDateTime updatedAt
) {
}
