package com.penelopec.calservice.appointment.domain.exception;

public class AppointmentNotFoundException extends RuntimeException {

  public AppointmentNotFoundException(String message) {
    super(message);
  }
}
