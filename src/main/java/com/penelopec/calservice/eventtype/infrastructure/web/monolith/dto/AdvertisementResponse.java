package com.penelopec.calservice.eventtype.infrastructure.web.monolith.dto;

public record AdvertisementResponse(
  boolean active,
  EstateResponse estate
) {}