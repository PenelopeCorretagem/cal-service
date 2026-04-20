package com.penelopec.calservice.shared.http.exception;

public class RemoteServiceException extends RuntimeException {

  private final String system;
  private final int statusCode;

  public RemoteServiceException(String system, int statusCode, String message) {
    super("[%s] %s".formatted(system, message));
    this.system = system;
    this.statusCode = statusCode;
  }

  public RemoteServiceException(String system, String message, Throwable cause) {
    super("[%s] %s".formatted(system, message), cause);
    this.system = system;
    this.statusCode = 0;
  }

  public String getSystem() { return system; }
  public int getStatusCode() { return statusCode; }
}
