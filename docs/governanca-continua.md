# Governança Contínua — cal-service

> Documento operacional do ciclo de revisão e manutenção do workspace GitHub Copilot.  
> Complementa as regras de `.github/instructions/governance.instructions.md` com o **processo vivo** de convergência entre aprendizado acumulado e regras formais.

---

## 1. Objetivo

Garantir que lições identificadas em sessões reais se convertam em regras formais que beneficiem sessões futuras. Sem este processo, o Memory Bank vira histórico morto e as instructions ficam obsoletas.

**Ciclo desejado:**

```
Sessão de trabalho real
  → Lição identificada
    → Checkpoint 3 registrado (skill: task-checkpoint-memory)
      → Revisão quinzenal disparada
        → Lição avaliada como candidata a regra formal
          → Instruction / agent / prompt / skill atualizado
            → Memory Bank reflete mudança
              → Próxima sessão se beneficia automaticamente
```

---

## 2. Checklist de Revisão Quinzenal

Execute este checklist a cada 15 dias (ou após 3+ tarefas concluídas):

1. **Memory Bank core** — `activeContext.md` e `progress.md` refletem o estado real do projeto?
2. **Índice de tarefas** — `tasks/_index.md` tem status corretos? Nenhuma tarefa em progresso fantasma?
3. **Lições acumuladas** — Há Checkpoint 3 registrados desde a última revisão? Extraia os candidatos a regra formal.
4. **Instructions** — Alguma instruction em `.github/instructions/` tem regras conflitantes, duplicadas ou obsoletas?
5. **Agents** — Os `description` de cada `.agent.md` ainda descrevem o comportamento real? Ferramentas desatualizadas?
6. **Skills** — As skills em `.github/skills/` têm fluxos que ainda fazem sentido com o estado atual do projeto?
7. **Prompts** — Os prompts em `.github/prompts/` produzem saídas compatíveis com os agents atuais?
8. **Convergência** — Aplique as lições aprovadas: edite o arquivo alvo e registre na retrospectiva.
9. **Limpeza** — Remova entradas claramente obsoletas do Memory Bank (tarefas antigas com dados desatualizados).
10. **Retrospectiva** — Preencha `.github/skills/task-checkpoint-memory/templates/retrospectiva-ciclo-template.md` e salve em `memory-bank/tasks/` com nome `RETRO-YYYY-MM-DD.md`.

---

## 3. Processo de Convergência: Lição → Regra Formal

### 3.1 Critério de Aprovação

Uma lição se torna candidata a regra formal quando:

- Ocorreu em pelo menos 2 sessões distintas **ou** causou retrabalho mensurável.
- É genérica o suficiente para beneficiar sessões futuras (não é caso isolado).
- Pode ser expressa como regra imperativa clara (faça X, evite Y).

### 3.2 Mapeamento Lição → Arquivo Alvo

| Tipo de Lição | Arquivo Alvo |
|---------------|-------------|
| Regra de código Java/Spring | `.github/instructions/java-spring.instructions.md` |
| Regra de testes | `.github/instructions/testing.instructions.md` |
| Regra de segurança | `.github/instructions/security.instructions.md` |
| Regra de API/OpenAPI | `.github/instructions/api-contract.instructions.md` |
| Regra de processo do agente | `.github/instructions/governance.instructions.md` |
| Regra de leitura/atualização do Memory Bank | `.github/instructions/memory-bank.instructions.md` |
| Ajuste de persona ou ferramentas de agent | `.github/agents/<agent>.agent.md` correspondente |
| Novo padrão de prompt reutilizável | `.github/prompts/<nome>.prompt.md` (novo arquivo) |
| Novo workflow repetível | `.github/skills/<skill>/SKILL.md` (novo ou atualizado) |

### 3.3 Fluxo de Aplicação

```
1. Identificar lição (no Checkpoint 3 do arquivo de tarefa)
2. Avaliar se atinge o critério de aprovação (seção 3.1)
3. Mapear ao arquivo alvo (seção 3.2)
4. Editar o arquivo: adicionar regra, atualizar seção ou criar novo artefato
5. Registrar na retrospectiva do ciclo (tabela "Convergência")
6. Atualizar activeContext.md com a decisão
```

---

## 4. Instrumentos do Ciclo

| Instrumento | Arquivo | Papel no Ciclo |
|-------------|---------|----------------|
| Skill de checkpoint | `.github/skills/task-checkpoint-memory/SKILL.md` | Captura de lições em tempo real |
| Template de checkpoint | `.github/skills/task-checkpoint-memory/templates/checkpoint-template.md` | Estrutura o registro de lição |
| Template de retrospectiva | `.github/skills/task-checkpoint-memory/templates/retrospectiva-ciclo-template.md` | Estrutura a revisão quinzenal |
| Prompt de refinamento | `.github/prompts/refinar-tarefa.prompt.md` | Garante que novas tarefas já nascem rastreáveis |
| Agent task-refiner | `.github/agents/task-refiner-governance.agent.md` | Orquestra o fluxo de tarefas com checkpoints |
| Agent architect | `.github/agents/architect.agent.md` | Avalia impacto de mudanças arquiteturais antes de aplicar convergência |

---

## 5. Regras de Limpeza do Memory Bank

Para evitar que o Memory Bank infle com histórico morto:

- **`activeContext.md`**: manter apenas decisões e próximos passos do ciclo atual. Informações antigas sobre tarefas concluídas pertencem ao arquivo de tarefa, não aqui.
- **`progress.md`**: atualizar apenas quando há mudança real de status. Não reescrever o histórico; apenas marcar itens como concluídos.
- **`tasks/_index.md`**: tarefas Completed ou Abandoned não precisam de descrição elaborada — apenas ID, nome e data.
- **Arquivos de tarefa**: não editar retrospectivamente o histórico de progresso; apenas adicionar novas entradas no log.

---

## 6. Quando Disparar o Ciclo

O ciclo de revisão é disparado por:

- **Automaticamente**: após 15 dias corridos desde a última retrospectiva.
- **Por volume**: após 3 ou mais tarefas concluídas desde a última revisão.
- **Por incidente**: quando uma instrução incorreta causou retrabalho em sessão real.
- **Por solicitação explícita**: quando qualquer membro do time pede `update memory bank` ou equivalente.

Qualquer agente ou colaborador pode disparar o ciclo usando o checklist da seção 2.

---

## 7. Armazenamento das Retrospectivas

Salvar cada retrospectiva preenchida como:

```
memory-bank/tasks/RETRO-YYYY-MM-DD.md
```

Não indexar retrospectivas no `_index.md` de tarefas — elas ficam no diretório `tasks/` como referência histórica, separadas das tarefas executáveis.

---

## 8. Referências

- [governance.instructions.md](../.github/instructions/governance.instructions.md) — políticas de execução e rastreabilidade.
- [memory-bank.instructions.md](../.github/instructions/memory-bank.instructions.md) — obrigatoriedade de leitura e atualização do Memory Bank.
- [SKILL.md (task-checkpoint-memory)](../.github/skills/task-checkpoint-memory/SKILL.md) — workflow de registro de checkpoint.
- [checkpoint-template.md](../.github/skills/task-checkpoint-memory/templates/checkpoint-template.md) — template de checkpoint individual.
- [retrospectiva-ciclo-template.md](../.github/skills/task-checkpoint-memory/templates/retrospectiva-ciclo-template.md) — template da revisão quinzenal.
