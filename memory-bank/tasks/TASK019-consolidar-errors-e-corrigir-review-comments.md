# TASK019 - Consolidar errors por contexto e corrigir review comments

**Status:** Completed
**Added:** 2026-04-20
**Updated:** 2026-04-20

## Original Request

Consolidar os enums de erro/validacao em um unico arquivo por contexto (seguindo o padrao de `AppointmentError`) e, em seguida, corrigir os pontos sinalizados no code review da PR.

## Thought Process

A mudanca precisava reduzir duplicacao de contratos de erro sem quebrar o fluxo atual de validacao e sem alterar o contrato de excecoes da aplicacao. A estrategia escolhida foi:
- consolidar os codigos de validacao dentro dos enums principais de erro de cada contexto;
- remover enums redundantes de validacao;
- corrigir os comentarios da review com impacto funcional (mensagem formatada, seguranca de rotas e retry em listener);
- atualizar testes para refletir o contrato esperado e validar fim-a-fim com compile/test.

## Implementation Plan

- [x] Consolidar `AppointmentValidationCode` em `AppointmentError`.
- [x] Consolidar `EventTypeValidationCode` em `EventTypeError`.
- [x] Ajustar validators/testes para os novos codigos consolidados.
- [x] Corrigir placeholder `%s` de status invalido em listagem de appointments.
- [x] Restringir whitelist de auth no `SecurityConfig`.
- [x] Remover retry com `Thread.sleep` do consumer e mover retry/backoff para `RabbitMQConfig`.
- [x] Alinhar teste de create appointment para contrato de `ValidationException`.
- [x] Validar com `./mvnw compile` e `./mvnw test` em Java 21.

## Progress Tracking

**Overall Status:** Completed - 100%

### Subtasks
| ID | Description | Status | Updated | Notes |
|----|-------------|--------|---------|-------|
| TASK019.1 | Consolidar erros de validacao em `AppointmentError` | Completed | 2026-04-20 | Novas constantes `APPT-VAL-*` adicionadas |
| TASK019.2 | Consolidar erros de validacao em `EventTypeError` | Completed | 2026-04-20 | Novas constantes `ET-VAL-*` adicionadas |
| TASK019.3 | Migrar validators e testes para enums consolidados | Completed | 2026-04-20 | Imports e asserts atualizados |
| TASK019.4 | Corrigir `%s` no `LIST_STATUS_INVALID` | Completed | 2026-04-20 | `ValidationResult` passou a aceitar args formataveis |
| TASK019.5 | Hardening de seguranca e retry AMQP | Completed | 2026-04-20 | `/auth/**` removido; retry/backoff no container |
| TASK019.6 | Ajustar testes de contrato e validar build | Completed | 2026-04-20 | `compile` e `test` verdes |

## Progress Log

### 2026-04-20 (Checkpoint 2 - Implementacao concluida)
- Consolidacao executada:
  - `AppointmentValidationCode` removido e codigos de validacao migrados para `AppointmentError`.
  - `EventTypeValidationCode` removido e codigos de validacao migrados para `EventTypeError` (constantes `VALIDATION_*` para evitar colisao com erros de dominio).
- Comentarios de review corrigidos:
  - `ListAppointmentsQueryValidator` passou a injetar o status invalido na mensagem (`%s`) usando `ValidationResult.addError(..., args)`.
  - `ValidationResult` ganhou overloads com argumentos para formatacao de mensagens sem quebrar chamadas existentes.
  - `shared/error/doc.md` atualizado com nome correto `CoreHttpStatusRegister`.
  - `SecurityConfig` em producao trocado de `/auth/**` para endpoints explicitos (`/auth/login`, `/auth/validate-token`).
  - `EstateChangedConsumer` sem retry com `Thread.sleep`; falhas sao propagadas para retry/DLQ gerenciados pelo container.
  - `RabbitMQConfig` configurado com `RetryInterceptorBuilder.stateless()`, `maxRetries(3)`, backoff exponencial e `RejectAndDontRequeueRecoverer`.
  - `CreateAppointmentServiceTest` alinhado ao contrato de validacao (espera `ValidationException` via `validateAndThrow`).
- Decisoes tomadas:
  - manter codigos de validacao consolidados nos enums de erro por contexto, sem criar novo arquivo intermediario;
  - usar nomes `VALIDATION_*` no `eventtype` para separar semanticamente validacao de dominio e evitar choque de constantes.
- Licoes aprendidas:
  - em Spring AMQP 4, a API usa `maxRetries` (nao `maxAttempts`);
  - para testar `validateAndThrow` com mock de interface funcional, e mais seguro stubar o proprio `validateAndThrow`.
- Bloqueios e resolucao:
  - bloqueio: chamada inicial de retry com API incompatível (`maxAttempts`) em `RabbitMQConfig`;
  - resolucao: ajuste para API suportada (`maxRetries` + `backOffOptions`).
- Validacao final:
  - `./mvnw compile` com Java 21: sucesso.
  - `./mvnw test` com Java 21: sucesso (`171` testes, `0` falhas).
