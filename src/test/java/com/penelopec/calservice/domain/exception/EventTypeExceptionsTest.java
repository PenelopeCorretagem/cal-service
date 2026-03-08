package com.penelopec.calservice.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EventTypeExceptionsTest {

    @Test
    @DisplayName("EventTypeCreationException deve manter mensagem")
    void creationExceptionShouldKeepMessage() {
        EventTypeCreationException ex = new EventTypeCreationException("erro de criação");
        assertThat(ex.getMessage()).isEqualTo("erro de criação");
    }

    @Test
    @DisplayName("EventTypeCreationException deve manter causa")
    void creationExceptionShouldKeepCause() {
        RuntimeException cause = new RuntimeException("root");
        EventTypeCreationException ex = new EventTypeCreationException("erro de criação", cause);

        assertThat(ex.getMessage()).isEqualTo("erro de criação");
        assertThat(ex.getCause()).isSameAs(cause);
    }

    @Test
    @DisplayName("EventTypeDeletionException deve manter mensagem e causa")
    void deletionExceptionShouldKeepMessageAndCause() {
        RuntimeException cause = new RuntimeException("root");
        EventTypeDeletionException ex = new EventTypeDeletionException("erro de remoção", cause);

        assertThat(ex.getMessage()).isEqualTo("erro de remoção");
        assertThat(ex.getCause()).isSameAs(cause);
    }

    @Test
    @DisplayName("EventTypeNotFoundException deve manter mensagem")
    void notFoundExceptionShouldKeepMessage() {
        EventTypeNotFoundException ex = new EventTypeNotFoundException("não encontrado");
        assertThat(ex.getMessage()).isEqualTo("não encontrado");
    }
}
