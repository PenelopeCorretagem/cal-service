package com.penelopec.calservice.appointment.application.service;

import com.penelopec.calservice.appointment.application.output.AvailableSlotsOutput;
import com.penelopec.calservice.appointment.application.query.GetAvailableSlotsQuery;
import com.penelopec.calservice.appointment.application.usecase.GetAvailableSlotsUseCase;
import com.penelopec.calservice.appointment.domain.gateway.CalComScheduleGateway;
import com.penelopec.calservice.appointment.domain.gateway.CalComScheduleGateway.AvailableSlotsRequest;
import com.penelopec.calservice.appointment.domain.gateway.CalComScheduleGateway.SlotResult;
import com.penelopec.calservice.shared.cache.CacheNames;
import org.springframework.cache.annotation.Cacheable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GetAvailableSlotsService implements GetAvailableSlotsUseCase {

  private static final String DEFAULT_TIMEZONE = "America/Sao_Paulo";

  private final CalComScheduleGateway scheduleGateway;

  public GetAvailableSlotsService(CalComScheduleGateway scheduleGateway) {
    this.scheduleGateway = scheduleGateway;
  }

  @Override
  @Cacheable(value = CacheNames.AVAILABLE_SLOTS, key = "#query.eventTypeId + ':' + #query.start + ':' + #query.end")
  public AvailableSlotsOutput execute(GetAvailableSlotsQuery query) {
    var request = new AvailableSlotsRequest(
      query.eventTypeId(),
      query.start(),
      query.end(),
      DEFAULT_TIMEZONE
    );

    Map<String, List<SlotResult>> slots = scheduleGateway.getAvailableSlots(request);

    Map<String, List<String>> output = new LinkedHashMap<>();
    slots.forEach((date, slotResults) ->
      output.put(date, slotResults.stream().map(SlotResult::start).toList())
    );

    return new AvailableSlotsOutput(output);
  }
}
