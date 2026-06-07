package com.penelopec.calservice.appointment.domain.repository;

import java.time.LocalDateTime;

public record AppointmentReportRow(
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
    LocalDateTime updatedAt,
    String eventTypeTitle,
    Long estateId
) {}
