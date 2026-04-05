package com.penelopec.calservice.appointment.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

class StatusTest {

  @Test
  @DisplayName("isTerminal deve retornar true para CANCELLED")
  void isTerminalShouldReturnTrue_whenStatusIsCancelled() {
    assertThat(Status.CANCELLED.isTerminal()).isTrue();
  }

  @Test
  @DisplayName("isTerminal deve retornar true para CONCLUDED")
  void isTerminalShouldReturnTrue_whenStatusIsConcluded() {
    assertThat(Status.CONCLUDED.isTerminal()).isTrue();
  }

  @Test
  @DisplayName("isTerminal deve retornar false para PENDING")
  void isTerminalShouldReturnFalse_whenStatusIsPending() {
    assertThat(Status.PENDING.isTerminal()).isFalse();
  }

  @Test
  @DisplayName("isTerminal deve retornar false para CONFIRMED")
  void isTerminalShouldReturnFalse_whenStatusIsConfirmed() {
    assertThat(Status.CONFIRMED.isTerminal()).isFalse();
  }

  @Test
  @DisplayName("getDescricao deve retornar descricao correta para cada status")
  void getDescricaoShouldReturnCorrectDescription_forEachStatus() {
    assertThat(Status.PENDING.getDescricao()).isEqualTo("agendado");
    assertThat(Status.CONFIRMED.getDescricao()).isEqualTo("confirmado");
    assertThat(Status.CONCLUDED.getDescricao()).isEqualTo("concluido");
    assertThat(Status.CANCELLED.getDescricao()).isEqualTo("cancelado");
  }

  @ParameterizedTest
  @EnumSource(Status.class)
  @DisplayName("Todos os valores de Status devem ter descricao nao nula")
  void allStatusValuesShouldHaveNonNullDescricao(Status status) {
    assertThat(status.getDescricao()).isNotNull().isNotBlank();
  }
}
