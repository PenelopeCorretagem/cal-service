package com.penelopec.calservice.appointment.domain.entity;

import com.penelopec.calservice.appointment.domain.error.AppointmentError;
import com.penelopec.calservice.appointment.domain.valueobject.Status;
import com.penelopec.calservice.shared.error.core.DomainException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Appointment {

  private Long id;
  private String bookingUid;
  private Long eventTypeId;
  private Long clientId;
  private Long estateAgentId;
  private Status status;
  private LocalDateTime startDateTime;
  private LocalDateTime endDateTime;
  private String attendeeName;
  private String attendeeEmail;
  private String notes;
  private String reason;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  private Appointment() {
  }

  public static Appointment createNew(Long eventTypeId, Long clientId, Long estateAgentId,
                                      LocalDateTime startDateTime, LocalDateTime endDateTime,
                                      String attendeeName, String attendeeEmail, String notes) {
    if (startDateTime == null || endDateTime == null) {
      throw new DomainException(AppointmentError.MISSING_DATETIMES);
    }
    if (!endDateTime.isAfter(startDateTime)) {
      throw new DomainException(AppointmentError.INVALID_DATES);
    }

    Appointment a = new Appointment();
    a.eventTypeId = eventTypeId;
    a.clientId = clientId;
    a.estateAgentId = estateAgentId;
    a.startDateTime = startDateTime;
    a.endDateTime = endDateTime;
    a.attendeeName = attendeeName;
    a.attendeeEmail = attendeeEmail;
    a.notes = notes;
    a.status = Status.PENDING;
    a.createdAt = LocalDateTime.now();
    a.updatedAt = a.createdAt;
    return a;
  }

  public static Appointment reconstitute(Long id, String bookingUid, Long eventTypeId,
                                         Long clientId, Long estateAgentId,
                                         Status status, LocalDateTime startDateTime,
                                         LocalDateTime endDateTime,
                                         String attendeeName, String attendeeEmail,
                                         String notes, String reason,
                                         LocalDateTime createdAt, LocalDateTime updatedAt) {
    Appointment a = new Appointment();
    a.id = id;
    a.bookingUid = bookingUid;
    a.eventTypeId = eventTypeId;
    a.clientId = clientId;
    a.estateAgentId = estateAgentId;
    a.status = status;
    a.startDateTime = startDateTime;
    a.endDateTime = endDateTime;
    a.attendeeName = attendeeName;
    a.attendeeEmail = attendeeEmail;
    a.notes = notes;
    a.reason = reason;
    a.createdAt = createdAt;
    a.updatedAt = updatedAt;
    return a;
  }

  public void assignBookingUid(String bookingUid) {
    this.bookingUid = bookingUid;
    this.updatedAt = LocalDateTime.now();
  }

  public void confirm() {
    if (this.status.isTerminal()) {
      throw new DomainException(AppointmentError.INVALID_STATUS_TRANSITION, this.status);
    }
    this.status = Status.CONFIRMED;
    this.updatedAt = LocalDateTime.now();
  }

  public void cancel(String reason) {
    if (this.status.isTerminal()) {
      throw new DomainException(AppointmentError.INVALID_STATUS_TRANSITION, this.status);
    }
    this.status = Status.CANCELLED;
    this.reason = reason;
    this.updatedAt = LocalDateTime.now();
  }

  public void conclude() {
    if (this.status == Status.CANCELLED) {
      throw new DomainException(AppointmentError.INVALID_STATUS_TRANSITION, this.status);
    }
    this.status = Status.CONCLUDED;
    this.updatedAt = LocalDateTime.now();
  }

  public void reschedule(LocalDateTime newStart, LocalDateTime newEnd, String reason) {
    if (this.status.isTerminal()) {
      throw new DomainException(AppointmentError.INVALID_STATUS_TRANSITION, this.status);
    }
    if (!newEnd.isAfter(newStart)) {
      throw new DomainException(AppointmentError.INVALID_DATES);
    }
    this.startDateTime = newStart;
    this.endDateTime = newEnd;
    this.reason = reason;
    this.updatedAt = LocalDateTime.now();
  }

  public int getDurationMinutes() {
    return (int) Duration.between(startDateTime, endDateTime).toMinutes();
  }

  public Long getId() { return id; }

  public String getBookingUid() { return bookingUid; }

  public Long getEventTypeId() { return eventTypeId; }

  public Long getClientId() { return clientId; }

  public Long getEstateAgentId() { return estateAgentId; }

  public Status getStatus() { return status; }

  public LocalDateTime getStartDateTime() { return startDateTime; }

  public LocalDateTime getEndDateTime() { return endDateTime; }

  public String getAttendeeName() { return attendeeName; }

  public String getAttendeeEmail() { return attendeeEmail; }

  public String getNotes() { return notes; }

  public String getReason() { return reason; }

  public LocalDateTime getCreatedAt() { return createdAt; }

  public LocalDateTime getUpdatedAt() { return updatedAt; }

  public void setId(Long id) { this.id = id; }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Appointment that)) return false;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
