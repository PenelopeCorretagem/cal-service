# TASK012 - Infraestrutura REST

**Status:** Completed
**Added:** 2026-04-19
**Updated:** 2026-04-19

## Original Request

Remediação dos findings sobre infraestrutura HTTP:
1. Introduzir fábrica/decorator central para RestClient, mantendo adapters focados em request/response.
2. Reduzir logs para DEBUG, mascarar query params e padronizar logs estruturados com correlação.
3. Atualizar exemplos e respostas documentadas para o contrato real de `ApiErrorResponse`.

## Thought Process

TASK012.1 (factory) e TASK012.3 (OpenAPI exemplos) são independentes entre si e podem ser paralelos.
TASK012.2 (LoggingInterceptor) depende logicamente de TASK012.1, pois o interceptor é configurado via factory.
TASK012.3 depende de TASK011 estar concluída para ter o contrato correto de `ApiErrorResponse` como referência.

## Implementation Plan

- [x] TASK012.1 — Criar `RestClientBuilderFactory` em `shared/http/config`; refatorar configs de clientes REST
- [x] TASK012.2 — Refatorar `LoggingInterceptor`: nível DEBUG e mascaramento de query params sensíveis
- [x] TASK012.3 — Atualizar `EventTypeControllerSwagger` e `AppointmentControllerSwagger` com contrato atual (`code`, `severity`, `violations`)

## Progress Tracking

**Overall Status:** Completed — 100%

### Subtasks

| ID | Description | Status | Updated | Notes |
|----|-------------|--------|---------|-------|
| TASK012.1 | `RestClientBuilderFactory` + refatoração dos configs | Completed | 2026-04-19 | Aplicado em `auth`, `eventtype` e `appointment` |
| TASK012.2 | `LoggingInterceptor` DEBUG + mascaramento + MDC correlação | Completed | 2026-04-19 | DEBUG + tempo de execução + sanitização de query params |
| TASK012.3 | Atualização dos exemplos OpenAPI | Completed | 2026-04-19 | `EventTypeControllerSwagger` e `AppointmentControllerSwagger` alinhados ao contrato `ApiErrorResponse` |

## Arquivos Afetados

### Criar
- `src/main/java/com/penelopec/calservice/shared/http/config/RestClientBuilderFactory.java`

### Modificar
- `src/main/java/com/penelopec/calservice/eventtype/infrastructure/config/CalClientConfig.java`
- `src/main/java/com/penelopec/calservice/appointment/infrastructure/config/AppointmentConfig.java`
- `src/main/java/com/penelopec/calservice/auth/infrastructure/config/AuthConfig.java` _(ou equivalente)_
- `src/main/java/com/penelopec/calservice/eventtype/infrastructure/config/MonolithClientConfig.java`
- `src/main/java/com/penelopec/calservice/shared/http/log/LoggingInterceptor.java`
- `src/main/java/com/penelopec/calservice/eventtype/infrastructure/controller/doc/EventTypeControllerSwagger.java`
- `src/main/java/com/penelopec/calservice/appointment/infrastructure/controller/doc/AppointmentControllerSwagger.java`

## Cobertura de Testes Necessária
- `RestClientBuilderFactory` — unitário (verifica configuração de timeout/interceptor) — Média
- `LoggingInterceptor` — unitário (verifica mascaramento de params) — Alta

## Riscos Identificados
- TASK012.1: adapters têm timeouts específicos por cliente — a factory deve suportar overrides sem forçar uma configuração única global

## Progress Log

### 2026-04-19
- Tarefa criada com plano aprovado. Aguardando início da implementação (Checkpoint 1).
- Ordem sugerida: TASK012.3 pode ser feita em paralelo com TASK012.1; TASK012.2 após TASK012.1.

### 2026-04-19 (Checkpoint 2)
- `RestClientBuilderFactory` implementada e integrada aos clients principais.
- `LoggingInterceptor` atualizado para DEBUG, duração e sanitização de query params sensíveis.
- Teste unitário de sanitização criado (`LoggingInterceptorTest`).

### 2026-04-19 (Checkpoint 2 - fechamento TASK012.3)
- `EventTypeControllerSwagger` e `AppointmentControllerSwagger` atualizados para o contrato real de `ApiErrorResponse` com exemplos usando `code`, `severity` e `violations`.
- Exemplos legados (`error`, `fieldErrors`) removidos dos casos documentados e substituídos por payloads compatíveis com o `GlobalExceptionHandler` atual.
- Respostas documentadas de integração e conflito foram alinhadas aos mapeamentos atuais (ex.: `502` para falhas de gateway e `409` para conflito de transição de status em appointment).
- Validação executada com Java 21: `./mvnw compile` seguido de `./mvnw test`; a suíte reportou 5 falhas em `GlobalExceptionHandlerTest` (status esperado 400/422/500/502 e recebido 404), fora do escopo de alteração da TASK012.3.
