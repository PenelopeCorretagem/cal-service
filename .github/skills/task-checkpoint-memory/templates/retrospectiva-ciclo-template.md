# Retrospectiva de Ciclo — Governança Contínua

**Data:** YYYY-MM-DD  
**Ciclo:** Quinzenal / Mensal  
**Responsável:** [nome ou @handle]  
**Período coberto:** YYYY-MM-DD a YYYY-MM-DD

---

## 1. Itens Revisados

Marque cada item verificado neste ciclo:

- [ ] `memory-bank/activeContext.md` — foco atual e decisões ativas refletem o estado real.
- [ ] `memory-bank/progress.md` — status geral e listas "O Que Falta" atualizados.
- [ ] `memory-bank/tasks/_index.md` — nenhuma tarefa com status desatualizado.
- [ ] `.github/copilot-instructions.md` — regras gerais sem entradas obsoletas.
- [ ] `.github/agents/*.agent.md` — agents com `description` e ferramentas corretas.
- [ ] `.github/instructions/*.instructions.md` — instructions sem conflitos ou duplicações.
- [ ] `.github/skills/` — skills com `description` localizável e workflow atualizado.
- [ ] `.github/prompts/` — prompts produzindo saída compatível com agents atuais.

---

## 2. Lições Coletadas no Período

> Liste as lições capturadas em checkpoints (Checkpoint 3) de tarefas concluídas no período.
> Se nenhuma lição nova foi registrada, escreva "Nenhuma lição nova neste ciclo."

| # | Lição | Tarefa de Origem | Candidato a Regra Formal? |
|---|-------|-----------------|--------------------------|
| 1 | [Descreva a lição] | [TASKXXX] | Sim / Não |
| 2 | | | |

---

## 3. Convergência: Lições → Regras Formais

> Para cada lição marcada como "Candidato a Regra Formal", registre a ação tomada.

| Lição | Arquivo Alvo | Ação | Status |
|-------|-------------|------|--------|
| [Lição da linha 1] | `.github/instructions/[arquivo].md` | Adicionada nova regra na seção X | ✅ Aplicado |
| [Lição da linha N] | `.github/agents/[agent].agent.md` | Checklist atualizado | ⏳ Pendente |

Formato de referência para decidir onde aplicar a convergência:

- Regra afeta **código Java/Spring** → `java-spring.instructions.md`.
- Regra afeta **testes** → `testing.instructions.md`.
- Regra afeta **segurança** → `security.instructions.md`.
- Regra afeta **endpoints/OpenAPI** → `api-contract.instructions.md`.
- Regra afeta **processo do agente** (rastreabilidade, escopo) → `governance.instructions.md`.
- Regra afeta **memory bank** (leitura, atualização) → `memory-bank.instructions.md`.
- Regra afeta **persona ou ferramentas** de um agent específico → arquivo `.agent.md` correspondente.

---

## 4. Entradas Obsoletas Identificadas

> Liste arquivos ou seções que estão desatualizados e registre a ação de limpeza.

| Arquivo | Seção/Entrada Obsoleta | Ação |
|---------|----------------------|------|
| [Arquivo] | [Descrição] | Removida / Atualizada / Adiada |

---

## 5. Decisões do Ciclo

> Registre decisões tomadas durante esta revisão (ex.: descontinuar um agent, mudar convenção de ID).

| Decisão | Justificativa |
|---------|---------------|
| [Decisão] | [Motivo] |

---

## 6. Próximos Passos

> Liste ações concretas a serem executadas antes da próxima revisão.

- [ ] [Ação 1]
- [ ] [Ação 2]

---

## 7. Métricas do Ciclo

| Métrica | Valor |
|---------|-------|
| Tarefas concluídas no período | [N] |
| Lições coletadas | [N] |
| Lições convertidas em regras formais | [N] |
| Entradas obsoletas removidas | [N] |
| Prompts/agents atualizados | [N] |
