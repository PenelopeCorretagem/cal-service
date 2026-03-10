package com.penelopec.calservice.application.output;

public record EventTypeOutput(
    Long id,
    String title,
    String slug,
    String description,
    int lengthInMinutes,
    int minimumBookingNotice,
    boolean hidden,
    Long estateId
) {}