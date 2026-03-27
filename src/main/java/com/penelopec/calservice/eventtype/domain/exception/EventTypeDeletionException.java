package com.penelopec.calservice.eventtype.domain.exception;

public class EventTypeDeletionException extends RuntimeException {
  public EventTypeDeletionException(String message) {
    super(message);
  }

  public EventTypeDeletionException(String message, Throwable cause) {
    super(message, cause);
  }
}
