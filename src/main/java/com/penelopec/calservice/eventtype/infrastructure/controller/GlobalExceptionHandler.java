package com.penelopec.calservice.eventtype.infrastructure.controller;

import com.penelopec.calservice.appointment.domain.exception.AppointmentIntegrationException;
import com.penelopec.calservice.appointment.domain.exception.AppointmentNotFoundException;
import com.penelopec.calservice.eventtype.domain.exception.EventTypeCreationException;
import com.penelopec.calservice.eventtype.domain.exception.EventTypeDeletionException;
import com.penelopec.calservice.eventtype.domain.exception.EventTypeNotFoundException;
import com.penelopec.calservice.eventtype.infrastructure.controller.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(EventTypeNotFoundException.class)
  public ResponseEntity<ApiErrorResponse> handleNotFound(EventTypeNotFoundException ex,
                                                         HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
      .body(ApiErrorResponse.of(
        HttpStatus.NOT_FOUND.value(),
        "Not Found",
        ex.getMessage(),
        request.getRequestURI()
      ));
  }

  @ExceptionHandler(AppointmentNotFoundException.class)
  public ResponseEntity<ApiErrorResponse> handleAppointmentNotFound(AppointmentNotFoundException ex,
                                                                    HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
      .body(ApiErrorResponse.of(
        HttpStatus.NOT_FOUND.value(),
        "Not Found",
        ex.getMessage(),
        request.getRequestURI()
      ));
  }

  @ExceptionHandler(AppointmentIntegrationException.class)
  public ResponseEntity<ApiErrorResponse> handleAppointmentIntegration(AppointmentIntegrationException ex,
                                                                       HttpServletRequest request) {
    log.error("Erro de integração com Cal.com: {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
      .body(ApiErrorResponse.of(
        HttpStatus.BAD_GATEWAY.value(),
        "Bad Gateway",
        ex.getMessage(),
        request.getRequestURI()
      ));
  }



  @ExceptionHandler(EventTypeCreationException.class)
  public ResponseEntity<ApiErrorResponse> handleCreationError(EventTypeCreationException ex,
                                                              HttpServletRequest request) {
    log.error("Erro ao criar/atualizar EventType: {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
      .body(ApiErrorResponse.of(
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        "Internal Server Error",
        ex.getMessage(),
        request.getRequestURI()
      ));
  }

  @ExceptionHandler(EventTypeDeletionException.class)
  public ResponseEntity<ApiErrorResponse> handleDeletionError(EventTypeDeletionException ex,
                                                              HttpServletRequest request) {
    log.error("Erro ao deletar EventType: {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
      .body(ApiErrorResponse.of(
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        "Internal Server Error",
        ex.getMessage(),
        request.getRequestURI()
      ));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                           HttpServletRequest request) {
    List<ApiErrorResponse.FieldError> fieldErrors = ex.getBindingResult()
      .getFieldErrors()
      .stream()
      .map(fe -> new ApiErrorResponse.FieldError(fe.getField(), fe.getDefaultMessage()))
      .toList();

    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
      .body(ApiErrorResponse.ofValidation(
        HttpStatus.UNPROCESSABLE_CONTENT.value(),
        "Unprocessable Content",
        "Erro de validação",
        request.getRequestURI(),
        fieldErrors
      ));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex,
                                                                HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
      .body(ApiErrorResponse.of(
        HttpStatus.UNPROCESSABLE_CONTENT.value(),
        "Unprocessable Content",
        ex.getMessage(),
        request.getRequestURI()
      ));
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ApiErrorResponse> handleIllegalState(IllegalStateException ex,
                                                              HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
      .body(ApiErrorResponse.of(
        HttpStatus.UNPROCESSABLE_CONTENT.value(),
        "Unprocessable Content",
        ex.getMessage(),
        request.getRequestURI()
      ));
  }
}
