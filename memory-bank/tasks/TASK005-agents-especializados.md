# TASK005 - Agents Especializados (Fase 4)

**Status:** Completed
**Added:** 2026-04-05
**Updated:** 2026-04-05

## Original Request

Refinar a Fase 4 do plano `plano-implementacao-workspace-copilot.md`: criar novos agents especializados (reviewer, test-designer, architect).

## Thought Process

Os agents base (context-architect, prompt-builder, task-refiner-governance) cobrem o fluxo de planejamento e refinamento. A Fase 4 exige agents que atuem na **execução** — revisão de código e design de testes — e no **suporte arquitetural** — avaliação de impacto de mudanças.

Distinção importante:
- `context-architect`: mapeia contexto **antes** de agir.
- `architect`: avalia **viabilidade e impacto** arquitetural (novo).
- `code-reviewer`: avalia **resultado** após a implementação (novo).
- `test-designer`: projeta **estratégia de testes** para uma mudança (novo).

## Implementation Plan

- [x] Criar `.github/agents/code-reviewer.agent.md`
- [x] Criar `.github/agents/test-designer.agent.md`
- [x] Criar `.github/agents/architect.agent.md`
- [x] Atualizar `AGENTS.md` com os novos agents

## Progress Tracking

**Overall Status:** Completed — 100%

### Subtasks

| ID  | Description | Status | Updated | Notes |
|-----|-------------|--------|---------|-------|
| 5.1 | code-reviewer.agent.md | Completed | 2026-04-05 | Frontmatter, checklist OWASP + Clean Architecture, tabela de findings |
| 5.2 | test-designer.agent.md | Completed | 2026-04-05 | Pirâmide de testes, JUnit 5+Mockito, tabela de plano |
| 5.3 | architect.agent.md | Completed | 2026-04-05 | Mapa de impacto arquitetural, veredicto, distinção clara dos outros agents |
| 5.4 | Atualizar AGENTS.md | Completed | 2026-04-05 | Tabela de 6 agents com papel de cada um |

## Progress Log

### 2026-04-05
- Tarefa criada a partir do refinamento da Fase 4 documentado em `docs/fase4-refinamento.md`.
- Implementação concluída: 3 agents criados e AGENTS.md atualizado.
  - `code-reviewer.agent.md`: checklist em 5 dimensões (arquitetura, domínio, mapeamento, segurança OWASP, API REST, testes), tabela de findings com severidades CRITICAL/MAJOR/MINOR/INFO.
  - `test-designer.agent.md`: pirâmide de testes por camada, padrões JUnit 5+Mockito, tabela de plano de testes com prioridade.
  - `architect.agent.md`: 7 perguntas-guia de avaliação, mapa de impacto por camada, breaking changes checklist, veredicto formal.
  - `AGENTS.md`: tabela de 6 agents com papel distinto de cada um adicionada antes da seção de Governança.
