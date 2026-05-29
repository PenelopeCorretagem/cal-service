package com.penelopec.calservice.appointment.application.output;

import java.time.LocalDateTime;

public record ExportAppointmentOutput(
    Long id,
    String status,
    LocalDateTime startDateTime,
    LocalDateTime endDateTime,
    String attendeeName,
    String attendeeEmail,
    String notes,
    String reason,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String advertisementName,
    String clientName,
    String estateAgentName) {
}
