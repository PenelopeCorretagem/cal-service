# Framework de Erros e Validacao

Documentacao oficial do framework centralizado de erros do cal-service.

## Estrutura Atual

```text
shared/error/
├── core/
│   ├── ErrorContract.java
│   ├── ErrorType.java
│   ├── ErrorSeverity.java
│   ├── CoreError.java
│   ├── DomainException.java
│   ├── GatewayException.java
│   ├── ApplicationException.java
│   ├── ApiErrorResponse.java
│   └── ApiValidationViolation.java
├── http/
│   ├── ErrorHttpStatusMapping.java
│   ├── HttpStatusResolver.java
│   ├── CoreHttpStatusRegister.java
│   └── GlobalExceptionHandler.java
└── handler/
    └── GlobalExceptionHandler.java (legacy placeholder, deprecated)
```

Erros de dominio continuam nos bounded contexts:

```text
eventtype/domain/error/EventTypeError.java
appointment/domain/error/AppointmentError.java
```

## Principios

- O dominio nao conhece HTTP.
- O contrato de erro e semantico e portavel (codigo + mensagem + severidade + tipo).
- O mapeamento para status HTTP acontece somente no pacote shared/error/http.
- Cada bounded context registra seus codigos no registry HTTP via registrar dedicado.

## Contrato de Erro

```java
public interface ErrorContract {
  String code();
  String messageTemplate();
  ErrorType type();
  ErrorSeverity severity();

  default String format(Object... args) {
    return String.format(messageTemplate(), args);
  }
}
```

`ErrorType`: `CORE`, `DOMAIN`, `GATEWAY`, `VALIDATION`, `APPLICATION`.

`ErrorSeverity`: `INFO`, `WARN`, `ERROR`, `CRITICAL`.

## Hierarquia de Excecoes

- `DomainException`: regras de dominio, invariantes, transicoes invalidas.
- `GatewayException`: falhas de integracao externa (Cal.com, monolito, auth).
- `ApplicationException`: falhas de orquestracao da camada de aplicacao.
- `ValidationException` (shared/validation): erros de validacao de command/query.

Nao use `RuntimeException` para erro de negocio.
Nao use `BusinessException` (foi removida).

## Mapeamento HTTP

### Registry

`ErrorHttpStatusMapping` e um registry concorrente de `code -> HttpStatus`.

- `register(code, status)`: valida entrada e bloqueia sobrescrita silenciosa.
- `resolve(code)`: fallback para `500` quando o codigo nao existe ou e invalido.

### Resolver

`HttpStatusResolver.resolve(error)` consulta o registry.
Se `error` for nulo, fallback para `500`.

### Registrars

- `CoreHttpStatusRegister`
- `EventTypeHttpStatusRegistrar`
- `AppointmentHttpStatusRegistrar`

Cada registrar e `@Component` e executa no startup.

## GlobalExceptionHandler

`shared/error/http/GlobalExceptionHandler` e o unico `@RestControllerAdvice` global.

### Principais mapeamentos

- `DomainException` -> status via registry
- `GatewayException` -> status via registry
- `ApplicationException` -> status via registry
- `ValidationException` -> `422` (`ofApplicationValidation`)
- `MethodArgumentNotValidException` -> `400` (`ofBeanValidation`)
- `HandlerMethodValidationException` -> `400`
- `MissingServletRequestParameterException` -> `400`
- `MethodArgumentTypeMismatchException` -> `400`
- `HttpMessageNotReadableException` -> `400`
- `HttpRequestMethodNotSupportedException` -> `405`
- `NoResourceFoundException` -> `404`
- `Exception` (fallback) -> `500`

O handler possui hardening de null-safety para path/message/error/violations.

## Formato da Resposta

```json
{
  "timestamp": "2026-04-19T14:30:00Z",
  "status": 422,
  "code": "CORE-VALIDATION",
  "message": "Dados invalidos na requisicao.",
  "path": "/appointments",
  "severity": "WARN",
  "violations": [
    { "field": "email", "message": "Email invalido", "code": "CORE-VALIDATION" }
  ]
}
```

## Politica de Log

- `CRITICAL` -> `error` com stacktrace
- `ERROR` -> `error`
- `WARN` -> `warn`
- `INFO` -> `info`

## Como adicionar um novo enum de erro de dominio

1. Criar enum em `<context>.domain.error` implementando `ErrorContract`.
2. Definir codigos semanticos (ex: `XYZ-NOT-FOUND`, nao `XYZ-404`).
3. Criar registrar HTTP no modulo de infraestrutura para mapear cada codigo.
4. Lancar `DomainException`, `GatewayException` ou `ApplicationException` conforme camada.

## Testes de Contrato

Cobertura minima esperada:

- `GlobalExceptionHandlerTest` com `@WebMvcTest` para 400/422/404/502/500.
- `ErrorHttpStatusMappingTest` para fallback, idempotencia e bloqueio de conflito.
