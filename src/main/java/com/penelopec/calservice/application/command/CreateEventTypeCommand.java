package com.penelopec.calservice.application.command;

public record CreateEventTypeCommand(
    String title,
    String description,
    Integer lengthInMinutes,
    Integer minimumBookingNotice,
    Boolean hidden,
    Long estateId
) {
    public CreateEventTypeCommand {
        if (title == null || title.isBlank())
            throw new IllegalArgumentException("Título é obrigatório");
        if (estateId == null)
            throw new IllegalArgumentException("ID do imóvel é obrigatório");
    }
}
