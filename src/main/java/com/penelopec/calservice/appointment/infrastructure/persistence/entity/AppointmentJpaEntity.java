package com.penelopec.calservice.appointment.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "agendamento")
public class AppointmentJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "booking_uid", unique = true)
  private String bookingUid;

  @Column(name = "event_type_id")
  private Long eventTypeId;

  @Column(name = "cliente_id")
  private Long clientId;

  @Column(name = "corretor_id")
  private Long estateAgentId;

  @Column(name = "nome_participante", length = 150)
  private String attendeeName;

  @Column(name = "email_participante")
  private String attendeeEmail;

  @Column(name = "observacoes", columnDefinition = "TEXT")
  private String notes;

  @Column(name = "motivo", length = 500)
  private String reason;

  @Column(name = "status", nullable = false, length = 20)
  private String status;

  @Column(name = "data_inicio", nullable = false)
  private LocalDateTime startDateTime;

  @Column(name = "data_fim", nullable = false)
  private LocalDateTime endDateTime;

  @Column(name = "criado_em", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "atualizado_em", nullable = false)
  private LocalDateTime updatedAt;

  protected AppointmentJpaEntity() {
  }

  public AppointmentJpaEntity(Long id, String bookingUid, Long eventTypeId, Long clientId,
                              Long estateAgentId, String status,
                              LocalDateTime startDateTime, LocalDateTime endDateTime,
                              String attendeeName, String attendeeEmail,
                              String notes, String reason,
                              LocalDateTime createdAt, LocalDateTime updatedAt) {
    this.id = id;
    this.bookingUid = bookingUid;
    this.eventTypeId = eventTypeId;
    this.clientId = clientId;
    this.estateAgentId = estateAgentId;
    this.status = status;
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.attendeeName = attendeeName;
    this.attendeeEmail = attendeeEmail;
    this.notes = notes;
    this.reason = reason;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public Long getId() { return id; }
  public String getBookingUid() { return bookingUid; }
  public Long getEventTypeId() { return eventTypeId; }
  public Long getClientId() { return clientId; }
  public Long getEstateAgentId() { return estateAgentId; }
  public String getAttendeeName() { return attendeeName; }
  public String getAttendeeEmail() { return attendeeEmail; }
  public String getNotes() { return notes; }
  public String getReason() { return reason; }
  public String getStatus() { return status; }
  public LocalDateTime getStartDateTime() { return startDateTime; }
  public LocalDateTime getEndDateTime() { return endDateTime; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }
}
