package com.penelopec.calservice.eventtype.application.command;

public record CreateEventTypeCommand(
  String title,
  String description,
  Integer lengthInMinutes,
  Integer minimumBookingNotice,
  Boolean hidden,
  Long estateId
) { }
