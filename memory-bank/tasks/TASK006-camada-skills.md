# TASK006 - Camada de Skills (task-checkpoint-memory)

**Status:** Completed
**Added:** 2026-04-05
**Updated:** 2026-04-05

## Original Request

Refinar a Fase 4: expandir skills com templates e referências reutilizáveis. Começar pela skill `task-checkpoint-memory`.

## Thought Process

A pasta `.github/skills/` existe mas está vazia. A skill de checkpoint é o ponto de entrada natural: é usada em todos os outros fluxos (agents, prompts, revisão quinzenal). Sem ela, o ciclo de governança contínua (TASK008) não tem instrumento formal.

Estrutura planejada:
```
.github/skills/task-checkpoint-memory/
├── SKILL.md                          ← instrução principal da skill
├── templates/
│   └── checkpoint-template.md        ← template de checkpoint
└── references/
    └── memory-governance.md          ← regras extraídas das instructions
```

## Implementation Plan

- [x] Criar `.github/skills/task-checkpoint-memory/SKILL.md`
- [x] Criar `.github/skills/task-checkpoint-memory/templates/checkpoint-template.md`
- [x] Criar `.github/skills/task-checkpoint-memory/references/memory-governance.md`

## Progress Tracking

**Overall Status:** Completed — 100%

### Subtasks

| ID  | Description | Status | Updated | Notes |
|-----|-------------|--------|---------|-------|
| 6.1 | SKILL.md principal | Completed | 2026-04-05 | frontmatter com description localizável; workflow em 4 etapas |
| 6.2 | checkpoint-template.md | Completed | 2026-04-05 | específico para cal-service com seções de decisões, lições e próximos passos |
| 6.3 | memory-governance.md | Completed | 2026-04-05 | regras extraídas de governance.instructions.md + memory-bank.instructions.md |

## Progress Log

### 2026-04-05
- Tarefa criada a partir do refinamento da Fase 4 documentado em `docs/fase4-refinamento.md`.

### 2026-04-05 (Checkpoint 2 — Implementação concluída)
- `SKILL.md` criado com frontmatter de description localizável, tabela de tipos de checkpoint, workflow de 4 etapas e listagem de saída esperada.
- `templates/checkpoint-template.md` criado com placeholders específicos para cal-service: contexto, ações, decisões (tabela), lições, bloqueios, próximos passos e referência de arquivos modificados.
- `references/memory-governance.md` criado consolidando 8 seções de regras extraídas de `governance.instructions.md` e `memory-bank.instructions.md`.
