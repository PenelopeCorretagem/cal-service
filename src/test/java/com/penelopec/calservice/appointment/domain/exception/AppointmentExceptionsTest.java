package com.penelopec.calservice.appointment.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AppointmentExceptionsTest {

  @Test
  @DisplayName("AppointmentNotFoundException deve manter mensagem")
  void notFoundExceptionShouldKeepMessage() {
    // Given / When
    AppointmentNotFoundException ex = new AppointmentNotFoundException("Agendamento não encontrado: 42");

    // Then
    assertThat(ex.getMessage()).isEqualTo("Agendamento não encontrado: 42");
  }

  @Test
  @DisplayName("AppointmentIntegrationException deve manter mensagem")
  void integrationExceptionShouldKeepMessage() {
    // Given / When
    AppointmentIntegrationException ex = new AppointmentIntegrationException("Erro na integração");

    // Then
    assertThat(ex.getMessage()).isEqualTo("Erro na integração");
  }

  @Test
  @DisplayName("AppointmentIntegrationException deve manter mensagem e causa")
  void integrationExceptionShouldKeepMessageAndCause() {
    // Given
    RuntimeException cause = new RuntimeException("root cause");

    // When
    AppointmentIntegrationException ex = new AppointmentIntegrationException("Erro na integração", cause);

    // Then
    assertThat(ex.getMessage()).isEqualTo("Erro na integração");
    assertThat(ex.getCause()).isSameAs(cause);
  }
}
