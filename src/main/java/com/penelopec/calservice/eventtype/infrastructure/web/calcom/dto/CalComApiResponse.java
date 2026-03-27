package com.penelopec.calservice.eventtype.infrastructure.web.calcom.dto;

public record CalComApiResponse<T>(
  String status,
  T data,
  Object error
) {
}
