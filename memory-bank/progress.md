# Progress — cal-service

**Última atualização**: 2026-05-20 (TASK020 concluida)

## Status Geral

| Área                          | Status           | Notas                                                  |
|-------------------------------|------------------|--------------------------------------------------------|
| Bounded Context `eventtype`   | ✅ Implementado   | CRUD, toggle, sync, testes unitários passando          |
| Bounded Context `appointment` | ✅ Implementado   | CRUD, ciclo de status, testes domínio e serviços passando |
| Framework `shared/error` (TASK011) | ✅ Concluída | Redesign + complemento pós-code review concluídos |
| Memory Bank (Workspace Copilot)| ✅ Fase 0 concluída | Arquivos core criados                                |
| Governança Copilot (.github/) | ✅ Fase 1 concluída | copilot-instructions.md, AGENTS.md, governance.instructions.md |
| Agents Copilot                | ✅ Fase 2 + TASK005 concluídas | context-architect, prompt-builder, task-refiner-governance, code-reviewer, test-designer, architect |
| Instructions por domínio      | ✅ Fase 3 concluída | java-spring, testing, security, api-contract          |
| Skills Copilot                | ✅ TASK006 concluída | task-checkpoint-memory (SKILL.md, template, referência) |
| Prompts Copilot               | ✅ TASK007 concluída | refinar-tarefa, criar-backlog-tarefa |
| Governança Contínua           | ✅ TASK008 concluída | checklist quinzenal, template retro, docs/governanca-continua.md |
| Infraestrutura REST (TASK012) | ✅ Concluída      | Factory/logging consolidados e contratos OpenAPI alinhados ao `ApiErrorResponse` |
| Testes de Contrato (TASK013)  | ✅ Concluída      | Validators cobertos com testes unitários + contrato HTTP do handler fechado com 405 |
| Semântica de Packages (TASK014) | ✅ Concluída     | Testes de eventtype reorganizados em paths/packages do contexto |
| Organização de Commits (TASK015) | ✅ Concluída    | Refatoração separada em commits temáticos para facilitar revisão |
| Documentação de API (TASK016) | ✅ Concluída      | Guia endpoint-by-endpoint com request/response, observações críticas e Mermaid |
| Paginação Event Types (TASK017) | ✅ Concluída     | `GET /event-types` paginado com `Page<T>` na application e contrato OpenAPI atualizado |
| Paginação Compartilhada (TASK018) | ✅ Concluída    | `Page<T>` centralizada em `shared.pagination` e fluxo de `ListAppointments` migrado |
| Consolidação de Errors + Review Fixes (TASK019) | ✅ Concluída | Enums de validação consolidados por contexto + correções de segurança, retry AMQP e mensagens de validação |
| CI/CD Pipelines (TASK020) | ✅ Concluída | Workflow do `penelope-api-rest` replicado em `authentication-service` e `cal-service`, com build/test/docker e tags Docker dedicadas |


## O Que Funciona

### Framework `shared/error` (TASK011 — redesenhado em 2026-04-19)

**Contrato unificado (`shared/error/core/`):**
- `ErrorContract` — interface única: `code()`, `messageTemplate()`, `type()`, `severity()`, `format()`.
- `ErrorType` — enum: `CORE | DOMAIN | GATEWAY | VALIDATION | APPLICATION`.
- `ErrorSeverity` — enum: `INFO | WARN | ERROR | CRITICAL`.
- `CoreError` — 16 constantes com códigos semânticos (ex: `CORE-NOT-FOUND`, não `CORE-404`).
- `DomainException` — exception para invariantes de domínio, not-found, transições de estado.
- `GatewayException` — exception para falhas de integração externa (Cal.com, monolito, auth).
- `ApplicationException` — exception para pré-condições de orquestração da aplicação.
- `ApiErrorResponse` — DTO de resposta: `of()`, `ofBeanValidation()` (400), `ofApplicationValidation()` (422).

**Transporte HTTP (`shared/error/http/`):**
- `ErrorHttpStatusMapping` — registry `ConcurrentHashMap<String, HttpStatus>`, zero imports de domínio.
- `HttpStatusResolver` — delega ao registry; fallback para 500.
- `CoreHttpStatusRegister` — `@Component` que registra as entradas de `CoreError`.
- `GlobalExceptionHandler` — `@RestControllerAdvice` com handlers para todas as exceptions tipadas.
- `EventTypeHttpStatusRegistrar` — `@Component` em `eventtype.infrastructure.error`.
- `AppointmentHttpStatusRegistrar` — `@Component` em `appointment.infrastructure.error`.

**Validação (`shared/validation/`):**
- `ValidationException` — carrega `List<ValidationError>`, lançada por `ValidationResult.throwIfHasErrors()`.
- `ValidationResult` — todos os métodos aceitam `ErrorContract` (não mais `ValidationCode`).
- `ValidationCode` — `@Deprecated` alias que `extends ErrorContract` (backward-compat).

### Bounded Context `eventtype`
- `CreateEventTypeService`, `GetEventTypeService`, `ListEventTypesService`, `ChangeEventTypeService`, `DeleteEventTypeService`, `ToggleEventTypeVisibilityService`, `SyncEventTypesService`.
- `EventTypeRepositoryAdapter`, `CalComEventTypeAdapter`, `MonolithEstateAdapter`.
- `SyncScheduler` / `SyncJob`.
- `EventTypeController` — REST API completa com Swagger.
- `GET /event-types` agora retorna payload paginado com metadados (`content`, `page`, `size`, `totalElements`, `totalPages`).
- Contrato de paginação usa `shared.pagination.Page` (sem duplicação por bounded context).
- `EventTypeError` — códigos semânticos; campo `ErrorType` por constante.
- Códigos de validação foram consolidados em `EventTypeError` (constantes `VALIDATION_*`).
- Testes unitários: todos passando.

### Bounded Context `appointment`
- `CreateAppointmentService`, `GetAppointmentService`, `ListAppointmentsService`, `ConfirmAppointmentService`, `ConcludeAppointmentService`, `CancelAppointmentService`, `RescheduleAppointmentService`, `DeleteAppointmentService`.
- `GET /appointments` agora usa o mesmo contrato de paginação compartilhado (`content`, `page`, `size`, `totalElements`, `totalPages`).
- `AppointmentError` — códigos semânticos; campo `ErrorType` por constante.
- Códigos de validação foram consolidados em `AppointmentError` (constantes `APPT-VAL-*`).
- `ListAppointmentsQueryValidator` — aceita `ErrorContract` via `ValidationResult` corrigido.
- Testes: todos passando.

### Infraestrutura
- Segurança JWT funcionando (`SecurityFilter`, `SecurityConfig`).
- OpenAPI/Swagger disponível em `/swagger-ui`.
- H2 Console disponível em `/h2-console` (dev).
- RabbitMQ consumer `EstateChangedConsumer` funcional.
- `RabbitMQConfig` usando `JacksonJsonMessageConverter` (sem deprecation).
- Build Maven configurado com JaCoCo para cobertura.

## O Que Falta Construir

### Média Prioridade
- [ ] Definir estratégia de migration de schema (Flyway/Liquibase).

## Problemas Conhecidos

- O ambiente local pode ter Java 8 como padrão; garantir `JAVA_HOME` com JDK 21 antes de rodar Maven.

## Métricas de Qualidade

- Última execução local (2026-04-20): `./mvnw compile` com sucesso em Java 21.
- Última execução local (2026-04-20): `./mvnw test` com sucesso (`171` testes, `0` falhas).
- TASK011 adicionou novos testes de contrato/hardening (`GlobalExceptionHandlerTest`, `ErrorHttpStatusMappingTest`, `SecurityFilterTest`, `LoggingInterceptorTest`).
- TASK013 adicionou testes unitários de validators (`eventtype` e `appointment`) e completou `GlobalExceptionHandlerTest` com cenário de método não permitido (`405`).
- TASK019 consolidou enums de validação em enums de erro por contexto e fechou comentários pendentes de review de segurança/retry/validação.
- Testes reportados em `target/surefire-reports/`.
- JaCoCo exec disponível em `target/jacoco.exec`.
- Relatório HTML em `target/site/jacoco/`.
