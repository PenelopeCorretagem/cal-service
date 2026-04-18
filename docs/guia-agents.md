# Guia de Uso dos Agents — cal-service

Documentação completa dos agents especializados do GitHub Copilot disponíveis neste repositório, incluindo quando usar cada um, como invocá-los e fluxos de exemplo.

---

## Sumário

1. [Visão Geral](#1-visão-geral)
2. [Catálogo de Agents](#2-catálogo-de-agents)
3. [Quando Usar Cada Agent](#3-quando-usar-cada-agent)
4. [Fluxos de Trabalho de Exemplo](#4-fluxos-de-trabalho-de-exemplo)
5. [Prompts Reutilizáveis](#5-prompts-reutilizáveis)
6. [Skill: task-checkpoint-memory](#6-skill-task-checkpoint-memory)
7. [Combinando Agents em um Ciclo Completo](#7-combinando-agents-em-um-ciclo-completo)
8. [Referência Rápida](#8-referência-rápida)

---

## 1. Visão Geral

O workspace deste repositório possui **6 agents especializados**, projetados para cobrir fases distintas do desenvolvimento. Cada agent tem um escopo claro e não deve ser usado fora dele.

```
Fluxo padrão de desenvolvimento:

  [demanda bruta]
       │
       ▼
  Task Refiner & Governance   ← "transformar em tarefa rastreável"
       │
       ▼
  Context Architect           ← "mapear o que precisa mudar"
       │
       ▼
  Architect (se necessário)   ← "validar decisão arquitetural"
       │
       ▼
  [implementação pelo developer / agente default]
       │
       ├──► Test Designer     ← "o que e como testar"
       │
       └──► Code Reviewer     ← "o resultado está correto?"
```

O **Prompt Builder** é transversal: usado quando se quer criar ou melhorar qualquer artefato do workspace Copilot (agents, instructions, prompts, skills).

---

## 2. Catálogo de Agents

### 2.1 Task Refiner & Governance

| Atributo | Valor |
|----------|-------|
| **Arquivo** | `.github/agents/task-refiner-governance.agent.md` |
| **Ferramentas** | `read`, `search`, `edit`, `execute` |
| **Fase** | Planejamento — antes de qualquer implementação |

**Papel:** Transforma uma demanda bruta em backlog executável. Produz artefato com objetivo, critérios de aceite, dependências técnicas, riscos e plano de implementação por fases. Garante rastreabilidade criando e atualizando o arquivo de tarefa no `memory-bank/tasks/`.

**Como invocar:**

No GitHub Copilot Chat, selecione o agent `Task Refiner & Governance` na lista de agents e descreva a demanda diretamente:

```
Preciso adicionar filtro por status no endpoint de listagem de appointments.
```

Ou use o prompt reutilizável `refinar-tarefa` (ver [seção 5](#5-prompts-reutilizáveis)).

---

### 2.2 Context Architect

| Atributo | Valor |
|----------|-------|
| **Arquivo** | `.github/agents/context-architect.agent.md` |
| **Ferramentas** | `read`, `search` |
| **Fase** | Tático — antes de editar arquivos |

**Papel:** Explora o codebase e produz um **Mapa de Contexto** listando arquivos primários a modificar, arquivos secundários afetados, cobertura de testes existente, padrão a seguir e sequência sugerida. Não faz implementação.

**Como invocar:**

```
Quero adicionar um novo use case que lista event types por empreendimento.
Me dê o mapa de contexto antes de começar.
```

**Saída esperada:**

```
## Mapa de Contexto para: Listar Event Types por Empreendimento

### Arquivos Primários (modificados diretamente)
- eventtype/domain/EventTypeRepository.java — adicionar método de query por estateId
- eventtype/application/ListEventTypesByEstate.java — nova interface de use case
- eventtype/application/service/ListEventTypesByEstateService.java — implementação

### Arquivos Secundários (podem precisar de atualização)
- eventtype/infrastructure/EventTypeController.java — novo endpoint GET

### Cobertura de Testes
- eventtype/application/service/ListEventTypesServiceTest.java — padrão a seguir

### Padrão a Seguir
- Referência: eventtype/application/service/ListEventTypesService.java

### Sequência Sugerida
1. Adicionar método no Repository (interface)
2. Criar interface do use case
3. Implementar o service
4. Criar o endpoint no controller
5. Criar testes unitários
```

---

### 2.3 Architect

| Atributo | Valor |
|----------|-------|
| **Arquivo** | `.github/agents/architect.agent.md` |
| **Ferramentas** | `read`, `search` |
| **Fase** | Estratégico — decisões de design |

**Papel:** Avalia viabilidade e impacto de mudanças arquiteturais. Verifica a regra de dependência `Infrastructure → Application → Domain`, identifica breaking changes e emite um **veredicto formal**: Viável / Viável com ressalvas / Inviável.

**Como invocar:**

```
Quero mover a lógica de cálculo de duração de agendamento para dentro do AppointmentController
como um método privado. Isso é viável?
```

**Saída esperada:**

```
## Mapa de Impacto Arquitetural — mover cálculo de duração para o Controller

### Verificação de Regra de Dependência
- Viola: lógica de negócio em controller quebra Clean Architecture.

### Veredicto: Inviável
Lógica de negócio pertence à camada de domínio ou aplicação.
Alternativa recomendada: método na entidade Appointment ou no use case.
```

---

### 2.4 Code Reviewer

| Atributo | Valor |
|----------|-------|
| **Arquivo** | `.github/agents/code-reviewer.agent.md` |
| **Ferramentas** | `read`, `search`, `problems` |
| **Fase** | Pós-implementação — revisão de qualidade |

**Papel:** Avalia código implementado contra checklist de Clean Architecture, OWASP Top 10, convenções do projeto e padrões de API REST. Produz **Relatório de Revisão** com tabela de findings classificados por severidade (CRITICAL / MAJOR / MINOR / INFO).

**Como invocar:**

```
Revise o CreateAppointmentService e o AppointmentController que acabei de implementar.
```

**Severidades:**

| Severidade | Significado |
|------------|-------------|
| `CRITICAL` | Violação de segurança ou lógica de negócio incorreta — bloqueia entrega |
| `MAJOR` | Viola convenção arquitetural ou padrão obrigatório |
| `MINOR` | Problema de qualidade sem impacto funcional |
| `INFO` | Sugestão de melhoria ou observação |

---

### 2.5 Test Designer

| Atributo | Valor |
|----------|-------|
| **Arquivo** | `.github/agents/test-designer.agent.md` |
| **Ferramentas** | `read`, `search`, `problems` |
| **Fase** | Durante ou antes da implementação — estratégia de testes |

**Papel:** Projeta o plano de testes para uma mudança. Mapeia casos de sucesso, casos de erro e edge cases por componente, respeitando a pirâmide de testes do projeto. Produz tabela com classe alvo, tipo (unitário/integração) e nome de método sugerido.

**Como invocar:**

```
Projete o plano de testes para o use case CancelAppointment.
```

**Saída esperada:**

```
## Plano de Testes — CancelAppointment

| # | Caso de Teste | Tipo | Classe Alvo | Prioridade | Método Sugerido |
|---|--------------|------|-------------|------------|-----------------|
| 1 | Cancela appointment com sucesso | Unitário | CancelAppointmentServiceTest | Alta | shouldCancelAppointment_whenAppointmentExists |
| 2 | Lança exceção quando appointment não encontrado | Unitário | CancelAppointmentServiceTest | Alta | shouldThrowException_whenAppointmentNotFound |
| 3 | Lança exceção quando status já é CANCELLED | Unitário | CancelAppointmentServiceTest | Alta | shouldThrowException_whenAppointmentAlreadyCancelled |
```

---

### 2.6 Prompt Builder

| Atributo | Valor |
|----------|-------|
| **Arquivo** | `.github/agents/prompt-builder.agent.md` |
| **Ferramentas** | `read`, `search`, `edit` |
| **Fase** | Transversal — manutenção do workspace Copilot |

**Papel:** Cria e melhora arquivos de customização do GitHub Copilot (`.agent.md`, `.instructions.md`, `.prompt.md`, `SKILL.md`). Valida frontmatter YAML, verifica conflitos com instruções existentes e garante que o artefato segue as convenções do projeto.

**Como invocar:**

```
Crie um novo agent para revisar migrações de banco de dados antes de executar.
```

---

## 3. Quando Usar Cada Agent

```
Tenho uma demanda bruta (ideia, ticket, requisito)
    └──► Task Refiner & Governance

Vou modificar código — quero saber o que muda
    └──► Context Architect

Estou considerando uma decisão arquitetural (mudar estrutura de pacotes,
trocar biblioteca, mover responsabilidade entre camadas)
    └──► Architect

Acabei de implementar algo — quero revisar
    └──► Code Reviewer

Preciso escrever ou planejar testes
    └──► Test Designer

Quero criar ou melhorar um agent, instruction, prompt ou skill
    └──► Prompt Builder
```

### Distinções Importantes

| Situação | Agent Correto | Agent Errado |
|----------|---------------|--------------|
| Planejar o que vai ser feito | Task Refiner | Context Architect |
| Explorar arquivos afetados antes de codar | Context Architect | Architect |
| Avaliar se uma mudança de design é boa | Architect | Context Architect |
| Verificar código após implementar | Code Reviewer | Architect |
| Criar testes durante a implementação | Test Designer | Code Reviewer |

---

## 4. Fluxos de Trabalho de Exemplo

### Fluxo A — Nova Feature do Zero

**Cenário:** Adicionar filtro por data no endpoint de listagem de appointments.

**Passo 1 — Refinar a demanda (`Task Refiner & Governance`)**

```
Demanda: Quero filtrar appointments por intervalo de data (dataInicio, dataFim)
no GET /appointments.
```

O agent:
- Lê o Memory Bank
- Produz artefato com critérios de aceite, dependências e plano por fases
- Cria `memory-bank/tasks/TASK009-filtro-data-appointments.md`
- Atualiza `memory-bank/tasks/_index.md`

**Passo 2 — Mapear contexto (`Context Architect`)**

```
Vou implementar o TASK009. Me dê o mapa de contexto.
```

O agent:
- Explora `appointment/domain/`, `application/`, `infrastructure/`
- Lista os 4–6 arquivos que precisarão mudar
- Aponta o padrão a seguir (ex: `ListEventTypesService`)
- Sugere a sequência de mudanças

**Passo 3 — Implementação** (developer ou agente default do Copilot)

**Passo 4 — Planejar testes (`Test Designer`)**

```
Projete os testes para o listAppointments com filtro por data.
```

**Passo 5 — Revisar resultado (`Code Reviewer`)**

```
Revise ListAppointmentsService e AppointmentController após a implementação do filtro.
```

---

### Fluxo B — Refatoração Arquitetural

**Cenário:** Avaliar se faz sentido extrair a lógica de envio de notificações para um bounded context separado.

**Passo 1 — Avaliação estratégica (`Architect`)**

```
Quero extrair a lógica de notificação de status de appointment para um
bounded context "notification". Avalie o impacto.
```

O agent produz:
- Verificação de regra de dependência
- Impacto por camada
- Efeito cascata em arquivos existentes
- Veredicto: Viável / Viável com ressalvas / Inviável

**Passo 2 — Se viável, refinar o trabalho (`Task Refiner & Governance`)**

```
O Architect aprovou a extração. Refine a tarefa de criação do bounded context notification.
```

**Passo 3 — Mapear arquivos afetados (`Context Architect`)**

```
Dado o plano aprovado, mapeie os arquivos que precisarão mudar.
```

---

### Fluxo C — Revisão de Pull Request

**Cenário:** Código de outro developer chegou para revisão.

**Passo 1 — Revisão arquitetural (`Code Reviewer`)**

```
Revise os arquivos modificados neste PR: CreateAppointmentService,
AppointmentJpaAdapter e AppointmentController.
```

O agent:
- Executa checklist de 20+ itens (Clean Architecture, OWASP, API, Testes)
- Produz tabela de findings com severidade
- Indica se está aprovado, aprovado com ressalvas ou reprovado

**Exemplo de saída:**

```
## Relatório de Revisão — CreateAppointmentService

### Resumo
Aprovado com ressalvas. A lógica está correta e segue Clean Architecture,
mas há um finding MAJOR e dois INFO.

### Findings

| # | Severidade | Arquivo | Linha | Descrição | Ação |
|---|-----------|---------|-------|-----------|------|
| 1 | MAJOR | AppointmentController.java | 47 | Falta @Valid no parâmetro do request body | Adicionar @Valid |
| 2 | INFO | CreateAppointmentService.java | 23 | Nome do método poderia ser mais expressivo | Sugestão opcional |
```

---

### Fluxo D — Cobertura de Testes Insuficiente

**Cenário:** JaCoCo reportou cobertura de 45% no `ConcludeAppointmentService`.

**Passo 1 — Plano de testes (`Test Designer`)**

```
O ConcludeAppointmentService tem cobertura de 45%. Projete os testes
que faltam para chegar a 80%.
```

O agent:
- Lê o service e a entidade de domínio
- Identifica branches não cobertas
- Produz tabela com casos de teste faltantes e métodos sugeridos

**Passo 2 — Implementar testes** (developer ou agente default)

**Passo 3 — Verificar conformidade (`Code Reviewer`)**

```
Revise os novos testes do ConcludeAppointmentServiceTest.
```

---

### Fluxo E — Manutenção do Workspace Copilot

**Cenário:** A instruction de segurança está desatualizada, precisa de nova regra.

**Passo 1 — Atualizar instruction (`Prompt Builder`)**

```
A instruction security.instructions.md precisa de uma nova regra:
"Nunca usar ObjectMapper manualmente para serializar respostas de API;
usar sempre os serializers do Spring."
Adicione isso sem quebrar o que já existe.
```

O agent:
- Lê a instruction atual
- Verifica conflitos
- Propõe a mudança antes de salvar

---

## 5. Prompts Reutilizáveis

Os prompts ficam em `.github/prompts/` e podem ser invocados via `@workspace /prompt-name` ou pelo seletor de prompts do Copilot Chat.

### 5.1 refinar-tarefa

**Arquivo:** `.github/prompts/refinar-tarefa.prompt.md`  
**Agent:** Task Refiner & Governance

Produz artefato completo de refinamento para uma demanda. Aceita 3 parâmetros:

| Parâmetro | Obrigatório | Descrição |
|-----------|-------------|-----------|
| `demanda` | Sim | Descrição da funcionalidade a implementar |
| `bounded_context` | Sim | `eventtype` / `appointment` / `infra` / `cross-cutting` |
| `contexto_adicional` | Não | Restrições, dependências, prazo |

**Exemplo de uso:**

```
demanda: Adicionar campo "observacoes" ao agendamento
bounded_context: appointment
contexto_adicional: Campo opcional, máximo 500 caracteres, deve ser retornado no output
```

---

### 5.2 criar-backlog-tarefa

**Arquivo:** `.github/prompts/criar-backlog-tarefa.prompt.md`  
**Agent:** Task Refiner & Governance

Decompõe uma tarefa grande em subtarefas atômicas mapeadas às camadas da Clean Architecture. Aceita 2 parâmetros:

| Parâmetro | Obrigatório | Descrição |
|-----------|-------------|-----------|
| `tarefa_grande` | Sim | Descrição da tarefa ou épico a decompor |
| `criterios_aceite` | Não | Critérios da tarefa pai (separados por `;`) |

**Exemplo de uso:**

```
tarefa_grande: Implementar bounded context de notificações de agendamento
criterios_aceite: Notificação enviada ao criar appointment; Notificação enviada ao cancelar
```

**Saída esperada:**

```
## Backlog: Implementar bounded context notification — TASK010

| ID       | Descrição                                | Camada          | Depende de | Prioridade |
|----------|------------------------------------------|-----------------|------------|------------|
| TASK010.1| Criar entidade Notification              | [domain]        | —          | Alta       |
| TASK010.2| Criar NotificationGateway interface      | [domain]        | TASK010.1  | Alta       |
| TASK010.3| Criar use case SendNotification          | [application]   | TASK010.2  | Alta       |
| TASK010.4| Implementar SendNotificationService      | [application]   | TASK010.3  | Alta       |
| TASK010.5| Criar adapter de envio (email/webhook)   | [infrastructure]| TASK010.2  | Média      |
```

---

## 6. Skill: task-checkpoint-memory

**Arquivo:** `.github/skills/task-checkpoint-memory/SKILL.md`

A skill é invocada automaticamente pelo Copilot quando o contexto indica que uma fase foi concluída ou uma decisão arquitetural foi tomada. Garante que o Memory Bank seja atualizado corretamente.

### Tipos de Checkpoint

| Tipo | Gatilho | Arquivos Atualizados |
|------|---------|----------------------|
| Checkpoint 0 | Início de tarefa | `tasks/TASKID.md` + `activeContext.md` |
| Checkpoint 1 | Plano aprovado | `tasks/TASKID.md` + `_index.md` |
| Checkpoint 2 | Implementação concluída | `tasks/TASKID.md` + `progress.md` + `activeContext.md` |
| Checkpoint 3 | Retrospectiva / lições | `tasks/TASKID.md` + `progress.md` + instruction relevante |

### Exemplo de Invocação

```
A implementação do TASK009 foi concluída. Testes passando. Registre o checkpoint 2.
```

O Copilot usa a skill para:
1. Atualizar o status da subtarefa no arquivo de task
2. Atualizar `progress.md` com o que foi entregue
3. Atualizar `activeContext.md` com os próximos passos

---

## 7. Combinando Agents em um Ciclo Completo

O ciclo completo de desenvolvimento de uma feature no cal-service envolve:

```
┌─────────────────────────────────────────────────────────────────┐
│  CICLO DE DESENVOLVIMENTO — cal-service                         │
│                                                                  │
│  1. ENTRADA DA DEMANDA                                          │
│     Agent: Task Refiner & Governance                            │
│     Resultado: TASK criada no memory-bank, plano aprovado       │
│                                                                  │
│  2. MAPEAMENTO DE CONTEXTO                                      │
│     Agent: Context Architect                                     │
│     Resultado: lista de arquivos e sequência de mudanças        │
│                                                                  │
│  3. AVALIAÇÃO ARQUITETURAL (se houver decisão de design)        │
│     Agent: Architect                                             │
│     Resultado: veredicto + mapa de impacto                      │
│                                                                  │
│  4. DESIGN DE TESTES                                            │
│     Agent: Test Designer                                         │
│     Resultado: plano de testes com casos e métodos sugeridos    │
│                                                                  │
│  5. IMPLEMENTAÇÃO                                               │
│     Agente default do Copilot ou developer                      │
│                                                                  │
│  6. REVISÃO DE CÓDIGO                                           │
│     Agent: Code Reviewer                                         │
│     Resultado: relatório com findings por severidade            │
│                                                                  │
│  7. CHECKPOINT DE MEMÓRIA                                       │
│     Skill: task-checkpoint-memory                               │
│     Resultado: memory-bank atualizado, task fechada             │
└─────────────────────────────────────────────────────────────────┘
```

### Checklist de Uso dos Agents por Feature

- [ ] Task Refiner executado — tarefa criada em `memory-bank/tasks/`
- [ ] Context Architect executado — mapa de contexto aprovado
- [ ] Architect consultado (se mudança de design) — veredicto registrado
- [ ] Test Designer executado — plano de testes produzido
- [ ] Implementação concluída — `./mvnw compile` passando
- [ ] Code Reviewer executado — findings CRITICAL e MAJOR resolvidos
- [ ] `./mvnw test` passando com cobertura ≥ 80% nos use cases
- [ ] Checkpoint 2 registrado via skill

---

## 8. Referência Rápida

### Localização dos Arquivos

| Tipo | Caminho |
|------|---------|
| Agents | `.github/agents/*.agent.md` |
| Instructions | `.github/instructions/*.instructions.md` |
| Prompts | `.github/prompts/*.prompt.md` |
| Skills | `.github/skills/*/SKILL.md` |
| Memory Bank | `memory-bank/` |
| Governança | `docs/governanca-continua.md` |

### Comandos de Build Essenciais

```bash
# Compilar (verificar se não quebrou nada)
./mvnw compile

# Rodar testes
./mvnw test

# Build completo + testes + cobertura
./mvnw clean verify

# Ver relatório de cobertura
# Abrir: target/site/jacoco/index.html
```

### Links Rápidos

- [AGENTS.md](../AGENTS.md) — overview do projeto e todos os agents
- [memory-bank/activeContext.md](../memory-bank/activeContext.md) — foco atual
- [memory-bank/tasks/_index.md](../memory-bank/tasks/_index.md) — índice de tarefas
- [docs/governanca-continua.md](./governanca-continua.md) — ciclo de revisão quinzenal
- [.github/copilot-instructions.md](../.github/copilot-instructions.md) — regras gerais do projeto
