package com.penelopec.calservice.infrastructure.monolith.dto;

public record MonolithEstateResponse(
    Long id,
    String nome,
    String descricao,
    Boolean ativo
) {}
