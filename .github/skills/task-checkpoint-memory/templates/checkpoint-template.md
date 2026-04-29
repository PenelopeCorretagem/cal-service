# Checkpoint — [TASKID] [Nome da Tarefa]

**Data:** YYYY-MM-DD  
**Tipo:** Checkpoint [0 / 1 / 2 / 3]  
**Tarefa:** [TASKID-nome-kebab-case.md]  
**Bounded Context Afetado:** [eventtype / appointment / infra / workspace-copilot]

---

## Contexto

> Descreva brevemente o que estava sendo feito no momento deste checkpoint.
> Inclua o estado do sistema antes da mudança e o que motivou a ação.

Exemplo:
> Implementando o use case `CreateAppointmentService` no contexto `appointment`. 
> O domínio já existia; faltava a camada de aplicação com mapeamentos e command objects.

---

## O Que Foi Feito

> Liste as ações concretas realizadas neste ciclo. Seja específico com arquivos e classes.

- [ ] [Ação 1 — ex.: Criado `AppointmentOutputMapper` com MapStruct]
- [ ] [Ação 2 — ex.: Registrado `CreateAppointmentCommand` como record imutável]
- [ ] [Ação N]

---

## Decisões Tomadas

> Registre cada decisão que não é óbvia ou que pode gerar dúvida no futuro.
> Se nenhuma decisão nova foi tomada, escreva "Nenhuma decisão nova neste ciclo."

| Decisão | Justificativa | Alternativa Descartada |
|---------|---------------|------------------------|
| [Descrição da decisão] | [Por que foi escolhida] | [O que foi considerado e descartado] |

Exemplo:
| Usar `record` para `CreateAppointmentCommand` | Imutabilidade garantida pelo compilador; menos boilerplate que `@Value` do Lombok | `@Value` descartado por não garantir imutabilidade em subclasses |

---

## Lições Aprendidas

> O que aprendemos que pode melhorar o processo ou evitar retrabalho futuro?
> Se nenhuma nova lição foi aprendida, escreva "Nenhuma nova lição neste ciclo."

- [Lição 1 — ex.: MapStruct não gera mapper para tipos sealed sem configuração explícita]
- [Lição N]

**Candidatos a instruction formal:**

| Lição | Arquivo alvo | Ação |
|-------|-------------|------|
| [Lição] | `.github/instructions/[arquivo].md` | [Adicionar regra / Atualizar seção] |

---

## Bloqueios Encontrados

> Descreva problemas que travaram o progresso e como foram resolvidos.
> Se não houve bloqueios, escreva "Sem bloqueios neste ciclo."

| Bloqueio | Impacto | Resolução |
|----------|---------|-----------|
| [Descrição] | [Alto / Médio / Baixo] | [Como foi resolvido] |

---

## Próximos Passos

> Liste as ações concretas para o próximo ciclo, em ordem de prioridade.

1. [Próxima ação — ex.: Criar testes unitários para `CreateAppointmentService`]
2. [Próxima ação N]
3. Atualizar `memory-bank/activeContext.md` com este checkpoint.
4. Atualizar `memory-bank/tasks/_index.md` com o novo status.

---

## Referência Rápida — Arquivos Modificados

```
src/main/java/com/penelopec/calservice/
└── [bounded-context]/
    ├── domain/         ← [arquivos de domínio criados/modificados]
    ├── application/    ← [arquivos de aplicação criados/modificados]
    └── infrastructure/ ← [arquivos de infra criados/modificados]

memory-bank/
├── activeContext.md    ← [atualizado? S/N]
├── progress.md         ← [atualizado? S/N]
└── tasks/
    ├── _index.md       ← [atualizado? S/N]
    └── TASKID-nome.md  ← [atualizado? S/N]
```
