package com.penelopec.calservice.shared.error.http;

import com.penelopec.calservice.shared.error.core.ApiErrorResponse;
import com.penelopec.calservice.shared.error.core.ApiValidationViolation;
import com.penelopec.calservice.shared.error.core.ApplicationException;
import com.penelopec.calservice.shared.error.core.CoreError;
import com.penelopec.calservice.shared.error.core.DomainException;
import com.penelopec.calservice.shared.error.core.ErrorContract;
import com.penelopec.calservice.shared.error.core.ErrorSeverity;
import com.penelopec.calservice.shared.error.core.GatewayException;
import com.penelopec.calservice.shared.validation.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Objects;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @SuppressWarnings("null-check")
  @ExceptionHandler(DomainException.class)
  public ResponseEntity<ApiErrorResponse> handleDomain(DomainException ex, HttpServletRequest request) {
    ErrorContract error = resolveError(ex.error());
    String message = resolveMessage(ex.getMessage(), error);
    log(error, message, ex);
    return buildErrorResponse(error, message, resolvePath(request));
  }

  @ExceptionHandler(GatewayException.class)
  public ResponseEntity<ApiErrorResponse> handleGateway(GatewayException ex, HttpServletRequest request) {
    ErrorContract error = resolveError(ex.error());
    String message = resolveMessage(ex.getMessage(), error);
    log(error, message, ex);
    return buildErrorResponse(error, message, resolvePath(request));
  }

  @ExceptionHandler(ApplicationException.class)
  public ResponseEntity<ApiErrorResponse> handleApplication(ApplicationException ex, HttpServletRequest request) {
    ErrorContract error = resolveError(ex.error());
    String message = resolveMessage(ex.getMessage(), error);
    log(error, message, ex);
    return buildErrorResponse(error, message, resolvePath(request));
  }

  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<ApiErrorResponse> handleValidation(ValidationException ex, HttpServletRequest request) {
    String path = resolvePath(request);
    List<ApiValidationViolation> violations = toApplicationViolations(ex);

    log.warn("[{}] Falha de validação em {}: {} violação(ões)",
      CoreError.VALIDATION_ERROR.code(), path, violations.size());

    return ResponseEntity
      .status(422)
      .body(ApiErrorResponse.ofApplicationValidation(path, violations));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiErrorResponse> handleBeanValidation(MethodArgumentNotValidException ex,
                                                               HttpServletRequest request) {
    String path = resolvePath(request);
    List<ApiValidationViolation> violations = toBeanViolations(ex);

    log.warn("[{}] Erro de validação em {}: {} violação(ões)",
      CoreError.VALIDATION_ERROR.code(), path, violations.size());

    return ResponseEntity
      .status(400)
      .body(ApiErrorResponse.ofBeanValidation(path, violations));
  }

  @ExceptionHandler(HandlerMethodValidationException.class)
  public ResponseEntity<ApiErrorResponse> handleConstraint(HandlerMethodValidationException ex,
                                                           HttpServletRequest request) {
    String path = resolvePath(request);
    List<ApiValidationViolation> violations = toConstraintViolations(ex);

    log.warn("[{}] Erro de validação em parâmetros em {}: {} violação(ões)",
      CoreError.VALIDATION_ERROR.code(), path, violations.size());

    return ResponseEntity
      .status(400)
      .body(ApiErrorResponse.ofBeanValidation(path, violations));
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ApiErrorResponse> handleMissingParam(MissingServletRequestParameterException ex,
                                                             HttpServletRequest request) {
    String path = resolvePath(request);
    log.warn("[{}] Parâmetro ausente '{}' em {}", CoreError.MISSING_PARAMETER.code(),
      ex.getParameterName(), path);
    return buildErrorResponse(CoreError.MISSING_PARAMETER,
      CoreError.MISSING_PARAMETER.format(ex.getParameterName()), path);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                             HttpServletRequest request) {
    String path = resolvePath(request);
    log.warn("[{}] Tipo inválido no parâmetro '{}' em {}", CoreError.TYPE_MISMATCH.code(),
      ex.getName(), path);
    return buildErrorResponse(CoreError.TYPE_MISMATCH,
      CoreError.TYPE_MISMATCH.messageTemplate(), path);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiErrorResponse> handleUnreadable(HttpMessageNotReadableException ex,
                                                           HttpServletRequest request) {
    String path = resolvePath(request);
    log.warn("[{}] Payload inválido em {}", CoreError.UNREADABLE_PAYLOAD.code(), path);
    return buildErrorResponse(CoreError.UNREADABLE_PAYLOAD,
      CoreError.UNREADABLE_PAYLOAD.messageTemplate(), path);
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ApiErrorResponse> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex,
                                                                 HttpServletRequest request) {
    String path = resolvePath(request);
    log.warn("[{}] Método {} não permitido em {}", CoreError.METHOD_NOT_ALLOWED.code(),
      ex.getMethod(), path);
    return buildErrorResponse(CoreError.METHOD_NOT_ALLOWED,
      CoreError.METHOD_NOT_ALLOWED.messageTemplate(), path);
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ApiErrorResponse> handleNotFound(NoResourceFoundException ex,
                                                         HttpServletRequest request) {
    String path = resolvePath(request);
    log.warn("[{}] Rota não encontrada: {}", CoreError.NOT_FOUND.code(), path);
    return buildErrorResponse(CoreError.NOT_FOUND,
      CoreError.NOT_FOUND.messageTemplate(), path);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiErrorResponse> handleFallback(Exception ex, HttpServletRequest request) {
    String path = resolvePath(request);
    log.error("[{}] Erro inesperado em {}: {}", CoreError.INTERNAL_ERROR.code(),
      path, ex.getMessage(), ex);
    return buildErrorResponse(CoreError.INTERNAL_ERROR,
      CoreError.INTERNAL_ERROR.messageTemplate(), path);
  }

  private ResponseEntity<ApiErrorResponse> buildErrorResponse(ErrorContract error,
                                                               String message, String path) {
    ErrorContract resolvedError = resolveError(error);
    String resolvedMessage = resolveMessage(message, resolvedError);
    String resolvedPath = resolvePath(path);
    var status = HttpStatusResolver.resolve(resolvedError);

    return ResponseEntity
      .status(status)
      .body(ApiErrorResponse.of(
        status.value(),
        resolvedError.code(),
        resolvedMessage,
        resolvedPath,
        resolvedError.severity()
      ));
  }

  private List<ApiValidationViolation> toApplicationViolations(ValidationException ex) {
    if (ex == null || ex.getErrors() == null) {
      return List.of();
    }

    return ex.getErrors().stream()
      .filter(Objects::nonNull)
      .map(error -> new ApiValidationViolation(
        safeText(error.field(), "global"),
        safeText(error.message(), CoreError.VALIDATION_ERROR.messageTemplate()),
        safeText(error.code(), CoreError.VALIDATION_ERROR.code())
      ))
      .toList();
  }

  private List<ApiValidationViolation> toBeanViolations(MethodArgumentNotValidException ex) {
    if (ex == null || ex.getBindingResult() == null) {
      return List.of();
    }

    return ex.getBindingResult().getFieldErrors().stream()
      .map(fieldError -> new ApiValidationViolation(
        safeText(fieldError.getField(), "global"),
        safeText(fieldError.getDefaultMessage(), CoreError.VALIDATION_ERROR.messageTemplate()),
        resolveValidationCode(fieldError.getCodes())
      ))
      .toList();
  }

  private List<ApiValidationViolation> toConstraintViolations(HandlerMethodValidationException ex) {
    if (ex == null) {
      return List.of();
    }

    return ex.getParameterValidationResults().stream()
      .flatMap((ParameterValidationResult result) -> result.getResolvableErrors()
        .stream()
        .map(error -> new ApiValidationViolation(
          safeText(result.getMethodParameter().getParameterName(), "parameter"),
          safeText(error.getDefaultMessage(), CoreError.VALIDATION_ERROR.messageTemplate()),
          resolveValidationCode(error.getCodes())
        )))
      .toList();
  }

  private String resolveValidationCode(String[] codes) {
    if (codes == null || codes.length == 0) {
      return "INVALID";
    }
    return safeText(codes[codes.length - 1], "INVALID");
  }

  private ErrorContract resolveError(ErrorContract error) {
    return error == null ? CoreError.INTERNAL_ERROR : error;
  }

  private String resolveMessage(String message, ErrorContract error) {
    return safeText(message, error.messageTemplate());
  }

  private String resolvePath(HttpServletRequest request) {
    return request == null ? "/unknown" : resolvePath(request.getRequestURI());
  }

  private String resolvePath(String path) {
    return safeText(path, "/unknown");
  }

  private String safeText(String value, String fallback) {
    return (value == null || value.isBlank()) ? fallback : value;
  }

  private void log(ErrorContract error, String message, Exception ex) {
    ErrorSeverity severity = error.severity() == null ? ErrorSeverity.ERROR : error.severity();
    String code = safeText(error.code(), CoreError.INTERNAL_ERROR.code());
    String safeMessage = resolveMessage(message, error);

    switch (severity) {
      case CRITICAL -> log.error("[{}] {}", code, safeMessage, ex);
      case ERROR    -> log.error("[{}] {}", code, safeMessage);
      case WARN     -> log.warn("[{}] {}", code, safeMessage);
      default       -> log.info("[{}] {}", code, safeMessage);
    }
  }
}
