package com.penelopec.calservice.domain.entity;

import com.penelopec.calservice.domain.valueobject.Slug;

public class EventType {

    private Long id;
    private String title;
    private Slug slug;
    private String description;
    private int lengthInMinutes;
    private int minimumBookingNotice;
    private boolean hidden;
    private Long estateId;

    private static final int DEFAULT_LENGTH_MINUTES = 60;
    private static final int DEFAULT_MIN_BOOKING_NOTICE = 120;
    private static final boolean DEFAULT_HIDDEN = false;
    private static final boolean DEFAULT_REQUIRES_CONFIRMATION = false;

    private EventType(Long id, String title, Slug slug, String description,
                      int lengthInMinutes, int minimumBookingNotice, boolean hidden, Long estateId) {
        this.id = id;
        this.title = title;
        this.slug = slug;
        this.description = description;
        this.lengthInMinutes = lengthInMinutes;
        this.minimumBookingNotice = minimumBookingNotice;
        this.hidden = hidden;
        this.estateId = estateId;
    }

    public static EventType createNew(String title, String description, Integer lengthInMinutes,
                                      Integer minimumBookingNotice, Boolean hidden, Long estateId) {
        validateTitle(title);
        validateEstateId(estateId);
        Slug slug = Slug.fromTitle(title);
        return new EventType(null, title, slug, description,
                lengthInMinutes != null ? lengthInMinutes : DEFAULT_LENGTH_MINUTES,
                minimumBookingNotice != null ? minimumBookingNotice : DEFAULT_MIN_BOOKING_NOTICE,
                hidden != null ? hidden : DEFAULT_HIDDEN,
                estateId);
    }

    public static EventType reconstitute(Long id, String title, String slugValue, String description,
                                         int lengthInMinutes, int minimumBookingNotice, boolean hidden,
                                         Long estateId) {
        return new EventType(id, title, Slug.of(slugValue), description,
                lengthInMinutes, minimumBookingNotice, hidden, estateId);
    }

    public void assignExternalId(Long externalId) {
        if (externalId == null || externalId <= 0) {
            throw new IllegalArgumentException("ID externo inválido: " + externalId);
        }
        if (this.id != null) {
            throw new IllegalStateException("EventType já possui ID atribuído: " + this.id);
        }
        this.id = externalId;
    }

    public boolean hasExternalId() {
        return this.id != null;
    }

    public void updateTitle(String title) {
        validateTitle(title);
        this.title = title;
        this.slug = Slug.fromTitle(title);
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateLengthInMinutes(int lengthInMinutes) {
        if (lengthInMinutes <= 0) {
            throw new IllegalArgumentException("Duração deve ser maior que zero");
        }
        this.lengthInMinutes = lengthInMinutes;
    }

    public void updateMinimumBookingNotice(int minimumBookingNotice) {
        if (minimumBookingNotice < 0) {
            throw new IllegalArgumentException("Antecedência mínima não pode ser negativa");
        }
        this.minimumBookingNotice = minimumBookingNotice;
    }

    public void toggleHidden() {
        this.hidden = !this.hidden;
    }

    public Long getId()                      { return id; }
    public String getTitle()                 { return title; }
    public Slug getSlug()                    { return slug; }
    public String getSlugValue()             { return slug.getValue(); }
    public String getDescription()           { return description; }
    public int getLengthInMinutes()          { return lengthInMinutes; }
    public int getMinimumBookingNotice()     { return minimumBookingNotice; }
    public boolean isHidden()                { return hidden; }
    public Long getEstateId()                { return estateId; }
    public boolean getRequiresConfirmation() { return DEFAULT_REQUIRES_CONFIRMATION; }

    private static void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Título do EventType não pode ser vazio");
        }
    }

    private static void validateEstateId(Long estateId) {
        if (estateId == null) {
            throw new IllegalArgumentException("ID do imóvel é obrigatório");
        }
    }
}
