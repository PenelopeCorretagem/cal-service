package com.penelopec.calservice.eventtype.domain.entity;

import com.penelopec.calservice.eventtype.domain.entity.EventType;
import com.penelopec.calservice.eventtype.domain.error.EventTypeError;
import com.penelopec.calservice.shared.error.core.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventTypeTest {

  @Nested
  @DisplayName("createNew")
  class CreateNew {

    @Test
    @DisplayName("Deve aplicar valores padrão quando opcionais forem nulos")
    void shouldApplyDefaultValuesWhenOptionalFieldsAreNull() {
      // When
      EventType eventType = EventType.createNew("Visita", "Descricao", null, null, null, 50L);

      // Then
      assertThat(eventType.getId()).isNull();
      assertThat(eventType.getTitle()).isEqualTo("Visita");
      assertThat(eventType.getSlugValue()).isEqualTo("visita");
      assertThat(eventType.getLengthInMinutes()).isEqualTo(60);
      assertThat(eventType.getMinimumBookingNotice()).isEqualTo(120);
      assertThat(eventType.isHidden()).isFalse();
      assertThat(eventType.getEstateId()).isEqualTo(50L);
    }

    @Test
    @DisplayName("Deve falhar quando título for inválido")
    void shouldFailWhenTitleIsInvalid() {
      assertThatThrownBy(() -> EventType.createNew(" ", "Desc", 60, 120, false, 1L))
        .isInstanceOf(DomainException.class)
        .satisfies(ex -> assertThat(((DomainException) ex).error()).isEqualTo(EventTypeError.INVALID_TITLE));
    }

    @Test
    @DisplayName("Deve falhar quando estateId for nulo")
    void shouldFailWhenEstateIdIsNull() {
      assertThatThrownBy(() -> EventType.createNew("Visita", "Desc", 60, 120, false, null))
        .isInstanceOf(DomainException.class)
        .satisfies(ex -> assertThat(((DomainException) ex).error()).isEqualTo(EventTypeError.ESTATE_ID_REQUIRED));
    }
  }

  @Nested
  @DisplayName("assignExternalId")
  class AssignExternalId {

    @Test
    @DisplayName("Deve atribuir ID externo quando válido")
    void shouldAssignExternalIdWhenValid() {
      // Given
      EventType eventType = EventType.createNew("Visita", "Desc", 60, 120, false, 10L);

      // When
      eventType.assignExternalId(123L);

      // Then
      assertThat(eventType.getId()).isEqualTo(123L);
      assertThat(eventType.hasExternalId()).isTrue();
    }

    @Test
    @DisplayName("Deve falhar quando ID externo for inválido")
    void shouldFailWhenExternalIdIsInvalid() {
      EventType eventType = EventType.createNew("Visita", "Desc", 60, 120, false, 10L);

      assertThatThrownBy(() -> eventType.assignExternalId(0L))
        .isInstanceOf(DomainException.class)
        .satisfies(ex -> assertThat(((DomainException) ex).error()).isEqualTo(EventTypeError.INVALID_EXTERNAL_ID));
    }

    @Test
    @DisplayName("Deve falhar ao atribuir ID externo quando j\u00e1 existir ID")
    void shouldFailWhenExternalIdAlreadyExists() {
      EventType eventType = EventType.reconstitute(9L, "Visita", "visita", "Desc", 60, 120, false, 10L);

      assertThatThrownBy(() -> eventType.assignExternalId(100L))
        .isInstanceOf(DomainException.class)
        .satisfies(ex -> assertThat(((DomainException) ex).error()).isEqualTo(EventTypeError.INVALID_EXTERNAL_ID));
    }
  }

  @Nested
  @DisplayName("updates")
  class Updates {

    @Test
    @DisplayName("Deve atualizar título e recalcular slug")
    void shouldUpdateTitleAndRecalculateSlug() {
      // Given
      EventType eventType = EventType.reconstitute(1L, "Visita Antiga", "visita-antiga", "Desc", 60, 120, false, 7L);

      // When
      eventType.updateTitle("Visita Nova");

      // Then
      assertThat(eventType.getTitle()).isEqualTo("Visita Nova");
      assertThat(eventType.getSlugValue()).isEqualTo("visita-nova");
    }

    @Test
    @DisplayName("Deve falhar ao atualizar duração com valor inválido")
    void shouldFailWhenUpdatingInvalidLength() {
      EventType eventType = EventType.reconstitute(1L, "Visita", "visita", "Desc", 60, 120, false, 7L);

      assertThatThrownBy(() -> eventType.updateLengthInMinutes(0))
        .isInstanceOf(DomainException.class)
        .satisfies(ex -> assertThat(((DomainException) ex).error()).isEqualTo(EventTypeError.INVALID_DURATION));
    }

    @Test
    @DisplayName("Deve falhar ao atualizar anteced\u00eancia com valor negativo")
    void shouldFailWhenUpdatingNegativeBookingNotice() {
      EventType eventType = EventType.reconstitute(1L, "Visita", "visita", "Desc", 60, 120, false, 7L);

      assertThatThrownBy(() -> eventType.updateMinimumBookingNotice(-1))
        .isInstanceOf(DomainException.class)
        .satisfies(ex -> assertThat(((DomainException) ex).error()).isEqualTo(EventTypeError.INVALID_BOOKING_NOTICE));
    }

    @Test
    @DisplayName("Deve alternar status de oculto")
    void shouldToggleHiddenStatus() {
      EventType eventType = EventType.reconstitute(1L, "Visita", "visita", "Desc", 60, 120, false, 7L);

      eventType.toggleHidden();
      assertThat(eventType.isHidden()).isTrue();

      eventType.toggleHidden();
      assertThat(eventType.isHidden()).isFalse();
    }
  }

  @Nested
  @DisplayName("hide e show")
  class HideAndShow {

    @Test
    @DisplayName("hide() deve definir hidden=true independente do estado anterior")
    void shouldSetHiddenTrue_whenCallingHide() {
      // Given
      EventType eventType = EventType.reconstitute(1L, "Visita", "visita", "Desc", 60, 120, false, 7L);

      // When
      eventType.hide();

      // Then
      assertThat(eventType.isHidden()).isTrue();
    }

    @Test
    @DisplayName("hide() deve ser idempotente quando já oculto")
    void shouldBeIdempotent_whenHidingAlreadyHiddenEventType() {
      // Given
      EventType eventType = EventType.reconstitute(1L, "Visita", "visita", "Desc", 60, 120, true, 7L);

      // When
      eventType.hide();

      // Then
      assertThat(eventType.isHidden()).isTrue();
    }

    @Test
    @DisplayName("show() deve definir hidden=false independente do estado anterior")
    void shouldSetHiddenFalse_whenCallingShow() {
      // Given
      EventType eventType = EventType.reconstitute(1L, "Visita", "visita", "Desc", 60, 120, true, 7L);

      // When
      eventType.show();

      // Then
      assertThat(eventType.isHidden()).isFalse();
    }

    @Test
    @DisplayName("show() deve ser idempotente quando já visível")
    void shouldBeIdempotent_whenShowingAlreadyVisibleEventType() {
      // Given
      EventType eventType = EventType.reconstitute(1L, "Visita", "visita", "Desc", 60, 120, false, 7L);

      // When
      eventType.show();

      // Then
      assertThat(eventType.isHidden()).isFalse();
    }
  }
}
