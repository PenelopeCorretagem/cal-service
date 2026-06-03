package com.penelopec.calservice.appointment.application.query;

public record ReportAppointmentsQuery(
    Long clientId,
    Long estateAgentId,
    Long estateId,
    String status,
    String estateTypeKey,
    String startDateTime,
    String endDateTime,
    Integer page,
    Integer size
) {}
