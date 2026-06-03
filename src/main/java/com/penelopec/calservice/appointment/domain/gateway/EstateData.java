package com.penelopec.calservice.appointment.domain.gateway;

public record EstateData(
    Long id,
    String title,
    String typeKey,
    String typeFriendlyName
) {}
