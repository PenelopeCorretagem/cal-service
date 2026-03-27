package com.penelopec.calservice.appointment.infrastructure.controller.dto;

import jakarta.validation.constraints.NotNull;

public record CreateAppointmentRequest(

  @NotNull(message = "eventTypeId é obrigatório")
  Long eventTypeId,

  @NotNull(message = "clientId é obrigatório")
  Long clientId,

  @NotNull(message = "estateAgentId é obrigatório")
  Long estateAgentId,

  @NotNull(message = "estateId é obrigatório")
  Long estateId,

  @NotNull(message = "startDateTime é obrigatório")
  String startDateTime,

  @NotNull(message = "endDateTime é obrigatório")
  String endDateTime,

  String attendeeName,

  String attendeeEmail,

  String notes
) {}
