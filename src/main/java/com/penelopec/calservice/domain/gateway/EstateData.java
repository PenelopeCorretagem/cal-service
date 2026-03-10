package com.penelopec.calservice.domain.gateway;

public record EstateData(
    Long id,
    String name,
    String description,
    boolean active
) {}
