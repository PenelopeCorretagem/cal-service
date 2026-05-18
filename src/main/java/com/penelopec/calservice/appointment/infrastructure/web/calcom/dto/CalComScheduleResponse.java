package com.penelopec.calservice.appointment.infrastructure.web.calcom.dto;

import java.util.List;

public record CalComScheduleResponse(
  Long id,
  Long ownerId,
  String name,
  String timeZone,
  List<CalComAvailability> availability,
  boolean isDefault,
  List<CalComOverride> overrides
) {

  public record CalComAvailability(
    List<String> days,
    String startTime,
    String endTime
  ) {}

  public record CalComOverride(
    String date,
    String startTime,
    String endTime
  ) {}
}
