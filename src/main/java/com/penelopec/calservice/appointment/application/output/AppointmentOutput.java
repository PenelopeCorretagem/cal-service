package com.penelopec.calservice.appointment.application.output;

import java.time.LocalDateTime;

public record AppointmentOutput(
    Long id,
    String bookingUid,
    Long eventTypeId,
    Long clientId,
    Long estateAgentId,
    Long estateId,
    Integer durationMinutes,
    String status,
    LocalDateTime startDateTime,
    LocalDateTime endDateTime,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) { }