package com.penelopec.calservice.infrastructure.calcom.dto;

public record CalComUser(
    Long id,
    String username,
    String email,
    String name
) {}
