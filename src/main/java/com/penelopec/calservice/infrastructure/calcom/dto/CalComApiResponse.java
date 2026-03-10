package com.penelopec.calservice.infrastructure.calcom.dto;

public record CalComApiResponse<T>(
    String status,
    T data,
    Object error
) {}
