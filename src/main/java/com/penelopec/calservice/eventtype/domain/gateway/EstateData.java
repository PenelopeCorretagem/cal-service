package com.penelopec.calservice.eventtype.domain.gateway;

public record EstateData(
  Long id,
  String name,
  String description,
  boolean active
) {
}
