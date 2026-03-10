package com.penelopec.calservice.infrastructure.persistence.mapper;

import com.penelopec.calservice.domain.entity.EventType;
import com.penelopec.calservice.infrastructure.persistence.entity.EventTypeJpaEntity;

public class EventTypeJpaMapper {

    private EventTypeJpaMapper() {}

    public static EventTypeJpaEntity toJpaEntity(EventType eventType) {
        return new EventTypeJpaEntity(
                eventType.getId(),
                eventType.getTitle(),
                eventType.getSlugValue(),
                eventType.getDescription(),
                eventType.getLengthInMinutes(),
                eventType.getMinimumBookingNotice(),
                eventType.isHidden(),
                eventType.getEstateId()
        );
    }

    public static EventType toDomain(EventTypeJpaEntity entity) {
        return EventType.reconstitute(
                entity.getId(),
                entity.getTitle(),
                entity.getSlug(),
                entity.getDescription(),
                entity.getLengthInMinutes(),
                entity.getMinimumBookingNotice(),
                entity.isHidden(),
                entity.getEstateId()
        );
    }
}
