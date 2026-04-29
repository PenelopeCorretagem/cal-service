package com.penelopec.calservice.eventtype.domain.valueobject;

import com.penelopec.calservice.eventtype.domain.valueobject.Slug;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SlugTest {

  @Nested
  @DisplayName("fromTitle")
  class FromTitle {

    @Test
    @DisplayName("Deve normalizar título em slug")
    void shouldNormalizeTitleToSlug() {
      // When
      Slug slug = Slug.fromTitle("  Visita   Alto Padrão!!  ");

      // Then
      assertThat(slug.getValue()).isEqualTo("visita-alto-padro");
      assertThat(slug.toString()).isEqualTo("visita-alto-padro");
    }

    @Test
    @DisplayName("Deve falhar quando título for nulo ou vazio")
    void shouldFailWhenTitleIsNullOrBlank() {
      assertThatThrownBy(() -> Slug.fromTitle(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Título não pode ser vazio");

      assertThatThrownBy(() -> Slug.fromTitle("   "))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Título não pode ser vazio");
    }

    @Test
    @DisplayName("Deve falhar quando slug resultante for vazio")
    void shouldFailWhenResultingSlugIsBlank() {
      assertThatThrownBy(() -> Slug.fromTitle("!!!@@@###"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Slug resultante é vazio");
    }
  }

  @Nested
  @DisplayName("of")
  class Of {

    @Test
    @DisplayName("Deve criar slug diretamente")
    void shouldCreateDirectSlug() {
      Slug slug = Slug.of("visita-premium");
      assertThat(slug.getValue()).isEqualTo("visita-premium");
    }

    @Test
    @DisplayName("Deve falhar quando valor for inválido")
    void shouldFailWhenValueIsInvalid() {
      assertThatThrownBy(() -> Slug.of(" "))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Slug não pode ser vazio");
    }
  }

  @Test
  @DisplayName("Deve implementar equals e hashCode por valor")
  void shouldImplementEqualsAndHashCodeByValue() {
    Slug first = Slug.of("abc");
    Slug second = Slug.of("abc");
    Slug third = Slug.of("xyz");

    assertThat(first).isEqualTo(second);
    assertThat(first.hashCode()).isEqualTo(second.hashCode());
    assertThat(first).isNotEqualTo(third);
  }
}
