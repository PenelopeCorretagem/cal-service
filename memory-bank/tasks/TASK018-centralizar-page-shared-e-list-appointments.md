# TASK018 - Centralizar Page em shared e aplicar em ListAppointments

**Status:** Completed
**Added:** 2026-04-19
**Updated:** 2026-04-19

## Original Request

Colocar `Page` no shared e mudar o fluxo de `ListAppointments` para usar Page tambem.

## Thought Process

A mudanca precisava remover duplicacao de contrato de paginacao entre bounded contexts e manter Clean Architecture. A abordagem escolhida foi:
- criar `Page<T>` generica em `shared`;
- migrar `eventtype` para importar a classe compartilhada;
- migrar o retorno de `ListAppointments` para `Page<AppointmentOutput>` em use case, service e controller;
- atualizar OpenAPI e guia de integracao para refletir o payload com `content`.

## Implementation Plan

- [x] Criar `shared.pagination.Page<T>`.
- [x] Migrar imports de `eventtype` para a Page compartilhada.
- [x] Alterar fluxo de `ListAppointments` para retornar `Page<AppointmentOutput>`.
- [x] Atualizar contratos OpenAPI e exemplos JSON.
- [x] Ajustar testes unitarios de services impactados.

## Progress Tracking

**Overall Status:** Completed - 100%

### Subtasks
| ID | Description | Status | Updated | Notes |
|----|-------------|--------|---------|-------|
| TASK018.1 | Criar Page compartilhada | Completed | 2026-04-19 | `shared/pagination/Page.java` criada |
| TASK018.2 | Migrar fluxo de EventTypes | Completed | 2026-04-19 | Imports ajustados para `shared.pagination.Page` |
| TASK018.3 | Migrar fluxo de ListAppointments | Completed | 2026-04-19 | Use case/service/controller/swagger retornando `Page<AppointmentOutput>` |
| TASK018.4 | Atualizar testes e docs | Completed | 2026-04-19 | Testes de services e guia de integracao atualizados |

## Progress Log

### 2026-04-19
- Criada `Page<T>` em `src/main/java/com/penelopec/calservice/shared/pagination/Page.java`.
- `eventtype` atualizado para usar `Page` compartilhada (use case, service, controller, swagger e teste de service).
- Fluxo de `ListAppointments` atualizado para usar `Page<AppointmentOutput>` em:
  - `ListAppointmentsUseCase`
  - `ListAppointmentsService`
  - `AppointmentController`
  - `AppointmentControllerSwagger`
  - `ListAppointmentsServiceTest`
- Guia de integracao atualizado em `docs/guia-integracao-api.md` para usar `content` no retorno de `GET /appointments`.
- Validacao: arquivos alterados sem erros na analise de problemas.
- Build global (`./mvnw compile`) segue bloqueado por erro preexistente fora do escopo em `RabbitMQConfig` (`maxAttempts(int)` nao encontrado).
