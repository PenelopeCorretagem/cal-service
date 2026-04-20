# TASK017 - Paginar endpoint de listagem de event types

**Status:** Completed
**Added:** 2026-04-19
**Updated:** 2026-04-19

## Original Request

Gerar commit da alteracao atual, mas antes ajustar o endpoint `GET /event-types` para ser sempre paginado e criar uma classe `Page` na camada application para evitar uso indevido de `Page` do Spring.

## Thought Process

A mudanca precisava preservar a Clean Architecture e evitar acoplamento da camada application com classes do Spring Data. A abordagem escolhida foi criar uma pagina generica em `eventtype.application.output`, migrar o use case de listagem para receber `page/size` e retornar pagina com metadados, mantendo o controller como adaptador simples.

## Implementation Plan

- [x] Criar classe `Page<T>` em `eventtype.application.output`.
- [x] Atualizar `ListEventTypesUseCase` e `ListEventTypesService` para paginação.
- [x] Atualizar `EventTypeController` e `EventTypeControllerSwagger` para `page/size`.
- [x] Atualizar testes unitarios de `ListEventTypesService`.
- [x] Sincronizar documentacao de integracao e rastreabilidade no Memory Bank.

## Progress Tracking

**Overall Status:** Completed - 100%

### Subtasks
| ID | Description | Status | Updated | Notes |
|----|-------------|--------|---------|-------|
| TASK017.1 | Criar Page generica na application | Completed | 2026-04-19 | `Page<T>` com metadados e slice local |
| TASK017.2 | Migrar use case/service para paginação | Completed | 2026-04-19 | `execute(page, size)` com retorno paginado |
| TASK017.3 | Atualizar contrato REST e OpenAPI | Completed | 2026-04-19 | `GET /event-types` com query params `page/size` |
| TASK017.4 | Ajustar testes unitarios de listagem | Completed | 2026-04-19 | Cobertura para pagina, vazio e defaults |
| TASK017.5 | Atualizar docs e Memory Bank | Completed | 2026-04-19 | Seção EVT-03 e arquivos de contexto atualizados |

## Progress Log

### 2026-04-19
- Criada classe `Page<T>` em `eventtype.application.output` para resposta paginada sem dependência de Spring Data na application.
- `ListEventTypesUseCase` alterado para `execute(int page, int size)` e `ListEventTypesService` ajustado para retorno de página.
- `EventTypeController` alterado para aceitar `page` e `size` via query params com defaults (`0` e `20`).
- `EventTypeControllerSwagger` atualizado com novo contrato, parâmetros e exemplos de resposta paginada.
- Guia de integração atualizado em `docs/guia-integracao-api.md` (EVT-03).
- Validação: `./mvnw compile` executado com sucesso em Java 21.
- Observação: `./mvnw test` falhou por erros preexistentes fora de escopo em testes de validators/appointment/eventtype (símbolos de enums de erro ausentes).
