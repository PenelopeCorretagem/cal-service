package com.penelopec.calservice.appointment.application.query;

public record GetAvailableSlotsQuery(
  Long eventTypeId,
  String start,
  String end
) {}
