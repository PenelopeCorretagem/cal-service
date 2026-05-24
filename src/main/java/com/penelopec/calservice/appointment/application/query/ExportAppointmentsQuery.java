package com.penelopec.calservice.appointment.application.query;

public record ExportAppointmentsQuery(
  Long userId,
  String startDate,
  String endDate
) { }
