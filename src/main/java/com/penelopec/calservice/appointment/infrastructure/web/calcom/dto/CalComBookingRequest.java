package com.penelopec.calservice.appointment.infrastructure.web.calcom.dto;

import java.time.OffsetDateTime;

public record CalComBookingRequest(
  Long eventTypeId,
  OffsetDateTime start,
  CalComAttendee attendee
) {

  public record CalComAttendee(
    String name,
    String email,
    String timeZone
  ) {}

  public static CalComBookingRequest of(Long eventTypeId, OffsetDateTime start,
                                        String attendeeName, String attendeeEmail) {
    return new CalComBookingRequest(
      eventTypeId,
      start,
      new CalComAttendee(attendeeName, attendeeEmail, "America/Sao_Paulo")
    );
  }
}
