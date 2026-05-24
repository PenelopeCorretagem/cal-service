package com.penelopec.calservice.auth.application.output;

public record ValidateTokenOutput(String email, Long id, String accessLevel) {
}
