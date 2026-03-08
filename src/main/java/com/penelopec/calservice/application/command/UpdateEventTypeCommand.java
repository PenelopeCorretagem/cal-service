package com.penelopec.calservice.application.command;

public record UpdateEventTypeCommand(
    Long eventTypeId,
    String title,
    String description,
    Integer lengthInMinutes,
    Integer minimumBookingNotice
) {
    public UpdateEventTypeCommand {
        if (eventTypeId == null)
            throw new IllegalArgumentException("ID do EventType é obrigatório");
    }
}
