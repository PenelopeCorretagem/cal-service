package com.penelopec.calservice.eventtype.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tipo_evento")
public class EventTypeJpaEntity {

  @Id
  private Long id;

  @Column(name = "titulo", nullable = false, length = 100)
  private String title;

  @Column(nullable = false, unique = true, length = 100)
  private String slug;

  @Column(name = "descricao", length = 500)
  private String description;

  @Column(name = "duracao_minutos", nullable = false)
  private int lengthInMinutes;

  @Column(name = "antecedencia_minima", nullable = false)
  private int minimumBookingNotice;

  @Column(name = "oculto", nullable = false)
  private boolean hidden;

  @Column(name = "empreendimento_id", nullable = false)
  private Long estateId;

  protected EventTypeJpaEntity() {
  }

  public EventTypeJpaEntity(Long id, String title, String slug, String description,
                            int lengthInMinutes, int minimumBookingNotice, boolean hidden,
                            Long estateId) {
    this.id = id;
    this.title = title;
    this.slug = slug;
    this.description = description;
    this.lengthInMinutes = lengthInMinutes;
    this.minimumBookingNotice = minimumBookingNotice;
    this.hidden = hidden;
    this.estateId = estateId;
  }

  public Long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getSlug() {
    return slug;
  }

  public String getDescription() {
    return description;
  }

  public int getLengthInMinutes() {
    return lengthInMinutes;
  }

  public int getMinimumBookingNotice() {
    return minimumBookingNotice;
  }

  public boolean isHidden() {
    return hidden;
  }

  public Long getEstateId() {
    return estateId;
  }
}
