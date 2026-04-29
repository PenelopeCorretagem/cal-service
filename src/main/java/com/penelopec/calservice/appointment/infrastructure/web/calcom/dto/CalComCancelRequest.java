package com.penelopec.calservice.appointment.infrastructure.web.calcom.dto;

public record CalComCancelRequest(
  String cancellationReason
) {

  public static CalComCancelRequest of(String reason) {
    if (reason == null || reason.isBlank()) {
      return new CalComCancelRequest("Cancelado pela imobiliaria.");
    }

    return new CalComCancelRequest(reason.trim());
  }
}
