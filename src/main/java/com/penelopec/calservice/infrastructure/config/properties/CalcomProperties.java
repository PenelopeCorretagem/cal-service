package com.penelopec.calservice.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "calcom")
public record CalcomProperties(Api api) {
  public record Api(String baseUrl, String key, String versionV1, String versionV2) {}
}