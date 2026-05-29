package com.penelopec.calservice.auth.application.output;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ValidateTokenOutput(String email, Long id, String accessLevel) {}
