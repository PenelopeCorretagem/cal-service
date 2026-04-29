# TASK008 - Ciclo de Governança Contínua

**Status:** Completed
**Added:** 2026-04-05
**Updated:** 2026-04-05

## Original Request

Refinar a Fase 4: revisão quinzenal do memory bank para convergir em novas rules/instructions. Formalizar o ciclo de governança contínua.

## Thought Process

Sem um processo explícito, o Memory Bank vira histórico morto e as instructions ficam obsoletas. O ciclo de governança contínua é o "sistema imunológico" do workspace: garante que lições aprendidas em sessões reais se convertam em regras formais que beneficiam sessões futuras.

Fluxo desejado:
```
Lição identificada (sessão real)
  → checkpoint registrado (skill TASK006)
    → revisão quinzenal (este processo)
      → instruction/prompt/agent atualizado
        → Memory Bank reflete mudança
```

Depende de TASK006 (skill) e TASK007 (prompts) para ter os instrumentos do ciclo disponíveis.

## Implementation Plan

- [x] Criar checklist de revisão quinzenal (máx. 10 itens)
- [x] Criar template de retrospectiva de ciclo
- [x] Documentar processo de convergência em `docs/governanca-continua.md`
- [x] Atualizar `activeContext.md` com o processo em vigor

## Progress Tracking

**Overall Status:** Completed — 100%

### Subtasks

| ID  | Description | Status | Updated | Notes |
|-----|-------------|--------|---------|-------|
| 8.1 | Checklist de revisão quinzenal | Completed | 2026-04-05 | Seção 2 de `docs/governanca-continua.md` (10 itens) |
| 8.2 | Template de retrospectiva | Completed | 2026-04-05 | `.github/skills/task-checkpoint-memory/templates/retrospectiva-ciclo-template.md` |
| 8.3 | docs/governanca-continua.md | Completed | 2026-04-05 | Processo completo de convergência, critérios, mapeamento e regras de limpeza |
| 8.4 | Atualizar Memory Bank | Completed | 2026-04-05 | activeContext.md + progress.md + _index.md |

## Progress Log

### 2026-04-05
- Tarefa criada a partir do refinamento da Fase 4 documentado em `docs/fase4-refinamento.md`.
- Dependências identificadas: TASK006 e TASK007 devem ser concluídas antes.

### 2026-04-05 (conclusão)
- `docs/governanca-continua.md` criado com: checklist quinzenal (10 itens), processo de convergência com critérios de aprovação, mapeamento lição→arquivo, instrumentos do ciclo, regras de limpeza do Memory Bank e armazenamento de retrospectivas.
- `.github/skills/task-checkpoint-memory/templates/retrospectiva-ciclo-template.md` criado com 7 seções: itens revisados, lições coletadas, convergência, entradas obsoletas, decisões, próximos passos e métricas.
- Memory Bank atualizado: TASK008 Completed, _index.md, activeContext.md, progress.md.
- Fase 4 concluída.
