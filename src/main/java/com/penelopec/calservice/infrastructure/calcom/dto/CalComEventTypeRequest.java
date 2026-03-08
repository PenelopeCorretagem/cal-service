package com.penelopec.calservice.infrastructure.calcom.dto;

public record CalComEventTypeRequest(
    String title,
    String slug,
    Integer lengthInMinutes,
    String description,
    Boolean hidden,
    Integer minimumBookingNotice,
    Boolean requiresConfirmation
) {}
