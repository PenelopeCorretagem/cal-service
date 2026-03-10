package com.penelopec.calservice.infrastructure.calcom.mapper;

import com.penelopec.calservice.domain.entity.EventType;
import com.penelopec.calservice.infrastructure.calcom.dto.CalComEventTypeRequest;
import com.penelopec.calservice.infrastructure.calcom.dto.CalComEventTypeResponse;

public class CalComEventTypeMapper {

    private CalComEventTypeMapper() {}

    public static CalComEventTypeRequest toRequest(EventType eventType, boolean hidden) {
        return new CalComEventTypeRequest(
            eventType.getTitle(),
            eventType.getSlugValue(),
            eventType.getLengthInMinutes(),
            eventType.getDescription(),
            hidden,
            eventType.getMinimumBookingNotice(),
            eventType.getRequiresConfirmation()
        );
    }

    public static EventType toDomain(CalComEventTypeResponse response, Long estateId) {
        return EventType.reconstitute(
            response.id(),
            response.title(),
            response.slug(),
            response.description(),
            response.lengthInMinutes() != null ? response.lengthInMinutes() : 60,
            response.minimumBookingNotice() != null ? response.minimumBookingNotice() : 120,
            response.hidden() != null && response.hidden(),
            estateId
        );
    }
}
