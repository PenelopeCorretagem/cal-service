package com.penelopec.calservice.infrastructure.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

@Schema(description = "Resposta padrão de erro da API")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorResponse(

        @Schema(description = "Código HTTP do erro", example = "404")
        int status,

        @Schema(description = "Classificação do erro", example = "Not Found")
        String error,

        @Schema(description = "Mensagem descritiva do erro", example = "EventType não encontrado: 99")
        String message,

        @Schema(description = "Caminho da requisição que originou o erro", example = "/event-types/99")
        String path,

        @Schema(description = "Momento em que o erro ocorreu", example = "2026-03-07T14:30:00Z")
        Instant timestamp,

        @Schema(description = "Lista de erros de validação (presente apenas em erros 422)")
        List<FieldError> fieldErrors
) {

    @Schema(description = "Detalhe de um erro de validação em campo específico")
    public record FieldError(

            @Schema(description = "Nome do campo com erro", example = "titulo")
            String field,

            @Schema(description = "Mensagem de validação", example = "Título é obrigatório")
            String message
    ) {}

    public static ApiErrorResponse of(int status, String error, String message, String path) {
        return new ApiErrorResponse(status, error, message, path, Instant.now(), null);
    }

    public static ApiErrorResponse ofValidation(int status, String error, String message, String path,
                                                 List<FieldError> fieldErrors) {
        return new ApiErrorResponse(status, error, message, path, Instant.now(), fieldErrors);
    }
}
