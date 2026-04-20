package com.penelopec.calservice.auth.application.output;

public record LoginOutput(String token, Long userId, String accessLevel) {
}
