package com.penelopec.calservice.appointment.domain.gateway;

import java.time.OffsetDateTime;

public interface CalComBookingGateway {

  BookingResult createBooking(CreateBookingRequest request);

  BookingResult rescheduleBooking(String bookingUid, OffsetDateTime newStartTime,
                                  OffsetDateTime newEndTime, String reason);

  BookingResult cancelBooking(String bookingUid, String reason);

  BookingResult getBooking(String bookingUid);

  record CreateBookingRequest(
    Long eventTypeId,
    OffsetDateTime startTime,
    OffsetDateTime endTime,
    String attendeeName,
    String attendeeEmail,
    String notes
  ) {}

  record BookingResult(
    String uid,
    Long id,
    String status,
    OffsetDateTime startTime,
    OffsetDateTime endTime
  ) {}
}
