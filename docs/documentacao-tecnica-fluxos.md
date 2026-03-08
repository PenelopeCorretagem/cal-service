# 📅 cal-service — Documentacao Tecnica de Fluxos

**Microsservico de integracao com Cal.com** | Java 21 + Spring Boot | Clean Architecture + DDD

---

## Indice

| # | Secao | Descricao |
|:---:|---|---|
| 01 | [🎯 Objetivo e Escopo](#1-objetivo-e-escopo) | O que este servico faz |
| 02 | [🧭 Navegacao Rapida](#2-navegacao-rapida) | Atalhos por objetivo e perfil |
| 03 | [🏗️ Visao Arquitetural](#3-visao-arquitetural) | Camadas e direcao de dependencia |
| 04 | [🧩 Componentes Principais](#4-componentes-principais) | Domain, Application, Infrastructure |
| 05 | [🔄 Fluxo HTTP Geral](#5-fluxo-http-geral) | Pipeline request/response |
| 06 | [🗺️ Mapa de Endpoints](#6-mapa-de-endpoints) | Rotas, metodos e status codes |
| 07 | [⚙️ Fluxos Funcionais](#7-fluxos-funcionais) | Passo a passo de cada caso de uso |
| 08 | [🔗 Integracoes Externas](#8-integracoes-externas) | Cal.com API + Monolito |
| 09 | [💾 Persistencia Local](#9-persistencia-local) | Tabela JPA e colunas |
| 10 | [🔒 Seguranca e Acesso](#10-seguranca-e-acesso) | JWT, CORS, profiles |
| 11 | [🚨 Tratamento de Erros](#11-tratamento-de-erros-http) | Mapa excecao -> HTTP status |
| 12 | [🔧 Configuracoes Operacionais](#12-configuracoes-operacionais) | Variaveis de ambiente e profiles |
| 13 | [🧪 Testes e Cobertura](#13-testes-e-cobertura) | Estrategia de testes por camada |
| 14 | [⚠️ Riscos Tecnicos](#14-riscos-tecnicos-e-atencoes) | Pontos de atencao conhecidos |
| 15 | [🚀 Runbook de Onboarding](#15-runbook-de-onboarding) | Guia rapido para novos membros |

---

## 1. Objetivo e Escopo

> **Para que serve este documento?**
> Acelerar o onboarding da squad e reduzir o tempo de entendimento da base de codigo do `cal-service`.

**Escopo funcional atual:**

| Funcionalidade | Descricao |
|---|---|
| ✅ Gerenciamento de EventType | CRUD completo via Cal.com API |
| ✅ Vinculo com empreendimento | Persistencia local de `estateId` |
| ✅ Sync automatica | Sincronizacao periodica com o monolito |

[⬆️ voltar ao indice](#indice)

---

## 2. Navegacao Rapida

### 🔍 Por objetivo

| Quero entender... | Ir para |
|---|---|
| 🏗️ Arquitetura do servico | [Visao Arquitetural](#3-visao-arquitetural) |
| 🗺️ Quais endpoints existem | [Mapa de Endpoints](#6-mapa-de-endpoints) |
| ⚙️ Fluxo de cada caso de uso | [Fluxos Funcionais](#7-fluxos-funcionais) |
| 🔄 Como funciona a sincronizacao | [Sync automatica](#77-sync-automatica-com-monolito) |
| 🔗 Dependencias externas | [Integracoes Externas](#8-integracoes-externas) |
| 🔒 Seguranca e perfis | [Seguranca e Acesso](#10-seguranca-e-acesso) |
| ❌ Erros e codigos HTTP | [Tratamento de Erros](#11-tratamento-de-erros-http) |
| 🚀 Como subir e validar rapido | [Runbook de Onboarding](#15-runbook-de-onboarding) |

### 👥 Por perfil da squad

| Perfil | Secoes recomendadas | Foco |
|:---:|---|---|
| 👨‍💻 **Backend** | `3` `4` `7` `9` `11` `13` | Arquitetura, fluxos e dominio |
| 🔍 **QA** | `6` `7` `11` `13` `15` | Endpoints, erros e testes |
| 🐳 **DevOps** | `10` `12` `14` `15` | Seguranca, config e riscos |
| 👑 **Tech Lead** | `3` `7` `8` `14` | Visao geral e integracoes |

[⬆️ voltar ao indice](#indice)

---

## 3. Visao Arquitetural

O projeto segue **Clean Architecture** com orientacao a **DDD**.

```
                    +---------------------+
                    |   Infrastructure    |
                    |  (Controllers, JPA, |
                    |   Adapters, Config) |
                    +--------+------------+
                             |
                             v
                    +---------------------+
                    |    Application      |
                    |   (Use Cases, DTOs, |
                    |    Ports, Services) |
                    +--------+------------+
                             |
                             v
                    +---------------------+
                    |      Domain         |
                    | (Entities, VOs,     |
                    |  Strategies, Ports) |
                    +---------------------+
```

> 💡 **Regra de ouro:** dependencias sempre apontam para o centro
> `infrastructure` → `application` → `domain`

| Camada | Responsabilidade | Pacote |
|---|---|---|
| **Domain** | Regras de negocio puras, contratos de saida | `c.p.calservice.domain` |
| **Application** | Casos de uso e orquestracao | `c.p.calservice.application` |
| **Infrastructure** | Web, seguranca, persistencia, HTTP clients, scheduler | `c.p.calservice.infrastructure` |

[⬆️ voltar ao indice](#indice)

---

## 4. Componentes Principais

### 4.1 🟠 Domain

| Tipo | Componente | Descricao |
|---|---|---|
| Entidade | `EventType` | Entidade central do dominio |
| Value Object | `Slug` | Identificador textual gerado a partir do titulo |
| Port | `CalComEventTypeGateway` | Contrato de saida para Cal.com |
| Port | `EstateGateway` | Contrato de saida para o monolito |
| Port | `EventTypeRepository` | Contrato de saida para persistencia local |

### 4.2 🔵 Application

| Tipo | Componente | Descricao |
|---|---|---|
| Use Case | `Create`, `Get`, `List`, `Update`, `Delete` | CRUD padrao |
| Use Case | `ToggleVisibility`, `Sync` | Operacoes especializadas |
| Port In | `application/port/in/*` | Interfaces de entrada dos use cases |
| DTO | `EventTypeOutput` | Saida padrao de todos os use cases |

### 4.3 🔴 Infrastructure

| Tipo | Componente | Descricao |
|---|---|---|
| Controller | `EventTypeController` | Endpoints REST |
| Adapter | `CalComEventTypeAdapter` | Integracao com Cal.com API |
| Adapter | `MonolithEstateAdapter` | Integracao com monolito |
| Adapter | `EventTypeRepositoryAdapter` | Persistencia JPA |
| Scheduler | `EventTypeSyncScheduler` | Rotina de sincronizacao |
| Security | `SecurityConfig` + `SecurityFilter` | JWT e CORS |
| Error | `GlobalExceptionHandler` | Tratamento global de excecoes |

[⬆️ voltar ao indice](#indice)

---

## 5. Fluxo HTTP Geral

```
  Client
    |
    v
 [EventTypeController]  ------>  Validacao de request
    |
    v
 [UseCase / Service]    ------>  Logica de negocio
    |
    v
 [Domain Entity]        ------>  Regras e invariantes
    |
    +---> [Gateway/Port]  ---->  Cal.com API / Monolito
    |
    +---> [Repository]    ---->  JPA (banco local)
    |
    v
 [Mapper]
    |
    v
 [EventTypeOutput]  ----------> ResponseEntity -> Client
```

[⬆️ voltar ao indice](#indice)

---

## 6. Mapa de Endpoints

| Metodo | Rota | Caso de Uso | Status | Detalhes |
|:---:|---|---|:---:|:---:|
| 🟢 `POST` | `/event-types` | Criar EventType | `201` | [ver fluxo](#71-criar-eventtype) |
| 🔵 `GET` | `/event-types/{eventTypeId}` | Buscar por ID | `200` | [ver fluxo](#72-buscar-por-id) |
| 🔵 `GET` | `/event-types` | Listar todos | `200` | [ver fluxo](#73-listar-todos) |
| 🟠 `PATCH` | `/event-types/{eventTypeId}` | Atualizar | `200` | [ver fluxo](#74-atualizar) |
| 🔴 `DELETE` | `/event-types/{eventTypeId}` | Excluir | `204` | [ver fluxo](#75-excluir) |
| 🟠 `PATCH` | `/event-types/{eventTypeId}/toggle-visibility` | Alternar visibilidade | `200` | [ver fluxo](#76-alternar-visibilidade) |

> 📖 **Documentacao interativa:** acesse `/swagger-ui` ou `/api-docs` com o servico rodando.

[⬆️ voltar ao indice](#indice)

---

## 7. Fluxos Funcionais

### 7.1 Criar EventType

> **`POST /event-types`** | Retorna `201 Created`

**Resumo:** cria um EventType no Cal.com e persiste o vinculo local com `estateId`.

```
Request -> Controller (valida) -> CreateEventTypeService
              |
              +-> EventType.createNew(...) com defaults
              +-> calComGateway.create(...)
              +-> eventTypeRepository.save(...)
              |
              v
         201 Created + header Location
```

**Defaults aplicados quando campo e nulo:**

| Campo | Valor default |
|---|:---:|
| `lengthInMinutes` | `60` |
| `minimumBookingNotice` | `120` |
| `hidden` | `false` |

**Regras de dominio:**
- ❗ `title` obrigatorio e nao vazio
- ❗ `estateId` obrigatorio
- ⚡ `slug` gerado automaticamente a partir de `title`

---

### 7.2 Buscar por ID

> **`GET /event-types/{id}`** | Retorna `200 OK` ou `404 Not Found`

**Resumo:** prioriza dado remoto do Cal.com, enriquece com `estateId` local quando necessario.

```
Request -> GetEventTypeService
              |
              +-> calComGateway.findById(id)
              |     |
              |     +-- encontrado sem estateId? -> enriquece via eventTypeRepository.findById
              |     +-- nao encontrado? -> 404
              |
              v
         200 OK + EventTypeOutput
```

---

### 7.3 Listar todos

> **`GET /event-types`** | Retorna `200 OK` (lista, inclusive vazia)

**Resumo:** lista event types do usuario autenticado no Cal.com.

```
Request -> ListEventTypesService
              |
              +-> calComGateway.listAll()
              |     |
              |     +-> GET /v2/me (busca username)
              |     +-> GET /v2/event-types?username=...
              |
              v
         200 OK + List<EventTypeOutput>
```

---

### 7.4 Atualizar

> **`PATCH /event-types/{id}`** | Retorna `200 OK`

**Resumo:** atualizacao parcial - atualiza remoto primeiro, depois persiste local.

```
Request -> UpdateEventTypeService
              |
              +-> eventTypeRepository.findById(id) -- nao achou? -> 404
              +-> atualiza campos enviados (title, description, length, notice)
              +-> calComGateway.update(...)        -- falhou?    -> 500
              +-> eventTypeRepository.save(...)
              |
              v
         200 OK + EventTypeOutput
```

**Campos atualizaveis:** `title`, `description`, `lengthInMinutes`, `minimumBookingNotice`

---

### 7.5 Excluir

> **`DELETE /event-types/{id}`** | Retorna `204 No Content`

**Resumo:** remove do Cal.com primeiro, depois do banco local.

```
Request -> DeleteEventTypeService
              |
              +-> calComGateway.delete(id)
              +-> eventTypeRepository.deleteById(id)
              |
              v
         204 No Content
```

> ⚠️ **Ordem importa:** exclusao remota antes da local garante consistencia.

---

### 7.6 Alternar visibilidade

> **`PATCH /event-types/{id}/toggle-visibility`** | Retorna `200 OK`

**Resumo:** inverte o campo `hidden` e sincroniza em ambos os lados.

```
Request -> ToggleEventTypeVisibilityService
              |
              +-> eventTypeRepository.findById(id)
              +-> eventType.toggleHidden()
              +-> calComGateway.update(...)
              +-> eventTypeRepository.save(...)
              |
              v
         200 OK + EventTypeOutput
```

---

### 7.7 Sync automatica com monolito

> **Componente:** `EventTypeSyncScheduler` → `SyncEventTypesUseCase`
> **Cron:** `0 */5 * * * *` (a cada 5 minutos)

```
Scheduler (cron) -> SyncEventTypesService
                       |
                       +-> estateGateway.fetchAllEstates()
                       +-> eventTypeRepository.findAll()
                       +-> valida duplicidade por estateId
                       |
                       +--[ Para cada empreendimento ATIVO ]--+
                       |   nao existe local? -> cria Cal.com + persiste
                       |   existe com divergencia? -> atualiza Cal.com + local
                       |
                       +--[ Para event types SEM empreendimento ativo ]--+
                       |   visivel? -> desativa (hidden=true) Cal.com + local
                       |
                       v
                  Log: criados / atualizados / desativados
```

**Regras de sync:**

| Cenario | Acao |
|---|---|
| Empreendimento ativo sem event type local | ➕ Cria no Cal.com + persiste local |
| Empreendimento ativo com divergencia | ✏️ Atualiza Cal.com + local |
| Event type local sem empreendimento ativo | 🚫 Desativa (`hidden=true`) |
| Duplicidade de `estateId` detectada | 🛑 Encerra com `IllegalStateException` |

> 🛡️ **Tolerancia a falha:** erro em um empreendimento nao interrompe os demais. Erros sao logados com contexto.

[⬆️ voltar ao indice](#indice)

---

## 8. Integracoes Externas

### 8.1 Cal.com API

| Item | Detalhe |
|---|---|
| **Client** | `RestClient` |
| **Auth** | `Authorization: Bearer {CALCOM_API_KEY}` |
| **Versao** | Header `cal-api-version: {calcom.api.version-v2}` |

**Endpoints consumidos:**

| Metodo | Endpoint | Uso |
|:---:|---|---|
| `POST` | `/v2/event-types` | Criar event type |
| `PATCH` | `/v2/event-types/{id}` | Atualizar event type |
| `DELETE` | `/v2/event-types/{id}` | Excluir event type |
| `GET` | `/v2/event-types/{id}` | Buscar por ID |
| `GET` | `/v2/me` | Obter usuario autenticado |
| `GET` | `/v2/event-types?username=...` | Listar por username |

**Mapeamento:** DTOs em `infrastructure/calcom/dto` → `CalComEventTypeMapper` → dominio

**Excecoes:**
- `EventTypeCreationException` - falha ao criar/atualizar
- `EventTypeDeletionException` - falha ao excluir
- `EventTypeNotFoundException` - nao encontrado

---

### 8.2 Monolito

| Item | Detalhe |
|---|---|
| **Client** | `RestClient` com base URL configuravel |
| **Auth** | `Authorization` (opcional, quando token existe) |
| **Endpoint** | `GET {monolith.api.estates-path}` |

**Mapeamento:** `MonolithEstateResponse` → `EstateData`

> 💡 Campo `ativo` nulo e tratado como `false`.

[⬆️ voltar ao indice](#indice)

---

## 9. Persistencia Local

**Tabela:** `tipo_evento`

| Coluna | Tipo | Observacao |
|---|---|---|
| `id` | PK | ID do EventType no Cal.com |
| `titulo` | String | Nome do event type |
| `slug` | String | **Unico** |
| `descricao` | String | Descricao do evento |
| `duracao_minutos` | Integer | Duracao em minutos |
| `antecedencia_minima` | Integer | Antecedencia minima para booking |
| `oculto` | Boolean | Visibilidade |
| `empreendimento_id` | Long | FK logica para o monolito |

**Repositorio:** `EventTypeJpaRepository` exposto via `EventTypeRepositoryAdapter`
- Busca adicional por `estateId`

> ⚠️ **Ponto de atencao:** o fluxo de sync valida duplicidade por `estateId`, mas o mapeamento JPA nao explicita `unique` para `empreendimento_id`.

[⬆️ voltar ao indice](#indice)

---

## 10. Seguranca e Acesso

### Profiles

| Profile | Comportamento |
|:---:|---|
| `dev` | 🔓 Todas as rotas liberadas (`permitAll`) |
| `prod` | 🔒 JWT obrigatorio para rotas nao-whitelist |

### Whitelist (rotas publicas)

```
/error
/swagger-ui/**
/api-docs/**
/swagger-resources/**
/webjars/**
/h2-console/**
```

### JWT

| Item | Detalhe |
|---|---|
| Header | `Authorization: Bearer <token>` |
| Algoritmo | HMAC256 |
| Secret | `app.security.token.secret` |

> ℹ️ Token invalido nao interrompe o filtro - a requisicao segue sem autenticacao e o bloqueio ocorre pelo security chain.

### CORS

| Item | Detalhe |
|---|---|
| Origins | Configuravel via `app.cors.allowed-origins` |
| Metodos | `GET` `POST` `PUT` `DELETE` `PATCH` `OPTIONS` |

[⬆️ voltar ao indice](#indice)

---

## 11. Tratamento de Erros HTTP

**Handler central:** `GlobalExceptionHandler`

| Excecao | Status | Quando ocorre |
|---|:---:|---|
| `EventTypeNotFoundException` | 🟥 `404` | Recurso nao encontrado no Cal.com |
| `EventTypeCreationException` | 🟥 `500` | Falha ao criar/atualizar no Cal.com |
| `EventTypeDeletionException` | 🟥 `500` | Falha ao excluir no Cal.com |
| `MethodArgumentNotValidException` | 🟧 `422` | Validacao de request falhou |
| `IllegalArgumentException` | 🟧 `422` | Regra de entrada violada |

**Formato de resposta:**

```json
{
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "Descricao do erro",
  "path": "/event-types",
  "timestamp": "2026-03-07T...",
  "fieldErrors": [ ]
}
```

[⬆️ voltar ao indice](#indice)

---

## 12. Configuracoes Operacionais

### Arquivos de configuracao

| Arquivo | Ambiente | Database | Scheduler |
|---|---|---|---|
| `application.yml` | Defaults globais | - | - |
| `application-dev.yml` | Desenvolvimento | H2 (memoria) | ❌ Desativado |
| `application-prod.yml` | Producao | MySQL + Hikari | ✅ Ativo |

### Variaveis de ambiente

| Variavel | Obrigatoria em prod | Descricao |
|---|:---:|---|
| `JWT_API_KEY` | ✅ | Secret para validacao JWT |
| `CALCOM_API_KEY` | ✅ | API key do Cal.com |
| `MONOLITH_BASE_URL` | ✅ | URL base do monolito |
| `MONOLITH_API_TOKEN` | 🟡 | Token de autenticacao do monolito |
| `SYNC_CRON` | 🟡 | Expressao cron customizada |
| `SYNC_ENABLED` | 🟡 | Habilita/desabilita scheduler |
| `DB_HOST` | ✅ | Host do banco MySQL |
| `DB_PORT` | ✅ | Porta do banco |
| `DB_NAME` | ✅ | Nome do banco |
| `DB_USER` | ✅ | Usuario do banco |
| `DB_PASSWORD` | ✅ | Senha do banco |

[⬆️ voltar ao indice](#indice)

---

## 13. Testes e Cobertura

**Pasta base:** `src/test/java/com/penelopec/calservice`

| Camada | Testes | Componentes cobertos |
|---|---|---|
| **Application** | Unitarios | `Create`, `Get`, `List`, `Update`, `Delete`, `Toggle`, `Sync` |
| **Domain** | Unitarios | `EventType`, `Slug`, excecoes |
| **Infrastructure** | Integracao | Adapters Cal.com, Monolito, Repositorio JPA |

[⬆️ voltar ao indice](#indice)

---

## 14. Riscos Tecnicos e Atencoes

| # | Risco | Impacto |
|:---:|---|---|
| 1 | Divergencia entre estado remoto e local em falhas parciais de rede | Dados inconsistentes entre Cal.com e banco local |
| 2 | Sync depende da disponibilidade do monolito e da API Cal.com | Sync pode falhar silenciosamente |
| 3 | Ausencia de constraint `UNIQUE` para `empreendimento_id` no banco | Duplicidade possivel fora do fluxo controlado |
| 4 | Token JWT invalido nao retorna erro imediato no filtro | Bloqueio ocorre por ausencia de auth no chain, nao no filtro |

[⬆️ voltar ao indice](#indice)

---

## 15. Runbook de Onboarding

### 🕐 Etapa 1 - Primeiros 30 minutos

> **Objetivo:** subir o servico e explorar a API interativamente.

- [ ] Subir projeto com profile `dev` (sem bloqueio de auth)
- [ ] Acessar `/swagger-ui` no navegador
- [ ] Executar CRUD completo de EventType
- [ ] Validar codigos HTTP: `201`, `200`, `204`, `404`, `422`, `500`

### 📖 Etapa 2 - Primeira leitura tecnica

> **Objetivo:** entender os fluxos de codigo.

| Ordem | Arquivo | Porque ler |
|:---:|---|---|
| 1 | `EventTypeController` | Entrada HTTP e mapeamento de rotas |
| 2 | `CreateEventTypeService` | Fluxo de criacao completo |
| 3 | `UpdateEventTypeService` | Fluxo de atualizacao parcial |
| 4 | `CalComEventTypeAdapter` | Contrato com API externa |
| 5 | `SyncEventTypesService` | Logica de sincronizacao |
| 6 | `EventTypeSyncScheduler` | Agendamento automatico |

### ✅ Etapa 3 - Checklist de ambientacao

> Marque cada item quando sentir confianca:

- [ ] Entendi a separacao `domain` / `application` / `infrastructure`
- [ ] Sei onde o dado e persistido localmente (tabela `tipo_evento`)
- [ ] Sei quais falhas retornam `422`, `404` e `500`
- [ ] Sei reproduzir um fluxo de sync manualmente
- [ ] Sei quais variaveis de ambiente sao obrigatorias em producao

[⬆️ voltar ao indice](#indice)

---

*Documentacao gerada para onboarding da squad* | *cal-service*
