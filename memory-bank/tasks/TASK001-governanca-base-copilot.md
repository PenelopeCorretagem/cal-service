# TASK001 - Implementar Governança Base Copilot (.github/)

**Status:** Completed
**Added:** 2026-04-05
**Updated:** 2026-04-05

## Original Request

Implementar a Fase 1 do plano de workspace GitHub Copilot: criar estrutura base de governança em `.github/`, incluindo `copilot-instructions.md`, `AGENTS.md`, `.github/instructions/governance.instructions.md` e as pastas `agents/` e `prompts/`.

## Thought Process

A Fase 1 é a camada de governança — ela define as regras, convenções e limites que todos os agentes e instruções seguirão. Deve refletir fielmente a arquitetura real do projeto (`cal-service`): Clean Architecture, DDD, bounded contexts, padrões de code (factory methods, command/output objects, MapStruct).

O `copilot-instructions.md` é o arquivo mais importante desta fase: funciona como "lei máxima" do repositório para o Copilot, aplicando-se a todos os arquivos.

O `AGENTS.md` serve como contexto portável para qualquer ferramenta de agentes (não só Copilot), garantindo que qualquer agente saiba rapidamente o que é o projeto, como está organizado e quais comandos são relevantes.

O `governance.instructions.md` define políticas de execução: o que exige confirmação, como rastrear mudanças, limites de escopo e integração com o memory bank.

## Implementation Plan

- [x] Criar `memory-bank/tasks/TASK001-governanca-base-copilot.md` (este arquivo)
- [x] Criar `.github/copilot-instructions.md` com regras de arquitetura, qualidade, segurança e definição de pronto
- [x] Criar `AGENTS.md` no root com contexto de projeto para agentes
- [x] Criar `.github/instructions/governance.instructions.md` com políticas de execução e rastreabilidade
- [x] Criar `.github/agents/README.md` e `.github/prompts/README.md` (estrutura para Fases 2 e 3)
- [x] Atualizar `memory-bank/tasks/_index.md`
- [x] Atualizar `memory-bank/activeContext.md`
- [x] Atualizar `memory-bank/progress.md`

## Progress Tracking

**Overall Status:** Completed - 100%

### Subtasks

| ID  | Description | Status | Updated | Notes |
|-----|-------------|--------|---------|-------|
| 1.1 | Criar arquivo de tarefa TASK001 | Complete | 2026-04-05 | Este arquivo |
| 1.2 | Criar `.github/copilot-instructions.md` | Complete | 2026-04-05 | |
| 1.3 | Criar `AGENTS.md` no root | Complete | 2026-04-05 | |
| 1.4 | Criar `governance.instructions.md` | Complete | 2026-04-05 | |
| 1.5 | Criar estrutura de pastas `.github/agents/` e `.github/prompts/` | Complete | 2026-04-05 | README.md como placeholder |
| 1.6 | Atualizar memory bank | Complete | 2026-04-05 | |

## Progress Log

### 2026-04-05
- Tarefa criada com base na Fase 1 do plano de implementação do workspace Copilot.
- Criado `.github/copilot-instructions.md` com regras completas de arquitetura (Clean Arch + DDD), convenções Java, testes, segurança, API REST e definição de pronto.
- Criado `AGENTS.md` no root com contexto portável de projeto: bounded contexts, stack, variáveis de ambiente, endpoints, comandos e links para governança.
- Criado `.github/instructions/governance.instructions.md` com políticas de execução, ações que exigem confirmação, rastreabilidade via memory bank, limites de escopo e regras de qualidade.
- Criados `.github/agents/README.md` e `.github/prompts/README.md` como placeholders estruturais para a Fase 2.
- Memory bank atualizado: `_index.md`, `activeContext.md`, `progress.md`.
- Fase 1 concluída com sucesso.
