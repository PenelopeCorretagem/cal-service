package com.penelopec.calservice.appointment.infrastructure.web.calcom.dto;

import java.time.OffsetDateTime;

public record CalComBookingResponse(
  Long id,
  String uid,
  String status,
  OffsetDateTime startTime,
  OffsetDateTime endTime,
  Long eventTypeId
) {}
