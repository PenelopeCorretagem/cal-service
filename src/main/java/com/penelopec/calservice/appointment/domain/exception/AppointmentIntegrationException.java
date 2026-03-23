package com.penelopec.calservice.appointment.domain.exception;

public class AppointmentIntegrationException extends RuntimeException {

  public AppointmentIntegrationException(String message) {
    super(message);
  }

  public AppointmentIntegrationException(String message, Throwable cause) {
    super(message, cause);
  }
}
