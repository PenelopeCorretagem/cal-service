package com.penelopec.calservice.eventtype.infrastructure.web.calcom.dto;

public record CalComEventTypeRequest(
  String title,
  String slug,
  Integer lengthInMinutes,
  String description,
  Boolean hidden,
  Integer minimumBookingNotice,
  Boolean requiresConfirmation
) {
}
