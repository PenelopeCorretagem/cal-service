package com.penelopec.calservice.eventtype.application.command;

public record UpdateEventTypeCommand(
  Long eventTypeId,
  String title,
  String description,
  Integer lengthInMinutes,
  Integer minimumBookingNotice
) { }
