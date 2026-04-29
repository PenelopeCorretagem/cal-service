# AGENTS.md — cal-service

Contexto de projeto para agentes de IA (GitHub Copilot, Claude, etc.). Padrão portável cross-tool.

---

## Visão Geral

`cal-service` é um microserviço Spring Boot (Java 21) que atua como **camada de integração centralizada com o Cal.com**, dentro do ecossistema **Penélope C** (plataforma imobiliária). O frontend nunca chama o Cal.com diretamente.

**Repositório:** `d:\sptech\projeto-pi\backend\cal-service`  
**Projeto:** Integrador SPTech

---

## Bounded Contexts

| Contexto      | Path                                      | Responsabilidade                              |
|---------------|-------------------------------------------|-----------------------------------------------|
| `eventtype`   | `src/main/java/.../eventtype/`            | Tipos de agendamento vinculados a empreendimentos |
| `appointment` | `src/main/java/.../appointment/`          | Agendamentos concretos com ciclo de vida de status |

---

## Arquitetura

Clean Architecture + DDD. Regra de dependência estrita:

```
Infrastructure → Application → Domain
```

Estrutura de pacotes por bounded context:

```
<context>/
├── domain/         ← Entidades, Value Objects, Gateways (interfaces), Repository (interface)
├── application/    ← Use Cases (interfaces), Services (@Service), Commands, Outputs, Mappers
└── infrastructure/ ← Controllers, JPA Adapters, Web Adapters, Config, Scheduler
```

### Padrões-chave

- **Factory methods**: `Entity.createNew(...)` / `Entity.reconstitute(...)`; sem construtores públicos em entidades de domínio.
- **Command Objects**: imutáveis, apenas dados, usados em operações de escrita.
- **Output Objects**: DTOs de saída dos use cases; nunca expor entidades de domínio ou JPA diretamente.
- **JPA Entity separada**: conversão via MapStruct mapper.
- **Use Case = Interface + @Service**: a interface define o contrato; o service implementa.

---

## Stack

| Tecnologia       | Versão        |
|------------------|---------------|
| Java             | 21            |
| Spring Boot      | 4.0.3         |
| MapStruct        | 1.5.5.Final   |
| SpringDoc OpenAPI| 2.7.0         |
| Auth0 JWT        | 4.4.0         |
| JaCoCo           | 0.8.12        |
| H2 (dev)         | —             |
| MySQL (prod)     | —             |

---

## Variáveis de Ambiente

| Variável               | Obrigatória | Descrição                              |
|------------------------|-------------|----------------------------------------|
| `JWT_API_KEY`          | Sim         | Segredo para validação de tokens JWT   |
| `CALCOM_API_KEY`       | Sim         | Chave de API do Cal.com                |
| `SPRING_PROFILES_ACTIVE` | Não       | Padrão: `dev`                          |
| `MONOLITH_BASE_URL`    | Prod        | URL base do monolito                   |
| `MONOLITH_API_TOKEN`   | Prod        | Token de autenticação para o monolito  |

---

## Endpoints da API

| Basepath         | Responsabilidade                   |
|------------------|------------------------------------|
| `/event-types`   | CRUD de tipos de agendamento       |
| `/appointments`  | Ciclo de vida de agendamentos      |
| `/swagger-ui`    | Swagger UI (SpringDoc)             |
| `/api-docs`      | OpenAPI JSON                       |
| `/h2-console`    | H2 Console (dev only)              |

---

## Comandos Essenciais

```bash
# Build completo + testes
./mvnw clean verify

# Apenas testes
./mvnw test

# Build sem testes
./mvnw clean package -DskipTests

# Rodar localmente (perfil dev, H2)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Relatório de cobertura (após mvnw test)
# target/site/jacoco/index.html
```

> **Atenção**: `JAVA_HOME` deve apontar para JDK 21.

---

## Agents Disponíveis

| Agent | Arquivo | Papel |
|-------|---------|-------|
| Context Architect | `.github/agents/context-architect.agent.md` | Mapeia arquivos afetados e sequência **antes** de agir (tático) |
| Task Refiner & Governance | `.github/agents/task-refiner-governance.agent.md` | Transforma demanda bruta em backlog executável com rastreabilidade |
| Prompt Builder | `.github/agents/prompt-builder.agent.md` | Cria e valida prompts e instructions com critério |
| Code Reviewer | `.github/agents/code-reviewer.agent.md` | Avalia qualidade e conformidade **após** implementação; produz findings com severidade |
| Test Designer | `.github/agents/test-designer.agent.md` | Projeta estratégia e plano de testes para uma mudança |
| Architect | `.github/agents/architect.agent.md` | Avalia viabilidade e impacto de mudanças arquiteturais (estratégico) |

---

## Prompts Disponíveis

| Prompt | Arquivo | Uso |
|--------|---------|-----|
| Refinar Tarefa | `.github/prompts/refinar-tarefa.prompt.md` | Transforma demanda bruta em artefato de refinamento com critérios de aceite, dependências e plano de implementação |
| Criar Backlog | `.github/prompts/criar-backlog-tarefa.prompt.md` | Decompõe tarefa grande em subtarefas atômicas mapeadas às camadas da Clean Architecture |

---

## Governança do Workspace

Regras detalhadas em:

- `.github/copilot-instructions.md` — Regras gerais de código, arquitetura e qualidade
- `.github/instructions/governance.instructions.md` — Políticas de execução, risco e rastreabilidade
- `.github/instructions/memory-bank.instructions.md` — Regras de leitura/atualização do memory bank
- `memory-bank/` — Documentação viva do estado atual do projeto

---

## Memory Bank

Leia **todos** os arquivos antes de iniciar qualquer tarefa:

```
memory-bank/
├── projectbrief.md       ← Escopo e objetivos
├── productContext.md     ← Por que existe, problemas que resolve
├── systemPatterns.md     ← Arquitetura e padrões técnicos
├── techContext.md        ← Stack, dependências, variáveis de ambiente
├── activeContext.md      ← Foco atual, decisões ativas, próximos passos
├── progress.md           ← O que funciona, o que falta, status geral
└── tasks/
    ├── _index.md         ← Índice de todas as tarefas com status
    └── TASKXXX-nome.md   ← Arquivo individual por tarefa
```

---

## Definição de Pronto

Uma tarefa é considerada pronta quando:

1. Código implementado seguindo Clean Architecture e as convenções acima.
2. Testes unitários criados e passando.
3. Cobertura mínima de 80% nos use cases.
4. Sem erros de compilação.
5. Sem segredos ou credenciais no código.
6. Endpoints documentados com SpringDoc/OpenAPI (se houver controller novo/modificado).
7. Memory Bank atualizado: `activeContext.md`, `progress.md` e tarefa em `tasks/`.
