# Active Context — cal-service

**Última atualização**: 2026-04-05 (TASK010 iniciada — plano aprovado)

## Foco Atual

TASK010 concluída (2026-04-05). Todas as 15 subtarefas implementadas e testadas. 134 testes passando, BUILD SUCCESS.

## O Que Foi Feito Recentemente

- **TASK010 concluída (2026-04-05)**: Consumer RabbitMQ implementado end-to-end.
  - Fase A: dependência AMQP + configuração YAML + `RabbitMQConfig.java` / `RabbitMQProperties.java`.
  - Fase B: `EstateChangedMessage.java` (record) + `EstateStatus.java` (enum).
  - Fase C: `hide()` e `show()` idempotentes na entidade `EventType`.
  - Fase D: `HandleEstateChangedCommand`, `HandleEstateChangedUseCase`, `HandleEstateChangedService`, bean em `EventTypeConfig`.
  - Fase E: `EstateChangedConsumer.java` com `@RabbitListener`, tradução de status e guarda DLQ para falhas.
  - Fase F: 12 testes novos (5+3+4). Suite total: 134 testes, 0 falhas.

## O Que Foi Feito Recentemente

- Fase 0 concluída: criação de todos os arquivos core do memory bank.
- Fase 1 concluída (2026-04-05): governança base em `.github/` (copilot-instructions.md, AGENTS.md, governance.instructions.md).
- Fase 2 concluída (2026-04-05): publicação dos agents base em `.github/agents/`.
- Fase 3 concluída (2026-04-05): instructions por domínio em `.github/instructions/`.
- TASK004 concluída (2026-04-05): cobertura de testes do `appointment` completa (122 testes, BUILD SUCCESS).
- TASK005 concluída (2026-04-05): agents especializados criados:
  - `code-reviewer.agent.md` — avaliação pós-implementação com tabela de findings (CRITICAL/MAJOR/MINOR/INFO).
  - `test-designer.agent.md` — plano de testes por camada com pirâmide JUnit 5+Mockito.
  - `architect.agent.md` — mapa de impacto arquitetural com veredicto formal.
  - `AGENTS.md` atualizado com tabela de 6 agents.
- TASK007 concluída (2026-04-05): prompts reutilizáveis criados:
  - `refinar-tarefa.prompt.md` — modo `agent`, 3 parâmetros (`{demanda}`, `{bounded_context}`, `{contexto_adicional}`), produz artefato de refinamento compatível com o task-refiner-governance agent.
  - `criar-backlog-tarefa.prompt.md` — modo `agent`, 2 parâmetros (`{tarefa_grande}`, `{criterios_aceite}`), decompõe em subtarefas atômicas com tabela de camada/dependência/prioridade.
  - Seção 'Prompts Disponíveis' adicionada ao `AGENTS.md`.
- TASK006 concluída (2026-04-05): skill `task-checkpoint-memory` criada em `.github/skills/` com SKILL.md (frontmatter localizável, workflow em 4 etapas), template de checkpoint e referência de governança.
- TASK008 concluída (2026-04-05): ciclo de governança contínua formalizado:
  - `docs/governanca-continua.md` — processo completo, checklist quinzenal de 10 itens, processo de convergência lição→regra, regras de limpeza do Memory Bank.
  - `.github/skills/task-checkpoint-memory/templates/retrospectiva-ciclo-template.md` — template de revisão quinzenal com 7 seções.
  - Fase 4 do workspace Copilot concluída.

## Estado Atual do Projeto

O `cal-service` possui:
- **Bounded context `eventtype`**: Totalmente implementado. CRUD completo, toggle de visibilidade, sync com Cal.com, testes unitários passando.
- **Bounded context `appointment`**: Completamente implementado e com cobertura de testes completa (122 testes, BUILD SUCCESS). Inclui domínio, aplicação e infraestrutura.
- **Workspace Copilot** (meta-projeto): Fases 0–3 concluídas. Fase 4 em execução (TASK005–TASK008 pendentes).

## Próximos Passos

Nenhuma tarefa em andamento. Aguardando nova demanda.
2. **TASK010 — Fase B**: Criar `EstateChangedMessage.java` (record) e `EstateStatus.java` (enum) em `infrastructure/messaging/`.
3. **TASK010 — Fase C**: Adicionar `hide()` e `show()` na entidade `EventType`.
4. **TASK010 — Fases D/E**: Implementar `HandleEstateChangedCommand`, `HandleEstateChangedUseCase`, `HandleEstateChangedService`, `EstateChangedConsumer`.
5. **TASK010 — Fase F**: Testes unitários — `HandleEstateChangedServiceTest`, `EstateChangedConsumerTest`, adições em `EventTypeTest`.

## Decisões Ativas

- A senha de `.env.example` não deve conter valores reais (apenas templates).
- O scheduler de sync é desabilitado em `dev` para não poluir logs.
- MapStruct é usado para mapeamento JPA ↔ Domain ↔ Output (sem conversão manual).
- `reconstitute()` e `createNew()` são os únicos factory methods aceitos nas entidades de domínio.

## Contexto de Integração

- Cal.com API: integração ativa; adaptadores `CalComEventTypeAdapter` e `CalComBookingAdapter`.
- Monolith: integração via `MonolithEstateAdapter` para buscar empreendimentos.
- Ambas integrações acessam sistemas externos configurados por variáveis de ambiente.
