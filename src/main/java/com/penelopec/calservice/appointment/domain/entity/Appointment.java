package com.penelopec.calservice.appointment.domain.entity;

import com.penelopec.calservice.appointment.domain.valueobject.Status;

import java.time.LocalDateTime;
import java.util.Objects;

public class Appointment {

  private Long id;
  private String bookingUid;
  private Long eventTypeId;
  private Long clientId;
  private Long estateAgentId;
  private Long estateId;
  private Integer durationMinutes;
  private Status status;
  private LocalDateTime startDateTime;
  private LocalDateTime endDateTime;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  private Appointment() {
  }

  public static Appointment createNew(Long eventTypeId, Long clientId, Long estateAgentId,
                                      Long estateId, LocalDateTime startDateTime,
                                      LocalDateTime endDateTime) {
    if (startDateTime == null || endDateTime == null) {
      throw new IllegalArgumentException("Datas de início e fim são obrigatórias");
    }
    if (!endDateTime.isAfter(startDateTime)) {
      throw new IllegalArgumentException("Data fim deve ser posterior à data início");
    }

    Appointment a = new Appointment();
    a.eventTypeId = eventTypeId;
    a.clientId = clientId;
    a.estateAgentId = estateAgentId;
    a.estateId = estateId;
    a.startDateTime = startDateTime;
    a.endDateTime = endDateTime;
    a.durationMinutes = (int) java.time.Duration.between(startDateTime, endDateTime).toMinutes();
    a.status = Status.PENDING;
    a.createdAt = LocalDateTime.now();
    a.updatedAt = a.createdAt;
    return a;
  }

  public static Appointment reconstitute(Long id, String bookingUid, Long eventTypeId,
                                         Long clientId, Long estateAgentId, Long estateId,
                                         Integer durationMinutes, Status status,
                                         LocalDateTime startDateTime, LocalDateTime endDateTime,
                                         LocalDateTime createdAt, LocalDateTime updatedAt) {
    Appointment a = new Appointment();
    a.id = id;
    a.bookingUid = bookingUid;
    a.eventTypeId = eventTypeId;
    a.clientId = clientId;
    a.estateAgentId = estateAgentId;
    a.estateId = estateId;
    a.durationMinutes = durationMinutes;
    a.status = status;
    a.startDateTime = startDateTime;
    a.endDateTime = endDateTime;
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
      throw new IllegalStateException("Não é possível confirmar agendamento com status " + this.status);
    }
    this.status = Status.CONFIRMED;
    this.updatedAt = LocalDateTime.now();
  }

  public void cancel() {
    if (this.status.isTerminal()) {
      throw new IllegalStateException("Não é possível cancelar agendamento com status " + this.status);
    }
    this.status = Status.CANCELLED;
    this.updatedAt = LocalDateTime.now();
  }

  public void conclude() {
    if (this.status == Status.CANCELLED) {
      throw new IllegalStateException("Não é possível concluir agendamento cancelado");
    }
    this.status = Status.CONCLUDED;
    this.updatedAt = LocalDateTime.now();
  }

  public void reschedule(LocalDateTime newStart, LocalDateTime newEnd) {
    if (this.status.isTerminal()) {
      throw new IllegalStateException("Não é possível reagendar agendamento com status " + this.status);
    }
    if (!newEnd.isAfter(newStart)) {
      throw new IllegalArgumentException("Data fim deve ser posterior à data início");
    }
    this.startDateTime = newStart;
    this.endDateTime = newEnd;
    this.durationMinutes = (int) java.time.Duration.between(newStart, newEnd).toMinutes();
    this.updatedAt = LocalDateTime.now();
  }

  // Getters

  public Long getId() { return id; }

  public String getBookingUid() { return bookingUid; }

  public Long getEventTypeId() { return eventTypeId; }

  public Long getClientId() { return clientId; }

  public Long getEstateAgentId() { return estateAgentId; }

  public Long getEstateId() { return estateId; }

  public Integer getDurationMinutes() { return durationMinutes; }

  public Status getStatus() { return status; }

  public LocalDateTime getStartDateTime() { return startDateTime; }

  public LocalDateTime getEndDateTime() { return endDateTime; }

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
