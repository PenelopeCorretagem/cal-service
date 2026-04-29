# TASK014 - Reestruturar packages de testes e shared

**Status:** Completed
**Added:** 2026-04-20
**Updated:** 2026-04-20

## Original Request

Fazer uma varredura nos packages dos testes e do módulo shared e reestruturar para manter a semântica.

## Thought Process

A principal inconsistência era semântica: testes do bounded context `eventtype` estavam em packages genéricos (`com.penelopec.calservice.application|domain|infrastructure`) enquanto a produção já está organizada em `com.penelopec.calservice.eventtype.*`.

No módulo `shared`, a estrutura de packages estava consistente; foi mantido apenas o placeholder legado `shared.error.handler.GlobalExceptionHandler` por compatibilidade, sem uso ativo.

## Implementation Plan

- [x] Mapear inconsistências de package/path nos testes e em `shared`.
- [x] Reestruturar os testes de `eventtype` para packages e caminhos semânticos do bounded context.
- [x] Validar com `./mvnw test` em Java 21.

## Progress Tracking

**Overall Status:** Completed - 100%

### Subtasks

| ID | Description | Status | Updated | Notes |
|-----|-------------|--------|---------|-------|
| TASK014.1 | Varredura de packages em testes e shared | Completed | 2026-04-20 | Detectadas inconsistências em testes legados de eventtype |
| TASK014.2 | Reestruturação de paths/packages dos testes eventtype | Completed | 2026-04-20 | 14 arquivos movidos e alinhados semanticamente |
| TASK014.3 | Validação de build/testes | Completed | 2026-04-20 | `./mvnw test` com Java 21: 148 testes, 0 falhas |

## Progress Log

### 2026-04-20 - Checkpoint 2 (Implementação concluída)
- Testes de `eventtype` foram movidos para paths semânticos do contexto:
  - `eventtype/application/service`
  - `eventtype/domain/{entity,valueobject}`
  - `eventtype/infrastructure/{messaging,persistence/web}`
- Package declarations foram atualizados para refletir os novos caminhos.
- Varredura no módulo `shared` não encontrou necessidade de alteração estrutural adicional; semântica preservada.
- Validação executada com Java 21:
  - `./mvnw test` -> BUILD SUCCESS
  - 148 testes executados, 0 failures, 0 errors.

## Entregas

- Reorganização semântica de packages de testes do `eventtype` concluída.
- Semântica do módulo `shared` verificada e mantida.
- Suíte de testes íntegra após a reestruturação.
