package com.penelopec.calservice.appointment.application.service;

import com.penelopec.calservice.appointment.application.output.ScheduleOutput;
import com.penelopec.calservice.appointment.application.output.ScheduleOutput.AvailabilityRuleOutput;
import com.penelopec.calservice.appointment.application.output.ScheduleOutput.OverrideRuleOutput;
import com.penelopec.calservice.appointment.application.usecase.GetSchedulesUseCase;
import com.penelopec.calservice.appointment.domain.gateway.CalComScheduleGateway;
import com.penelopec.calservice.appointment.domain.gateway.CalComScheduleGateway.ScheduleResult;
import com.penelopec.calservice.shared.cache.CacheNames;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

public class GetSchedulesService implements GetSchedulesUseCase {

  private final CalComScheduleGateway scheduleGateway;

  public GetSchedulesService(CalComScheduleGateway scheduleGateway) {
    this.scheduleGateway = scheduleGateway;
  }

  @Override
  @Cacheable(value = CacheNames.SCHEDULES, key = "'all'")
  public List<ScheduleOutput> execute() {
    return scheduleGateway.getSchedules().stream()
      .map(this::toOutput)
      .toList();
  }

  private ScheduleOutput toOutput(ScheduleResult result) {
    List<AvailabilityRuleOutput> availability = result.availability().stream()
      .map(a -> new AvailabilityRuleOutput(a.days(), a.startTime(), a.endTime()))
      .toList();

    List<OverrideRuleOutput> overrides = result.overrides().stream()
      .map(o -> new OverrideRuleOutput(o.date(), o.startTime(), o.endTime()))
      .toList();

    return new ScheduleOutput(
      result.id(),
      result.name(),
      result.timeZone(),
      availability,
      result.isDefault(),
      overrides
    );
  }
}
