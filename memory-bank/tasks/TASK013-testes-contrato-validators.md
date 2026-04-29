# TASK013 - Testes de Contrato e Validators

**Status:** Completed
**Added:** 2026-04-19
**Updated:** 2026-04-20

## Original Request

Criar testes unitários dedicados para todos os validators e testes de contrato HTTP do `GlobalExceptionHandler` cobrindo os cenários 400/422/404/502/payload inválido e método não permitido.

## Thought Process

Com TASK011/TASK012 já estabilizadas, a execução foi dividida em três blocos:
- Cobertura unitária dos validators de command.
- Cobertura unitária do validator de query (`ListAppointmentsQueryValidator`).
- Fechamento do contrato HTTP do `GlobalExceptionHandler` para incluir `405 Method Not Allowed`.

## Implementation Plan

- [x] TASK013.1 — 6 `CommandValidatorTest`: `CreateEventType`, `UpdateEventType`, `HandleEstateChanged`, `CreateAppointment`, `RescheduleAppointment`, `CancelAppointment`
- [x] TASK013.2 — `ListAppointmentsQueryValidatorTest`: cenários válidos e inválidos (page negativo, size zero, status inválido, datetime malformado)
- [x] TASK013.3 — `GlobalExceptionHandlerTest` com `@WebMvcTest`: 400 (BeanValidation), 422 (ApplicationValidation), 404 (DomainException), 502 (Gateway/Auth), 500 (fallback), método não permitido, payload malformado

## Progress Tracking

**Overall Status:** Completed — 100%

### Subtasks

| ID | Description | Status | Updated | Notes |
|----|-------------|--------|---------|-------|
| TASK013.1 | 6 CommandValidator unit tests | Completed | 2026-04-20 | 6 classes de teste adicionadas para validators de command |
| TASK013.2 | `ListAppointmentsQueryValidatorTest` | Completed | 2026-04-20 | Cobertura de cenários válidos e inválidos de paginação/status/datetime |
| TASK013.3 | `GlobalExceptionHandlerTest` com `@WebMvcTest` | Completed | 2026-04-20 | Cenário `405` adicionado, mantendo 400/422/404/502/500/payload inválido |

## Arquivos Criados

```
src/test/java/com/penelopec/calservice/
├── eventtype/application/validator/
│   ├── CreateEventTypeCommandValidatorTest.java
│   ├── UpdateEventTypeCommandValidatorTest.java
│   └── HandleEstateChangedCommandValidatorTest.java
├── appointment/application/validator/
│   ├── AppointmentCommandValidatorTest.java
│   ├── RescheduleAppointmentCommandValidatorTest.java
│   ├── CancelAppointmentCommandValidatorTest.java
│   └── ListAppointmentsQueryValidatorTest.java
```

## Arquivos Atualizados

```
src/test/java/com/penelopec/calservice/shared/error/http/GlobalExceptionHandlerTest.java
```

## Cobertura de Testes Necessária
- 6 CommandValidators — unitário por classe — Alta
- `ListAppointmentsQueryValidator` — unitário — Alta
- `GlobalExceptionHandler` — `@WebMvcTest` — Alta

## Riscos Identificados
- Risco de bootstrap do `@WebMvcTest` com segurança e controller de apoio foi mitigado no teste existente do handler.
- Não restam riscos abertos para TASK013 após validação completa com `compile` e `test`.

## Progress Log

### 2026-04-19
- Tarefa criada com plano aprovado. Status Pending — aguarda conclusão de TASK011 e TASK012 (Checkpoint 1).

### 2026-04-20
- TASK013.1 concluída com criação de 6 testes unitários de command validators.
- TASK013.2 concluída com criação de `ListAppointmentsQueryValidatorTest` cobrindo page/size/status/datetime.
- TASK013.3 concluída com adição do cenário `405 Method Not Allowed` em `GlobalExceptionHandlerTest`.
- Validação executada com Java 21:
    - `./mvnw compile` -> sucesso
    - `./mvnw test` -> sucesso (suite completa)
