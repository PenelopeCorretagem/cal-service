package com.penelopec.calservice.domain.exception;

public class EventTypeNotFoundException extends RuntimeException {
    public EventTypeNotFoundException(String message) {
        super(message);
    }
}
