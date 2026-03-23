package com.penelopec.calservice.appointment.infrastructure.web.calcom.dto;

import java.time.OffsetDateTime;

public record CalComBookingRequest(
  Long eventTypeId,
  OffsetDateTime start,
  OffsetDateTime end,
  CalComAttendee attendee,
  String notes
) {

  public record CalComAttendee(
    String name,
    String email,
    String timeZone
  ) {}

  public static CalComBookingRequest of(Long eventTypeId, OffsetDateTime start, OffsetDateTime end,
                                        String attendeeName, String attendeeEmail, String notes) {
    return new CalComBookingRequest(
      eventTypeId,
      start,
      end,
      new CalComAttendee(attendeeName, attendeeEmail, "America/Sao_Paulo"),
      notes
    );
  }
}
