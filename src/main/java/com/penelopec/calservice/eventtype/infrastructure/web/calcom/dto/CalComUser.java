package com.penelopec.calservice.eventtype.infrastructure.web.calcom.dto;

public record CalComUser(
  Long id,
  String username,
  String email,
  String name
) {
}
