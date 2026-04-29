package com.penelopec.calservice.eventtype.infrastructure.web.calcom.dto;

import java.time.OffsetDateTime;

public record CalComEventTypeResponse(
  Long id,
  String title,
  String slug,
  Integer lengthInMinutes,
  String description,
  Boolean hidden,
  Integer minimumBookingNotice,
  OffsetDateTime createdAt,
  OffsetDateTime updatedAt
) {
}
