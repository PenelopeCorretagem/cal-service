package com.penelopec.calservice.domain.entity;

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
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Título do EventType não pode ser vazio");
        }

        @Test
        @DisplayName("Deve falhar quando estateId for nulo")
        void shouldFailWhenEstateIdIsNull() {
            assertThatThrownBy(() -> EventType.createNew("Visita", "Desc", 60, 120, false, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("ID do imóvel é obrigatório");
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
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("ID externo inválido");
        }

        @Test
        @DisplayName("Deve falhar ao atribuir ID externo quando já existir ID")
        void shouldFailWhenExternalIdAlreadyExists() {
            EventType eventType = EventType.reconstitute(9L, "Visita", "visita", "Desc", 60, 120, false, 10L);

            assertThatThrownBy(() -> eventType.assignExternalId(100L))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("já possui ID atribuído");
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
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Duração deve ser maior que zero");
        }

        @Test
        @DisplayName("Deve falhar ao atualizar antecedência com valor negativo")
        void shouldFailWhenUpdatingNegativeBookingNotice() {
            EventType eventType = EventType.reconstitute(1L, "Visita", "visita", "Desc", 60, 120, false, 7L);

            assertThatThrownBy(() -> eventType.updateMinimumBookingNotice(-1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Antecedência mínima não pode ser negativa");
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
}
