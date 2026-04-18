---
name: task-checkpoint-memory
description: >
  Registra checkpoints de aprendizado, decisões e lições no Memory Bank do cal-service.
  Use esta skill sempre que uma fase de implementação for concluída, uma decisão arquitetural
  importante for tomada, ou um ciclo de revisão quinzenal for iniciado.
---

# Skill: task-checkpoint-memory

## Quando Usar Esta Skill

Use esta skill nos seguintes momentos:

1. **Ao concluir uma subtarefa ou fase** — registrar o que foi feito, decidido e aprendido.
2. **Ao tomar uma decisão arquitetural significativa** — preservar o raciocínio para sessões futuras.
3. **Ao encontrar um bloqueio ou risco** — documentar o problema e a solução adotada.
4. **Durante revisão quinzenal** — consolidar lições em regras formais.
5. **Ao final de qualquer tarefa do Memory Bank** — checkpoint de encerramento obrigatório.

## Pré-condições

Antes de registrar um checkpoint, verifique:

- [ ] Os arquivos core do Memory Bank foram lidos (ver [memory-governance.md](./references/memory-governance.md)).
- [ ] A tarefa correspondente existe em `memory-bank/tasks/TASKID-nome.md`.
- [ ] O status da tarefa está atualizado em `memory-bank/tasks/_index.md`.

## Workflow Passo a Passo

### Etapa 1 — Identificar o tipo de checkpoint

| Tipo | Gatilho | Arquivo a atualizar |
|------|---------|---------------------|
| Checkpoint 0 | Início de tarefa | `tasks/TASKID.md` + `activeContext.md` |
| Checkpoint 1 | Plano aprovado | `tasks/TASKID.md` + `_index.md` |
| Checkpoint 2 | Implementação concluída | `tasks/TASKID.md` + `progress.md` + `activeContext.md` |
| Checkpoint 3 | Retro / lições aprendidas | `tasks/TASKID.md` + `progress.md` + instruction relevante (se aplicável) |

### Etapa 2 — Preencher o template de checkpoint

Use o template em [templates/checkpoint-template.md](./templates/checkpoint-template.md).

Campos obrigatórios:
- **Data**: formato `YYYY-MM-DD`.
- **Tipo**: Checkpoint 0 / 1 / 2 / 3.
- **Decisões tomadas**: ao menos uma entrada (ou "nenhuma decisão nova").
- **Lições aprendidas**: ao menos uma entrada (ou "nenhuma nova lição").
- **Próximos passos**: lista de ações concretas.

### Etapa 3 — Atualizar os arquivos do Memory Bank

Dependendo do tipo de checkpoint, atualize os arquivos correspondentes:

**Checkpoint 0 e 1 — Início e plano aprovado:**
```
memory-bank/tasks/TASKID-nome.md  → adicionar entrada no Progress Log
memory-bank/tasks/_index.md        → mover tarefa para "In Progress"
memory-bank/activeContext.md       → atualizar "Foco Atual" e "Próximos Passos"
```

**Checkpoint 2 — Implementação concluída:**
```
memory-bank/tasks/TASKID-nome.md  → marcar subtarefas como concluídas + log
memory-bank/tasks/_index.md        → atualizar status para "Completed"
memory-bank/progress.md            → registrar o que foi entregue
memory-bank/activeContext.md       → atualizar foco e próximos passos
```

**Checkpoint 3 — Retro e lições:**
```
memory-bank/tasks/TASKID-nome.md  → adicionar seção de lições aprendidas no log
memory-bank/progress.md            → atualizar seção de status geral
.github/instructions/<relevante>   → atualizar se lição virar regra formal
```

### Etapa 4 — Verificar convergência (somente Checkpoint 3)

Pergunte: **"Esta lição deveria virar uma instruction ou regra formal?"**

Critério de convergência:
- Se o problema ocorreu mais de uma vez → candidato a instruction.
- Se a decisão impacta todos os bounded contexts → candidato a `copilot-instructions.md`.
- Se é específica de domínio (eventtype / appointment) → candidato a instruction de domínio.

Se sim, atualize o arquivo de instruction relevante em `.github/instructions/`.

## Saída Esperada

Após executar a skill, os seguintes artefatos devem estar atualizados:

```
memory-bank/
├── activeContext.md          ← foco e próximos passos atualizados
├── progress.md               ← (checkpoint 2 e 3) entregas registradas
└── tasks/
    ├── _index.md             ← status da tarefa atualizado
    └── TASKID-nome.md        ← Progress Log com nova entrada
```

## Referências

- [memory-governance.md](./references/memory-governance.md) — regras de governança e atualização do Memory Bank.
- [checkpoint-template.md](./templates/checkpoint-template.md) — template para preenchimento.
- [governance.instructions.md](../../instructions/governance.instructions.md) — políticas completas de execução.
- [memory-bank.instructions.md](../../instructions/memory-bank.instructions.md) — workflow obrigatório do Memory Bank.
