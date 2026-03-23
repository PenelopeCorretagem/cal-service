package com.penelopec.calservice.appointment.infrastructure.controller.dto;

import jakarta.validation.constraints.NotNull;

public record RescheduleAppointmentRequest(

  @NotNull(message = "startDateTime é obrigatório")
  String startDateTime,

  @NotNull(message = "endDateTime é obrigatório")
  String endDateTime,

  String reason
) {}
