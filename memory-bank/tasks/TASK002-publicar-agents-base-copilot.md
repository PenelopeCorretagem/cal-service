# TASK002 - Publicar Agents Base Copilot (Fase 2)

**Status:** Completed
**Added:** 2026-04-05
**Updated:** 2026-04-05

## Original Request

Implementar a Fase 2 do plano de workspace GitHub Copilot: publicação dos agents base em `.github/agents/`.

## Thought Process

A Fase 2 consiste em adaptar os agents de referência (disponíveis em `docs/`) para o contexto específico do `cal-service`, adicionando:
- Padrões técnicos obrigatórios do projeto (Clean Architecture, factory methods, Use Case pattern).
- Integração com o fluxo de Memory Bank para rastreabilidade.
- Linguagem e convenções do projeto (português nos comentários, inglês no código).

O agent `task-refiner-governance` é novo (não existe em `docs/`) e foi criado do zero para atender ao requisito de checkpoints obrigatórios e refinamento estruturado de demandas.

## Implementation Plan

- [x] Criar `context-architect.agent.md` adaptado para cal-service.
- [x] Criar `prompt-builder.agent.md` adaptado para cal-service.
- [x] Criar `task-refiner-governance.agent.md` com checkpoints obrigatórios.
- [x] Atualizar README do diretório `.github/agents/`.
- [x] Registrar conclusão no Memory Bank.

## Progress Tracking

**Overall Status:** Completed - 100%

### Subtasks

| ID  | Description | Status | Updated | Notes |
|-----|-------------|--------|---------|-------|
| 2.1 | Criar context-architect.agent.md | Complete | 2026-04-05 | Adaptado com padrões cal-service |
| 2.2 | Criar prompt-builder.agent.md | Complete | 2026-04-05 | Adaptado com padrões cal-service |
| 2.3 | Criar task-refiner-governance.agent.md | Complete | 2026-04-05 | Novo agent com checkpoints obrigatórios |
| 2.4 | Atualizar memory bank | Complete | 2026-04-05 | _index.md, activeContext.md, progress.md |

## Progress Log

### 2026-04-05
- Lidos todos os arquivos core do memory bank antes de iniciar.
- Criado `context-architect.agent.md` em `.github/agents/`, adaptado do arquivo em `docs/` para incluir padrões específicos do cal-service (factory methods, use case pattern, MapStruct, OWASP).
- Criado `prompt-builder.agent.md` em `.github/agents/`, simplificado e adaptado para o contexto do projeto.
- Criado `task-refiner-governance.agent.md` em `.github/agents/` — agent novo, construído do zero com:
  - Pré-condições obrigatórias de leitura do memory bank.
  - Artefato estruturado de refinamento com objetivo, critérios de aceite, dependências, riscos, plano e checkpoints.
  - Processo de registro automático no memory bank após aprovação.
  - Limites de escopo e padrões técnicos obrigatórios embarcados.
- Atualizado memory bank: task file, _index.md, activeContext.md, progress.md.
