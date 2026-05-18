package com.penelopec.calservice.appointment.infrastructure.web.calcom.adapter;

import com.penelopec.calservice.appointment.domain.error.AppointmentError;
import com.penelopec.calservice.appointment.domain.gateway.CalComScheduleGateway;
import com.penelopec.calservice.appointment.infrastructure.web.calcom.dto.CalComScheduleResponse;
import com.penelopec.calservice.appointment.infrastructure.web.calcom.dto.CalComSlotEntry;
import com.penelopec.calservice.eventtype.infrastructure.web.calcom.dto.CalComApiResponse;
import com.penelopec.calservice.shared.error.core.GatewayException;
import com.penelopec.calservice.shared.http.exception.RemoteServiceException;
import com.penelopec.calservice.shared.http.executor.RestExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CalComScheduleAdapter implements CalComScheduleGateway {

  private static final Logger log = LoggerFactory.getLogger(CalComScheduleAdapter.class);
  private static final String SYSTEM = "CALCOM";
  private static final String SCHEDULES_API_VERSION = "2024-06-11";
  private static final String SLOTS_API_VERSION = "2024-09-04";

  private final RestClient restClient;
  private final RestExecutor restExecutor;

  private static final ParameterizedTypeReference<CalComApiResponse<List<CalComScheduleResponse>>>
    WRAPPER_SCHEDULES = new ParameterizedTypeReference<>() {};

  private static final ParameterizedTypeReference<CalComApiResponse<Map<String, List<CalComSlotEntry>>>>
    WRAPPER_SLOTS = new ParameterizedTypeReference<>() {};

  public CalComScheduleAdapter(RestClient restClient, RestExecutor restExecutor) {
    this.restClient = restClient;
    this.restExecutor = restExecutor;
  }

  @Override
  public List<ScheduleResult> getSchedules() {
    log.info("Buscando schedules (horários de trabalho) no Cal.com");

    try {
      List<CalComScheduleResponse> schedules = Optional.ofNullable(
          restExecutor.executeOrNull(SYSTEM, () ->
            restClient.get()
              .uri("/v2/schedules")
              .headers(h -> h.set("cal-api-version", SCHEDULES_API_VERSION))
              .retrieve()
              .body(WRAPPER_SCHEDULES)))
        .map(CalComApiResponse::data)
        .orElse(Collections.emptyList());

      log.info("Encontrados {} schedules no Cal.com", schedules.size());
      return schedules.stream().map(this::toScheduleResult).toList();
    } catch (RemoteServiceException e) {
      throw new GatewayException(AppointmentError.SCHEDULE_FETCH_FAILED, e);
    }
  }

  @Override
  public Map<String, List<SlotResult>> getAvailableSlots(AvailableSlotsRequest request) {
    log.info("Buscando slots disponíveis no Cal.com para eventTypeId={}, start={}, end={}",
      request.eventTypeId(), request.start(), request.end());

    try {
      Map<String, List<CalComSlotEntry>> slots = Optional.ofNullable(
          restExecutor.executeOrNull(SYSTEM, () ->
            restClient.get()
              .uri(uriBuilder -> uriBuilder
                .path("/v2/slots")
                .queryParam("eventTypeId", request.eventTypeId())
                .queryParam("start", request.start())
                .queryParam("end", request.end())
                .queryParam("timeZone", request.timeZone())
                .build())
              .headers(h -> h.set("cal-api-version", SLOTS_API_VERSION))
              .retrieve()
              .body(WRAPPER_SLOTS)))
        .map(CalComApiResponse::data)
        .orElse(Collections.emptyMap());

      log.info("Encontrados slots para {} datas no Cal.com", slots.size());
      return toSlotResults(slots);
    } catch (RemoteServiceException e) {
      throw new GatewayException(AppointmentError.SLOTS_FETCH_FAILED, e);
    }
  }

  private ScheduleResult toScheduleResult(CalComScheduleResponse response) {
    List<AvailabilityRule> availability = response.availability().stream()
      .map(a -> new AvailabilityRule(a.days(), a.startTime(), a.endTime()))
      .toList();

    List<OverrideRule> overrides = response.overrides() != null
      ? response.overrides().stream()
          .map(o -> new OverrideRule(o.date(), o.startTime(), o.endTime()))
          .toList()
      : Collections.emptyList();

    return new ScheduleResult(
      response.id(),
      response.name(),
      response.timeZone(),
      availability,
      response.isDefault(),
      overrides
    );
  }

  private Map<String, List<SlotResult>> toSlotResults(Map<String, List<CalComSlotEntry>> slots) {
    Map<String, List<SlotResult>> result = new LinkedHashMap<>();
    slots.forEach((date, entries) ->
      result.put(date, entries.stream()
        .map(e -> new SlotResult(e.start()))
        .toList())
    );
    return result;
  }
}
