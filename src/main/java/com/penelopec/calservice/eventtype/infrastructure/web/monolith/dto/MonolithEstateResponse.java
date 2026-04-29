package com.penelopec.calservice.eventtype.infrastructure.web.monolith.dto;

public record MonolithEstateResponse(
  Long id,
  String nome,
  String descricao,
  Boolean ativo
) {
}
