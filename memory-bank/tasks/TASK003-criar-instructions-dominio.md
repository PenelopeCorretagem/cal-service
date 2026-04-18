# TASK003 - Criar Instructions por Domínio (Fase 3)

**Status:** Completed
**Added:** 2026-04-05
**Updated:** 2026-04-05

## Original Request

Implementar a Fase 3 do plano de workspace GitHub Copilot: criar instructions por domínio em `.github/instructions/`.

## Thought Process

A Fase 3 adiciona instructions específicas por domínio técnico, complementando as regras gerais já existentes em `governance.instructions.md` e `copilot-instructions.md`. Cada arquivo foi desenhado com `applyTo` direcionado para minimizar conflitos e maximizar relevância contextual:

- `java-spring`: aplica a todo código de produção (`src/main/java/**`)
- `testing`: aplica a arquivos de teste (`src/test/java/**`)
- `security`: aplica a todo código Java (`src/**`) — segurança é transversal
- `api-contract`: aplica a controllers, Swagger interfaces, Requests e Outputs

Evitou-se duplicar conteúdo já coberto pelo `copilot-instructions.md`; cada arquivo aprofunda seu domínio específico.

## Implementation Plan

- [x] Criar `java-spring.instructions.md` — Clean Architecture, Lombok, MapStruct, factory methods
- [x] Criar `testing.instructions.md` — JUnit 5, Mockito, nomenclatura, cobertura
- [x] Criar `security.instructions.md` — OWASP Top 10, JWT, validação, gatilhos de risco
- [x] Criar `api-contract.instructions.md` — HTTP verbs, status codes, OpenAPI, controllers
- [x] Atualizar Memory Bank

## Progress Tracking

**Overall Status:** Completed - 100%

### Subtasks
| ID  | Description | Status | Updated | Notes |
|-----|-------------|--------|---------|-------|
| 3.1 | java-spring.instructions.md | Complete | 2026-04-05 | applyTo: src/main/java/**/*.java |
| 3.2 | testing.instructions.md | Complete | 2026-04-05 | applyTo: src/test/java/**/*.java |
| 3.3 | security.instructions.md | Complete | 2026-04-05 | applyTo: src/**/*.java |
| 3.4 | api-contract.instructions.md | Complete | 2026-04-05 | applyTo: *Controller*, *Swagger*, *Request*, *Output* |
| 3.5 | Memory Bank atualizado | Complete | 2026-04-05 | activeContext, progress, _index |

## Progress Log
### 2026-04-05
- Criados os 4 arquivos de instructions em `.github/instructions/`.
- `java-spring`: foco em Clean Architecture, entidades, use cases, command/output, Lombok, MapStruct, Value Objects.
- `testing`: foco em JUnit 5 + Mockito, nomenclatura, Given/When/Then, cobertura mínima, restrição do @SpringBootTest.
- `security`: foco em OWASP Top 10, JWT, validação @Valid, CORS, SQL injection, gatilhos de revisão.
- `api-contract`: foco em verbos HTTP, status codes, SpringDoc/OpenAPI obrigatório, controllers como adaptadores puros, records para request/output.
- Memory Bank atualizado: tarefa, índice, activeContext e progress.
