package com.penelopec.calservice.shared.error.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

@Schema(description = "Resposta padrão de erro da API")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ApiErrorResponse(

  @Schema(description = "Momento em que o erro ocorreu", example = "2026-04-18T14:30:00Z")
  Instant timestamp,

  @Schema(description = "Código HTTP do erro", example = "404")
  int status,

  @Schema(description = "Código de negócio do erro", example = "CORE-404")
  String code,

  @Schema(description = "Mensagem descritiva do erro", example = "Recurso não encontrado.")
  String message,

  @Schema(description = "Caminho da requisição que originou o erro", example = "/event-types/99")
  String path,

  @Schema(description = "Severidade do erro")
  ErrorSeverity severity,

  @Schema(description = "Violações de validação (presente apenas em erros de validação)")
  List<ApiValidationViolation> violations
) {

  public static ApiErrorResponse of(
    int status, String code, String message,
    String path, ErrorSeverity severity) {

    return new ApiErrorResponse(
      Instant.now(), status, code, message,
      path, severity, List.of()
    );
  }

  public static ApiErrorResponse ofBeanValidation(
    String path,
    List<ApiValidationViolation> violations) {

    return new ApiErrorResponse(
      Instant.now(),
      400,
      CoreError.VALIDATION_ERROR.code(),
      CoreError.VALIDATION_ERROR.messageTemplate(),
      path,
      CoreError.VALIDATION_ERROR.severity(),
      violations
    );
  }

  public static ApiErrorResponse ofApplicationValidation(
    String path,
    List<ApiValidationViolation> violations) {

    return new ApiErrorResponse(
      Instant.now(),
      422,
      CoreError.VALIDATION_ERROR.code(),
      CoreError.VALIDATION_ERROR.messageTemplate(),
      path,
      CoreError.VALIDATION_ERROR.severity(),
      violations
    );
  }
}
