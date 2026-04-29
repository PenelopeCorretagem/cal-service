package com.penelopec.calservice.appointment.application.command;

public record AppointmentCommand(
    Long eventTypeId,
    Long clientId,
    Long estateAgentId,
    String startDateTime,
    String attendeeName,
    String attendeeEmail,
    String notes) { }