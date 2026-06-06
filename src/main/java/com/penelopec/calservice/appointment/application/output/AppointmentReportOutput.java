package com.penelopec.calservice.appointment.application.output;

import java.time.LocalDateTime;

public record AppointmentReportOutput(
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
    String attendeeName,
    String attendeeEmail,
    String notes,
    String reason,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String estateTitle,
    String estateType,
    String eventTypeTitle,
    ClientInfo client,
    EstateAgentInfo estateAgent,
    EstateInfo estate,
    EventTypeInfo eventType
) {

    public record ClientInfo(Long id, String name, String email) {}

    public record EstateAgentInfo(Long id, String name, String creci) {}

    public record EstateInfo(Long id, String title, String type) {}

    public record EventTypeInfo(Long id, String title) {}
}
