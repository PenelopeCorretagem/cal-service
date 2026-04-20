package com.penelopec.calservice.appointment.application.output;

import java.time.LocalDateTime;

public record AppointmentOutput(
    Long id,
    String bookingUid,
    Long eventTypeId,
    Long clientId,
    Long estateAgentId,
    Integer durationMinutes,
    String status,
    LocalDateTime startDateTime,
    LocalDateTime endDateTime,
    String attendeeName,
    String attendeeEmail,
    String notes,
    String reason,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) { }