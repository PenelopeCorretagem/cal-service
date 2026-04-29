package com.penelopec.calservice.eventtype.infrastructure.messaging;

import java.time.Instant;

public record EstateChangedMessage(
        Long estateId,
        Long advertisementId,
        String title,
        String description,
        String slug,
        String action,
        EstateStatus newStatus,
        Instant occurredAt
) {
    public static final String ACTION_CREATED = "CREATED";
    public static final String ACTION_UPDATED = "UPDATED";
}
