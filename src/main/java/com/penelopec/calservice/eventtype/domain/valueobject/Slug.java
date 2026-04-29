package com.penelopec.calservice.eventtype.domain.valueobject;

public final class Slug {

  private final String value;

  private Slug(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("Slug não pode ser vazio");
    }
    this.value = value;
  }

  public static Slug fromTitle(String title) {
    if (title == null || title.isBlank()) {
      throw new IllegalArgumentException("Título não pode ser vazio para gerar slug");
    }

    String normalized = title.toLowerCase()
      .replaceAll("[^a-z0-9\\s-]", "")
      .replaceAll("\\s+", "-")
      .replaceAll("-+", "-")
      .replaceAll("^-|-$", "");

    if (normalized.isBlank()) {
      throw new IllegalArgumentException("Slug resultante é vazio para o título: " + title);
    }

    return new Slug(normalized);
  }

  public static Slug of(String value) {
    return new Slug(value);
  }

  public String getValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Slug slug = (Slug) o;
    return value.equals(slug.value);
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public String toString() {
    return value;
  }
}
