package com.penelopec.calservice.appointment.infrastructure.web.calcom.dto;

import java.time.OffsetDateTime;

public record CalComRescheduleRequest(
  OffsetDateTime start
) {}
