package com.penelopec.calservice.eventtype.application.output;

public record EventTypeOutput(
  Long id,
  String title,
  String slug,
  String description,
  int lengthInMinutes,
  int minimumBookingNotice,
  boolean hidden,
  Long estateId
) {
}