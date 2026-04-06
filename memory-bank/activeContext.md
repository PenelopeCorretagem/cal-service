# Active Context — cal-service

**Última atualização**: 2026-04-05

## Foco Atual

Nenhuma tarefa em andamento. Aguardando nova demanda.

## O Que Foi Feito Recentemente

- **TASK010 concluída (2026-04-05)**: Consumer RabbitMQ implementado end-to-end.
  - Fase A: dependência AMQP + configuração YAML + `RabbitMQConfig.java` / `RabbitMQProperties.java`.
  - Fase B: `EstateChangedMessage.java` (record) + `EstateStatus.java` (enum).
  - Fase C: `hide()` e `show()` idempotentes na entidade `EventType`.
  - Fase D: `HandleEstateChangedCommand`, `HandleEstateChangedUseCase`, `HandleEstateChangedService`, bean em `EventTypeConfig`.
  - Fase E: `EstateChangedConsumer.java` com `@RabbitListener`, tradução de status e guarda DLQ para falhas.
  - Fase F: 12 testes novos (5+3+4). Suite total: 134 testes, 0 falhas.
- **TASK008 concluída (2026-04-05)**: ciclo de governança contínua formalizado (`docs/governanca-continua.md`, template de retrospectiva quinzenal). Fase 4 do workspace Copilot concluída.
- **TASK007 concluída (2026-04-05)**: prompts reutilizáveis criados (`refinar-tarefa.prompt.md`, `criar-backlog-tarefa.prompt.md`).
- **TASK006 concluída (2026-04-05)**: skill `task-checkpoint-memory` criada em `.github/skills/`.
- **TASK005 concluída (2026-04-05)**: agents especializados criados (`code-reviewer`, `test-designer`, `architect`).
- **TASK004 concluída (2026-04-05)**: cobertura de testes do `appointment` completa (122 testes, BUILD SUCCESS).
- **Fase 3 concluída (2026-04-05)**: instructions por domínio em `.github/instructions/`.
- **Fases 0–2 concluídas (2026-04-05)**: memory bank, governança base e agents base.

## Estado Atual do Projeto

O `cal-service` possui:
- **Bounded context `eventtype`**: Totalmente implementado. CRUD completo, toggle de visibilidade, sync com Cal.com, consumer RabbitMQ para mudança de status de imóvel, testes unitários passando.
- **Bounded context `appointment`**: Completamente implementado e com cobertura de testes completa. Inclui domínio, aplicação e infraestrutura.
- **Workspace Copilot** (meta-projeto): Fases 0–4 concluídas. 6 agents especializados, instructions por domínio, prompts reutilizáveis, skill de checkpoint e processo de governança contínua.

## Próximos Passos

Nenhuma tarefa em andamento. Aguardando nova demanda.

## Decisões Ativas

- A senha de `.env.example` não deve conter valores reais (apenas templates).
- O scheduler de sync é desabilitado em `dev` para não poluir logs.
- MapStruct é usado para mapeamento JPA ↔ Domain ↔ Output (sem conversão manual).
- `reconstitute()` e `createNew()` são os únicos factory methods aceitos nas entidades de domínio.

## Contexto de Integração

- Cal.com API: integração ativa; adaptadores `CalComEventTypeAdapter` e `CalComBookingAdapter`.
- Monolith: integração via `MonolithEstateAdapter` para buscar empreendimentos.
- Ambas integrações acessam sistemas externos configurados por variáveis de ambiente.

## Decisões Ativas

- A senha de `.env.example` não deve conter valores reais (apenas templates).
- O scheduler de sync é desabilitado em `dev` para não poluir logs.
- MapStruct é usado para mapeamento JPA ↔ Domain ↔ Output (sem conversão manual).
- `reconstitute()` e `createNew()` são os únicos factory methods aceitos nas entidades de domínio.

## Contexto de Integração

- Cal.com API: integração ativa; adaptadores `CalComEventTypeAdapter` e `CalComBookingAdapter`.
- Monolith: integração via `MonolithEstateAdapter` para buscar empreendimentos.
- Ambas integrações acessam sistemas externos configurados por variáveis de ambiente.
