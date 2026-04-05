# Progress — cal-service

**Última atualização**: 2026-04-05 (Fase 4 concluída)

## Status Geral

| Área                          | Status           | Notas                                                  |
|-------------------------------|------------------|--------------------------------------------------------|
| Bounded Context `eventtype`   | ✅ Implementado   | CRUD, toggle, sync, testes unitários passando          |
| Bounded Context `appointment` | ✅ Implementado   | CRUD, ciclo de status, testes domínio e serviços passando |
| Memory Bank (Workspace Copilot)| ✅ Fase 0 concluída | Arquivos core criados                                |
| Governança Copilot (.github/) | ✅ Fase 1 concluída | copilot-instructions.md, AGENTS.md, governance.instructions.md |
| Agents Copilot                | ✅ Fase 2 + TASK005 concluídas | context-architect, prompt-builder, task-refiner-governance, code-reviewer, test-designer, architect |
| Instructions por domínio      | ✅ Fase 3 concluída | java-spring, testing, security, api-contract          |
| Skills Copilot                | ✅ TASK006 concluída | task-checkpoint-memory (SKILL.md, template, referência) |
| Prompts Copilot               | ✅ TASK007 concluída | refinar-tarefa, criar-backlog-tarefa |
| Governança Contínua           | ✅ TASK008 concluída | checklist quinzenal, template retro, docs/governanca-continua.md |


## O Que Funciona

### Bounded Context `eventtype`
- `CreateEventTypeService` — Cria novo tipo de agendamento com slug gerado do título.
- `GetEventTypeService` — Busca tipo de agendamento por ID.
- `ListEventTypesService` — Lista todos os tipos de agendamento.
- `UpdateEventTypeService` (ChangeEventTypeService) — Atualiza título/descrição/duração/notificação.
- `DeleteEventTypeService` — Remove tipo de agendamento.
- `ToggleEventTypeVisibilityService` — Alterna visibilidade (hidden/visible).
- `SyncEventTypesService` — Sincroniza com Cal.com.
- `EventTypeRepositoryAdapter` — Adapter JPA para persistência.
- `CalComEventTypeAdapter` — Adapter para Cal.com API.
- `MonolithEstateAdapter` — Adapter para API do monolito.
- `SyncScheduler` / `SyncJob` — Agendamento automático de sync.
- `EventTypeController` — REST API completa com Swagger (`EventTypeControllerSwagger`).
- Testes unitários: todos passando (Surefire reports em `target/surefire-reports/`).

### Bounded Context `appointment`
- `CreateAppointmentService` — Cria agendamento via Cal.com e persiste localmente.
- `GetAppointmentService` — Busca agendamento por ID.
- `ListAppointmentsService` — Lista com filtros e paginação.
- `ConfirmAppointmentService` — Confirma agendamento.
- `ConcludeAppointmentService` — Conclui agendamento.
- `CancelAppointmentService` — Cancela via Cal.com e persiste.
- `RescheduleAppointmentService` — Reagenda via Cal.com e persiste.
- `DeleteAppointmentService` — Remove agendamento (cancela remotamente se houver bookingUid).
- Testes de domínio: `AppointmentTest`, `AppointmentExceptionsTest`, `StatusTest` — cobrem factory methods, transições de status e value objects.
- 122 testes passando (BUILD SUCCESS).

### Infraestrutura
- Segurança JWT funcionando (`SecurityFilter`, `SecurityConfig`).
- OpenAPI/Swagger disponível em `/swagger-ui`.
- H2 Console disponível em `/h2-console` (dev).
- Build Maven configurado com JaCoCo para cobertura.

## O Que Falta Construir

### Alta Prioridade

_(nenhuma pendência de alta prioridade — Fases 0-4 concluídas)_

### Média Prioridade
- [ ] Configurar CI/CD pipeline.
- [ ] Definir estratégia de migration de schema (Flyway/Liquibase).

### Baixa Prioridade
- [ ] Configurar CI/CD pipeline.
- [ ] Definir estratégia de migration de schema (Flyway/Liquibase).

## Problemas Conhecidos

- O ambiente local pode ter Java 8 como padrão; garantir `JAVA_HOME` com JDK 21 antes de rodar Maven. O JDK 21 está disponível via jabba em `C:\Users\kenner.lima\.jabba\jdk\temurin@21`.

## Métricas de Qualidade

- 122 testes passando (BUILD SUCCESS em 2026-04-05).
- Testes reportados em `target/surefire-reports/` incluem todos os testes do `eventtype` e `appointment`.
- JaCoCo exec disponível em `target/jacoco.exec`.
- Relatório HTML em `target/site/jacoco/`.
