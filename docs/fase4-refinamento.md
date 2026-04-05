# Refinamento da Fase 4 — Escala e Governança Contínua

> Documento derivado do [plano-implementacao-workspace-copilot.md](./plano-implementacao-workspace-copilot.md).  
> Fase 4 era descrita de forma muito genérica. Este arquivo decompõe cada entregável em tarefas rastreáveis.

---

## Contexto

As Fases 0–3 entregaram a base do workspace Copilot:

| Fase | Entregável | Status |
|------|------------|--------|
| 0 | Memory Bank Foundation | Concluído |
| 1 | Governança base `.github/` | Concluído |
| 2 | Agents base (context-architect, prompt-builder, task-refiner-governance) | Concluído |
| 3 | Instructions por domínio (java-spring, testing, security, api-contract) | Concluído |

A Fase 4 representa a **maturação** do workspace: novos agents especializados, skills reutilizáveis, prompts parametrizados e um ciclo sustentável de governança contínua.

---

## Visão Geral das Tarefas da Fase 4

| ID | Nome | Entregável Principal | Dependências |
|----|------|----------------------|--------------|
| TASK005 | Agents especializados | reviewer, test-designer, architect agents | TASK002 (agents base concluído) |
| TASK006 | Camada de Skills | task-checkpoint-memory SKILL.md + assets | TASK001 (governança base concluída) |
| TASK007 | Camada de Prompts | refinar-tarefa, criar-backlog-tarefa | TASK005 (agents existindo como referência) |
| TASK008 | Ciclo de governança contínua | checklist + processo de revisão quinzenal | TASK006, TASK007 |

---

## Refinamento da Tarefa: TASK005 — Agents Especializados

### Objetivo e Escopo

Criar três agents especializados complementares aos agents base já existentes. Cada agent terá persona, ferramentas e formato de saída definidos. Escopo: bounded context `eventtype` e `appointment` do `cal-service`.

### Critérios de Aceite

- [ ] `code-reviewer.agent.md` publicado em `.github/agents/` com `description`, `model`, `tools` e instruções de revisão de código alinhadas à Clean Architecture e OWASP Top 10.
- [ ] `test-designer.agent.md` publicado em `.github/agents/` com foco em cobertura de use cases, factory methods e adapters.
- [ ] `architect.agent.md` publicado em `.github/agents/` capaz de avaliar impacto arquitetural de mudanças, gerar mapas de contexto e checar a regra de dependência Infrastructure → Application → Domain.
- [ ] Cada agent referencia as instructions relevantes via `applyTo` ou menção explícita em seu conteúdo.
- [ ] `AGENTS.md` atualizado com a descrição dos novos agents na tabela de agents disponíveis.

### Dependências Técnicas

- `.github/agents/` existente (criado na Fase 2).
- Instructions de domínio existentes (Fase 3) serão referenciadas pelos agents.
- `AGENTS.md` na raiz para atualização.

### Riscos e Pontos de Atenção

- Agents muito genéricos perdem foco; cada um deve ter scopo de atuação claramente distinto dos agents base.
- `code-reviewer` e `context-architect` têm sobreposição potencial; definir que o architect **planeja** e o reviewer **avalia o resultado**.
- Evitar duplicação de regras já expressas em instructions; agents devem referenciar as instructions, não reescrevê-las.

### Plano de Implementação

#### Fase A — code-reviewer.agent.md
1. Definir persona: revisor de código para Java/Spring com foco em Clean Architecture e OWASP.
2. Definir `tools`: `codebase`, `problems`, `terminalCommand`.
3. Definir formato de saída obrigatório: tabela de findings com severidade (CRITICAL / MAJOR / MINOR / INFO) e referência ao arquivo.
4. Definir checklist embutido: arquitetura em camadas, ausência de lógica em controllers, uso correto de Output Objects, segurança.
5. Criar arquivo `.github/agents/code-reviewer.agent.md`.

#### Fase B — test-designer.agent.md
1. Definir persona: especialista em design de testes para Clean Architecture Java.
2. Definir `tools`: `codebase`, `terminalCommand`, `problems`.
3. Formato de saída: plano de testes com tabela (caso, tipo, classe alvo, prioridade).
4. Incluir regras de JUnit 5 + Mockito alinhadas com `testing.instructions.md`.
5. Criar arquivo `.github/agents/test-designer.agent.md`.

#### Fase C — architect.agent.md
1. Definir persona: arquiteto de software focado em DDD + Clean Architecture.
2. Definir `tools`: `codebase`, `terminalCommand`, `search`.
3. Formato de saída: Mapa de Contexto (primários, secundários, testes, padrão, sequência) — mesmo formato do Context Architect, mas com avaliação de risco arquitetural.
4. Diferenciar escopo: Context Architect mapeia **antes** de agir; Architect avalia **viabilidade e impacto** de mudanças arquiteturais.
5. Criar arquivo `.github/agents/architect.agent.md`.

#### Fase D — Atualização do AGENTS.md
1. Adicionar linha de cada novo agent na tabela de agents disponíveis.
2. Atualizar seção de bounded contexts atingidos por cada agent.

### Checkpoints de Memória

- Checkpoint 0: contexto inicial registrado (este documento).
- Checkpoint 1: plano aprovado, TASK005 criada em `memory-bank/tasks/`.
- Checkpoint 2: três agents criados, AGENTS.md atualizado.
- Checkpoint 3: retrospectiva — agents usados em sessão real, lições registradas.

### Definição de Pronto

- [ ] Três arquivos `.agent.md` criados e com frontmatter correto.
- [ ] Cada agent tem `description` clara e distinta dos agents existentes.
- [ ] `AGENTS.md` reflete os novos agents.
- [ ] Memory Bank atualizado: TASK005, `_index.md`, `activeContext.md`, `progress.md`.

---

## Refinamento da Tarefa: TASK006 — Camada de Skills

### Objetivo e Escopo

Criar a camada de skills do workspace, começando pela skill `task-checkpoint-memory`, que padroniza o registro de checkpoints de aprendizado e decisões no Memory Bank. Skills ficam em `.github/skills/`.

### Critérios de Aceite

- [ ] Pasta `.github/skills/task-checkpoint-memory/` criada.
- [ ] `SKILL.md` principal publicado com instruções completas de quando e como registrar checkpoints.
- [ ] Template `checkpoint-template.md` criado em `.github/skills/task-checkpoint-memory/templates/`.
- [ ] Referência `memory-governance.md` criada em `.github/skills/task-checkpoint-memory/references/`, contendo as regras extraídas de `governance.instructions.md` e `memory-bank.instructions.md` relevantes para o checkpoint.
- [ ] `SKILL.md` é localizável (description clara para descoberta automática pelo VS Code Copilot).

### Dependências Técnicas

- `.github/skills/` existente (pasta criada mas vazia).
- `memory-bank.instructions.md` e `governance.instructions.md` como fontes de regras a referenciar.

### Riscos e Pontos de Atenção

- Skills precisam de `description` no frontmatter para serem descobertas pelo VS Code Copilot (campo crítico).
- O template de checkpoint não deve ser genérico demais; deve ser específico para o contexto do `cal-service`.

### Plano de Implementação

#### Fase A — SKILL.md principal
1. Definir caso de uso da skill: quando usar, pré-condições, saída esperada.
2. Descrever o workflow passo a passo de registro de checkpoint.
3. Referenciar o template e a referência de governança.
4. Criar `.github/skills/task-checkpoint-memory/SKILL.md`.

#### Fase B — Template de checkpoint
1. Estruturar template com seções: contexto, decisões tomadas, lições aprendidas, próximos passos.
2. Incluir placeholders com exemplos do `cal-service`.
3. Criar `.github/skills/task-checkpoint-memory/templates/checkpoint-template.md`.

#### Fase C — Referência de governança
1. Extrair regras relevantes de `governance.instructions.md` (checkpoints, rastreabilidade).
2. Extrair regras de `memory-bank.instructions.md` (formato de atualização).
3. Criar `.github/skills/task-checkpoint-memory/references/memory-governance.md`.

### Checkpoints de Memória

- Checkpoint 0: contexto inicial registrado (este documento).
- Checkpoint 1: plano aprovado, TASK006 criada em `memory-bank/tasks/`.
- Checkpoint 2: SKILL.md + template + referência criados.
- Checkpoint 3: skill usada em sessão real, lições registradas.

### Definição de Pronto

- [ ] Três arquivos da skill criados (SKILL.md, template, referência).
- [ ] `SKILL.md` com frontmatter correto e `description` localizável.
- [ ] Skill referenciada na documentação de governança (AGENTS.md ou copilot-instructions.md).
- [ ] Memory Bank atualizado: TASK006, `_index.md`, `activeContext.md`, `progress.md`.

---

## Refinamento da Tarefa: TASK007 — Camada de Prompts Reutilizáveis

### Objetivo e Escopo

Criar os primeiros prompts reutilizáveis do workspace em `.github/prompts/`. Prompts são templates parametrizados para operações repetitivas. Começar pelos dois prompts identificados no plano original.

### Critérios de Aceite

- [ ] Pasta `.github/prompts/` criada.
- [ ] `refinar-tarefa.prompt.md` publicado — prompt parametrizado para refinamento de demanda bruta em backlog executável.
- [ ] `criar-backlog-tarefa.prompt.md` publicado — prompt para decomposição de uma tarefa grande em subtarefas com dependências.
- [ ] Cada prompt tem frontmatter correto com `description` e `mode` (se aplicável).
- [ ] Prompts são funcionais: ao usá-los no VS Code Copilot, produzem saída no formato esperado pelo `task-refiner-governance` agent.

### Dependências Técnicas

- TASK005 concluída (agents existem como referência de saída esperada dos prompts).
- `task-refiner-governance` agent existente como base de compatibilidade de saída.
- `.github/prompts/` a ser criada (pasta não existe ainda).

### Riscos e Pontos de Atenção

- Prompts reutilizáveis ficam obsoletos rapidamente se não alinhados às instructions; incluir referências explícitas às instructions relevantes.
- Usar variáveis de template (`${variavel}`) aumenta reutilização mas pode confundir agentes que não interpretam a sintaxe corretamente.

### Plano de Implementação

#### Fase A — refinar-tarefa.prompt.md
1. Definir parâmetros de entrada: `{demanda}`, `{bounded_context}`, `{contexto_adicional}`.
2. Estruturar o prompt para produzir saída no formato do artefato de refinamento do `task-refiner-governance` agent.
3. Incluir referências às instructions relevantes como contexto para o modelo.
4. Criar `.github/prompts/refinar-tarefa.prompt.md`.

#### Fase B — criar-backlog-tarefa.prompt.md
1. Definir parâmetros de entrada: `{tarefa_grande}`, `{criterios_aceite}`.
2. Estruturar o prompt para decompor a tarefa em subtarefas atômicas com IDs, dependências e prioridade.
3. Incluir regra de que cada subtarefa deve se encaixar em uma das camadas da Clean Architecture.
4. Criar `.github/prompts/criar-backlog-tarefa.prompt.md`.

#### Fase C — Pasta e documentação
1. Criar pasta `.github/prompts/`.
2. Referenciar os prompts no `AGENTS.md` ou `copilot-instructions.md`.

### Checkpoints de Memória

- Checkpoint 0: contexto inicial registrado (este documento).
- Checkpoint 1: plano aprovado, TASK007 criada em `memory-bank/tasks/`.
- Checkpoint 2: dois prompts criados e funcionais.
- Checkpoint 3: prompts usados em sessão real, lições registradas.

### Definição de Pronto

- [ ] Pasta `.github/prompts/` criada.
- [ ] Dois arquivos `.prompt.md` criados com frontmatter correto.
- [ ] Prompts produzem saída compatível com o formato do `task-refiner-governance` agent.
- [ ] Memory Bank atualizado: TASK007, `_index.md`, `activeContext.md`, `progress.md`.

---

## Refinamento da Tarefa: TASK008 — Ciclo de Governança Contínua

### Objetivo e Escopo

Formalizar o ciclo de revisão e manutenção do workspace Copilot: processo quinzenal de convergência entre aprendizado acumulado (Memory Bank, checkpoints, lições) e as regras ativas (instructions, agents, skills). Escopo: meta-processo de governança, não código de aplicação.

### Critérios de Aceite

- [ ] Checklist de revisão quinzenal documentado (o que revisar, quando, por quem).
- [ ] Processo de convergência documentado: como lições do Memory Bank viram novas rules/instructions.
- [ ] Template de retrospectiva de ciclo criado (a ser preenchido a cada revisão quinzenal).
- [ ] Documento de governança contínua publicado em `docs/` ou `.github/`.
- [ ] `progress.md` e `activeContext.md` atualizados com o processo em vigor.

### Dependências Técnicas

- TASK006 concluída (skill de checkpoint existindo para alimentar o ciclo).
- TASK007 concluída (prompts existindo como instrumentos do ciclo).
- TASK005 concluída (agents especializados alimentando revisões).

### Riscos e Pontos de Atenção

- Um processo de revisão muito complexo não será seguido; manter o checklist curto (máximo 10 itens).
- Sem responsável definido, o ciclo não acontece; documentar que o processo é acionado por qualquer membro do time via prompt ou agent.
- Memory Bank pode inflar com histórico morto; o processo deve incluir limpeza de entradas obsoletas.

### Plano de Implementação

#### Fase A — Checklist de revisão quinzenal
1. Definir os itens a revisar: agents, instructions, skills, prompts, memory bank (activeContext, progress, _index).
2. Definir critérios de convergência: quando uma lição vira uma instruction formal.
3. Limitar a 8–10 itens verificáveis.
4. Documentar em `.github/instructions/governance.instructions.md` (seção nova) ou em `docs/governanca-continua.md`.

#### Fase B — Template de retrospectiva
1. Estruturar template: data, itens revisados, lições convertidas em rules, próximos ajustes.
2. Criar `.github/skills/task-checkpoint-memory/templates/retrospectiva-ciclo-template.md`.

#### Fase C — Processo de convergência
1. Descrever o fluxo: Lição identificada → checkpoint registrado → revisão quinzenal → instruction/prompt atualizado → Memory Bank reflects mudança.
2. Criar `docs/governanca-continua.md` descrevendo o processo completo.

#### Fase D — Atualização do Memory Bank
1. Registrar o processo em vigor em `activeContext.md`.
2. Atualizar `progress.md` marcando Fase 4 como concluída.
3. Atualizar `tasks/_index.md`.

### Checkpoints de Memória

- Checkpoint 0: contexto inicial registrado (este documento).
- Checkpoint 1: plano aprovado, TASK008 criada em `memory-bank/tasks/`.
- Checkpoint 2: checklist + template + processo documentados.
- Checkpoint 3: primeiro ciclo real executado, lições registradas.

### Definição de Pronto

- [ ] Checklist de revisão quinzenal documentado e acessível.
- [ ] Template de retrospectiva criado.
- [ ] Processo de convergência documentado em `docs/governanca-continua.md`.
- [ ] Memory Bank atualizado: TASK008, `_index.md`, `activeContext.md`, `progress.md`.

---

## Sequência Sugerida de Execução

```
TASK005 → TASK006 → TASK007 → TASK008
```

- TASK005 e TASK006 podem ser executadas em paralelo (sem dependência entre si).
- TASK007 depende de TASK005 (agents como referência de compatibilidade de saída).
- TASK008 depende de TASK006 e TASK007 (skills e prompts como instrumentos do ciclo).

## Definição de Pronto da Fase 4

Fase 4 concluída quando:

- [ ] TASK005 concluída (3 agents especializados publicados).
- [ ] TASK006 concluída (skill task-checkpoint-memory com SKILL.md, template e referência).
- [ ] TASK007 concluída (2 prompts reutilizáveis publicados).
- [ ] TASK008 concluída (ciclo de governança documentado e operacional).
- [ ] `progress.md` reflete Fase 4 como concluída.
- [ ] `activeContext.md` atualizado com o novo estado do workspace.
