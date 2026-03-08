package com.penelopec.calservice.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "monolith")
public record MonolithProperties(Api api) {

    public record Api(String baseUrl, String token, String estatesPath) {}
}
