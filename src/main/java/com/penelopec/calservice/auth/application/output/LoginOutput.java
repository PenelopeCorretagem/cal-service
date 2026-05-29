package com.penelopec.calservice.auth.application.output;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LoginOutput(String token, @JsonAlias("id") Long userId, String accessLevel) {}
