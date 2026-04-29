package com.penelopec.calservice.auth.infrastructure.web.adapter;

public class AuthenticationGatewayException extends RuntimeException {

  public AuthenticationGatewayException(String message, Throwable cause) {
    super(message, cause);
  }
}
