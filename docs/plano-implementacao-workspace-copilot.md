# Plano de Implementacao do Workspace GitHub Copilot

## Contexto

Este plano adapta as praticas atuais do repositorio [github/awesome-copilot](https://github.com/github/awesome-copilot) para o projeto `cal-service`, priorizando:

- Agents especializados por fluxo.
- Rules e governanca por instrucoes.
- Skills para tarefas recorrentes.
- Prompts reutilizaveis.
- Banco de memoria com checkpoints.

## Diagnostico Atual do Workspace

### Estrutura observada

- Projeto Spring Boot (Java 21, Maven, testes com Surefire/JaCoCo).
- Pasta `.github/` existente.
- Pasta `.github/skills/` existe, mas esta vazia.
- Nao foram encontrados ainda arquivos de customizacao do Copilot:
  - `.github/copilot-instructions.md`
  - `.github/agents/*.agent.md`
  - `.github/instructions/*.instructions.md`
  - `.github/prompts/*.prompt.md`
  - `AGENTS.md`

### Lacunas principais

- Falta um baseline de governanca para uso de agentes.
- Falta um fluxo padrao para refinamento e quebra de tarefas.
- Nao existe padrao de checkpoint para consolidacao de aprendizado no memory bank.
- Nao existe catalogo inicial de skills e prompts para o time.

## Referencia de "como esta sendo feito hoje" (awesome-copilot)

Padroes observados no repositorio de referencia:

- Organizacao por primitivas claras: `agents/`, `instructions/`, `skills/`, `hooks/`, `workflows/`, `plugins/`.
- Forte uso de frontmatter e convencoes de nomenclatura (lowercase com hifens).
- `description` tratada como campo critico para descoberta de agentes/skills.
- Separacao de responsabilidades:
  - Agent para persona e orquestracao.
  - Instructions para regras persistentes por escopo (`applyTo`).
  - Skill para workflow repetivel com possiveis assets.
  - Prompt para tarefa focada e reutilizavel.
- Recomendacao de customizacao em nivel de repositorio dentro de `.github/` para padronizacao da equipe.

## Objetivo Prioritario

Criar primeiro o fluxo completo de **Memory Bank** no padrao da instruction, para depois plugar os agents e demais artefatos com rastreabilidade e continuidade entre sessoes.

## Arquitetura Alvo (Primeira Iteracao)

### 1. Camada de memory bank (prioridade 1)

Padrao base (instruction anexada): pasta `memory-bank/` no root com arquivos obrigatorios e tarefas.

- `memory-bank/projectbrief.md`
- `memory-bank/productContext.md`
- `memory-bank/activeContext.md`
- `memory-bank/systemPatterns.md`
- `memory-bank/techContext.md`
- `memory-bank/progress.md`
- `memory-bank/tasks/_index.md`
- `memory-bank/tasks/TASKID-taskname.md`

### 2. Camada base de governanca

- `.github/copilot-instructions.md`
  - Regras gerais do repositorio (arquitetura, qualidade, seguranca, testes, definicao de pronto).
- `AGENTS.md`
  - Contexto de projeto para agentes (padrao portavel cross-tool).
- `.github/instructions/governance.instructions.md`
  - Politicas de execucao, risco, limites e rastreabilidade.
- `.github/instructions/memory-bank.instructions.md`
  - Regra operacional para leitura e atualizacao obrigatoria do memory bank.

### 3. Camada de agents (base inicial)

- `.github/agents/context-architect.agent.md` (base no anexo)
  - Papel: mapear contexto, dependencias e sequencia antes de editar.
- `.github/agents/prompt-builder.agent.md` (base no anexo)
  - Papel: criar e validar prompts e instrucoes com criterio.
- `.github/agents/task-refiner-governance.agent.md` (fluxo principal)
  - Papel: transformar demanda bruta em backlog executavel.
  - Entregas obrigatorias:
    - Objetivo e escopo.
    - Criterios de aceite.
    - Dependencias e riscos.
    - Plano de implementacao por etapas.
    - Checkpoints de memoria.

### 4. Camada de skills

- `.github/skills/task-checkpoint-memory/SKILL.md`
  - Skill para registrar checkpoints de aprendizado e decisoes.
- Assets opcionais:
  - `.github/skills/task-checkpoint-memory/templates/checkpoint-template.md`
  - `.github/skills/task-checkpoint-memory/references/memory-governance.md`

### 5. Camada de prompts

- `.github/prompts/refinar-tarefa.prompt.md`
  - Prompt parametrizado para refinamento.
- `.github/prompts/criar-backlog-tarefa.prompt.md`
  - Prompt para decomposicao em tarefas pequenas com dependencias.

## Fluxo Operacional (Memory Bank First)

1. Ler todos os arquivos obrigatorios de `memory-bank/` antes de qualquer planejamento.
2. Se faltar arquivo core, criar e registrar estado inicial do projeto.
3. Criar/atualizar tarefa em `memory-bank/tasks/` com ID unico e refletir em `tasks/_index.md`.
4. Executar refinamento da demanda e gerar plano de implementacao rastreavel.
5. Atualizar progresso durante a execucao:
  - status geral,
  - subtarefas,
  - log de progresso,
  - indice de tarefas.
6. Consolidar checkpoints de memoria:
  - Checkpoint 0: contexto inicial.
  - Checkpoint 1: plano aprovado.
  - Checkpoint 2: implementacao concluida.
  - Checkpoint 3: retro e licoes aprendidas.
7. Atualizar `activeContext.md` e `progress.md` ao final de cada ciclo.

## Plano de Implementacao (Roadmap)

### Fase 0 - Memory Bank Foundation (Dia 1)

- Criar a estrutura `memory-bank/` no padrao da instruction.
- Criar arquivos core e pasta `tasks/` com `_index.md`.
- Definir template de tarefa (`TASKID-taskname.md`) e de checkpoint.
- Registrar baseline em `projectbrief.md`, `activeContext.md` e `progress.md`.

### Fase 1 - Governanca e Bootstrap (Dia 1-2)

- Criar estrutura base em `.github/`:
  - `copilot-instructions.md`
  - `agents/`
  - `instructions/`
  - `prompts/`
- Criar `AGENTS.md` no root apontando para as regras de engenharia.
- Criar `memory-bank.instructions.md` em `.github/instructions/` para enforcement.

### Fase 2 - Agents Base e Fluxo de Tarefas (Dia 2-3)

- Adaptar e publicar `context-architect.agent.md` na pasta `.github/agents/`.
- Adaptar e publicar `prompt-builder.agent.md` na pasta `.github/agents/`.
- Implementar `task-refiner-governance.agent.md` com checkpoints obrigatorios.
- Integrar agents com o fluxo de tarefas do `memory-bank/tasks/`.

### Fase 3 - Regras e Qualidade (Dia 3-4)

- Adicionar instructions por dominio:
  - `java-spring.instructions.md`
  - `testing.instructions.md`
  - `security.instructions.md`
  - `api-contract.instructions.md`
- Definir gatilhos de revisao de risco e evidencias minimas.

### Fase 4 - Escala e Governanca Continua (Dia 4+)

- Criar novos agents especializados (ex.: reviewer, test-designer, architect).
- Expandir skills com templates e referencias reutilizaveis.
- Revisao quinzenal do memory bank para convergir em novas rules/instructions.

## Definicao de Pronto por Fase

- Fase 0 concluida quando todos os arquivos core do memory bank existirem e estiverem preenchidos com baseline.
- Fase 1 concluida quando governanca base estiver ativa em `.github/` e `AGENTS.md`.
- Fase 2 concluida quando os 3 agents base estiverem publicados e usando `memory-bank/tasks/`.
- Fase 3 concluida quando as instructions por dominio estiverem aplicadas e sem conflito.
- Fase 4 concluida quando houver ciclo continuo de aprendizado (checkpoints -> rules -> melhoria).

## Riscos e Mitigacoes

- Risco: excesso de instrucoes genericas e conflitantes.
  - Mitigacao: `applyTo` especifico e ownership por arquivo.
- Risco: agente sem foco operacional.
  - Mitigacao: definir formato de saida obrigatorio e limites de escopo.
- Risco: memory bank virar historico morto.
  - Mitigacao: checkpoints curtos, acionaveis e com revisao recorrente.

## Entregavel Imediato Recomendado (proximo passo)

Implementar na sequencia:

1. `memory-bank/projectbrief.md`
2. `memory-bank/productContext.md`
3. `memory-bank/systemPatterns.md`
4. `memory-bank/techContext.md`
5. `memory-bank/activeContext.md`
6. `memory-bank/progress.md`
7. `memory-bank/tasks/_index.md`
8. `.github/instructions/memory-bank.instructions.md`
9. `.github/agents/context-architect.agent.md`
10. `.github/agents/prompt-builder.agent.md`
