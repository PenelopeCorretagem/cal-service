# TASK007 - Camada de Prompts Reutilizáveis

**Status:** Completed
**Added:** 2026-04-05
**Updated:** 2026-04-05

## Original Request

Refinar a Fase 4: criar camada de prompts reutilizáveis em `.github/prompts/`.

## Thought Process

Dois prompts foram identificados no plano original como essenciais:
- `refinar-tarefa.prompt.md`: parametriza o fluxo do `task-refiner-governance` agent para demandas brutas.
- `criar-backlog-tarefa.prompt.md`: decomposição de tarefa grande em subtarefas atômicas com dependências.

Eles reduzem o atrito ao iniciar qualquer ciclo de trabalho — o usuário não precisa lembrar do formato exato do artefato de refinamento.

**Dependência de TASK005**: os prompts devem produzir saída compatível com os agents; os agents precisam existir como referência.

## Implementation Plan

- [x] Criar pasta `.github/prompts/`
- [x] Criar `.github/prompts/refinar-tarefa.prompt.md`
- [x] Criar `.github/prompts/criar-backlog-tarefa.prompt.md`
- [x] Referenciar os prompts em `AGENTS.md` ou `copilot-instructions.md`

## Progress Tracking

**Overall Status:** Completed — 100%

### Subtasks

| ID  | Description | Status | Updated | Notes |
|-----|-------------|--------|---------|-------|
| 7.1 | Criar pasta .github/prompts/ | Completed | 2026-04-05 | Pasta já existia com README |
| 7.2 | refinar-tarefa.prompt.md | Completed | 2026-04-05 | Parametrizado: {demanda}, {bounded_context}, {contexto_adicional} |
| 7.3 | criar-backlog-tarefa.prompt.md | Completed | 2026-04-05 | Parametrizado: {tarefa_grande}, {criterios_aceite} |
| 7.4 | Referenciar prompts na documentação | Completed | 2026-04-05 | Seção 'Prompts Disponíveis' adicionada ao AGENTS.md |

## Progress Log

### 2026-04-05
- Tarefa criada a partir do refinamento da Fase 4 documentado em `docs/fase4-refinamento.md`.
- Dependência identificada: TASK005 deve ser concluída antes (agents como referência de compatibilidade).
- TASK005 e TASK006 concluídas; TASK007 desbloqueada e implementada.
- `refinar-tarefa.prompt.md` criado: modo `agent`, 3 parâmetros de entrada, instrui leitura do Memory Bank antes de refinar, produz artefato no formato do task-refiner-governance agent, e registra no Memory Bank após aprovação.
- `criar-backlog-tarefa.prompt.md` criado: modo `agent`, 2 parâmetros de entrada, decompõe tarefa em subtarefas atômicas mapeadas às camadas da Clean Architecture com tabela de ID/camada/dependência/prioridade.
- Seção 'Prompts Disponíveis' adicionada ao `AGENTS.md` com tabela de 2 prompts.
