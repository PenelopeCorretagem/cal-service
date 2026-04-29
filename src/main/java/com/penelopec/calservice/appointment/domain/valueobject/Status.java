package com.penelopec.calservice.appointment.domain.valueobject;

public enum Status {
  PENDING("agendado"),
  CONFIRMED("confirmado"),
  CONCLUDED("concluido"),
  CANCELLED("cancelado");

  private final String descricao;

  Status(String status) {
    this.descricao = status;
  }

  public boolean isTerminal() {
    return this == CANCELLED || this == CONCLUDED;
  }

  public String getDescricao() {
    return descricao;
  }
}
