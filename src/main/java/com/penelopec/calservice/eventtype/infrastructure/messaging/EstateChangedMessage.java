package com.penelopec.calservice.eventtype.infrastructure.messaging;

import java.time.Instant;

public record EstateChangedMessage(
        Long estateId,
        EstateStatus newStatus,
        Instant occurredAt
) {
}
