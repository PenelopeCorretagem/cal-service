package com.penelopec.calservice.appointment.domain.gateway;

import java.util.List;
import java.util.Map;

public interface CalComScheduleGateway {

  List<ScheduleResult> getSchedules();

  Map<String, List<SlotResult>> getAvailableSlots(AvailableSlotsRequest request);

  record ScheduleResult(
    Long id,
    String name,
    String timeZone,
    List<AvailabilityRule> availability,
    boolean isDefault,
    List<OverrideRule> overrides
  ) {}

  record AvailabilityRule(
    List<String> days,
    String startTime,
    String endTime
  ) {}

  record OverrideRule(
    String date,
    String startTime,
    String endTime
  ) {}

  record AvailableSlotsRequest(
    Long eventTypeId,
    String start,
    String end,
    String timeZone
  ) {}

  record SlotResult(
    String start
  ) {}
}
