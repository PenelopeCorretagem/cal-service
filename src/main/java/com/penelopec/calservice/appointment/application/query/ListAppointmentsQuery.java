package com.penelopec.calservice.appointment.application.query;

public record ListAppointmentsQuery(
  Long clientId,
  Long estateAgentId,
  Long estateId,
  String status,
  String startDateTime,
  String endDateTime,
  Integer page,
  Integer size
) { }
