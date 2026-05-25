# [TASK020] - Replicar estrutura de CI/CD nos servicos

**Status:** Completed
**Added:** 2026-05-20
**Updated:** 2026-05-20

## Original Request
"olhando meu projeto, tem uma estrutura de ci/cd dentro do projeto api-rest. faca iguais para os outros dois projetos, por favor"

## Thought Process
A demanda pede espelhamento da estrutura de CI/CD do projeto `penelope-api-rest` para `authentication-service` e `cal-service`, mantendo o mesmo padrao de gatilhos e jobs. Para evitar conflito de publicacao de imagem Docker entre repositorios, o nome da imagem foi adaptado por servico.

## Implementation Plan
- [x] Ler e usar `penelope-api-rest/.github/workflows/ci.yml` como template.
- [x] Criar workflow equivalente em `authentication-service`.
- [x] Criar workflow equivalente em `cal-service`.
- [x] Ajustar tags Docker para cada servico.
- [x] Atualizar Memory Bank com checkpoint de conclusao.

## Progress Tracking

**Overall Status:** Completed - 100%

### Subtasks
| ID | Description | Status | Updated | Notes |
|----|-------------|--------|---------|-------|
| 20.1 | Mapear pipeline de referencia no `penelope-api-rest` | Completed | 2026-05-20 | Workflow `build -> test -> docker` confirmado |
| 20.2 | Criar workflow no `authentication-service` | Completed | 2026-05-20 | Arquivo criado em `.github/workflows/ci.yml` |
| 20.3 | Criar workflow no `cal-service` | Completed | 2026-05-20 | Arquivo criado em `.github/workflows/ci.yml` |
| 20.4 | Registrar rastreabilidade no Memory Bank | Completed | 2026-05-20 | `_index`, `activeContext` e `progress` atualizados |

## Progress Log
### 2026-05-20
- Pipeline de referencia identificado em `penelope-api-rest/.github/workflows/ci.yml`.
- Workflows equivalentes criados para `authentication-service` e `cal-service`.
- Gatilhos (`push`/`pull_request` em `develop` e `main`) e jobs (`build`, `test`, `docker`) mantidos.
- Publicacao Docker mantida apenas em push para `develop`.
- Imagens Docker ajustadas para `${{ secrets.DOCKERHUB_USERNAME }}/auth-service:latest` e `${{ secrets.DOCKERHUB_USERNAME }}/cal-service:latest`.

