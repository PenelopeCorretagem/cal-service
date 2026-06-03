package com.penelopec.calservice.eventtype.infrastructure.web.monolith.dto;

public record EstateResponse(
  Long id,
  String title,
  String description,
  String typeKey,
  String typeFriendlyName
) { }