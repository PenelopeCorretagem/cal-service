package com.penelopec.calservice.domain.exception;

public class EventTypeCreationException extends RuntimeException {
    public EventTypeCreationException(String message) {
        super(message);
    }

    public EventTypeCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
