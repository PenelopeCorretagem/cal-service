package com.penelopec.calservice.appointment.application.output;

import java.util.List;

public record ScheduleOutput(
  Long id,
  String name,
  String timeZone,
  List<AvailabilityRuleOutput> availability,
  boolean isDefault,
  List<OverrideRuleOutput> overrides
) {

  public record AvailabilityRuleOutput(
    List<String> days,
    String startTime,
    String endTime
  ) {}

  public record OverrideRuleOutput(
    String date,
    String startTime,
    String endTime
  ) {}
}
