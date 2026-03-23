package com.penelopec.calservice.appointment.infrastructure.web.calcom.dto;

public record CalComCancelRequest(
  String reason,
  boolean allRemainingBookings
) {}
