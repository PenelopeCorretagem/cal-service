# TASK010 - RabbitMQ Estate Changed Consumer

**Status:** Completed
**Added:** 2026-04-05
**Updated:** 2026-04-05

## Original Request

Implementar um fluxo de mensageria utilizando RabbitMQ, no qual o monólito será responsável por publicar eventos sempre que houver alteração no estado da entidade Estate. A partir desses eventos, o microserviço deverá consumir as mensagens e realizar a atualização do estado do EventType correspondente.

## Thought Process

O `cal-service` já possui `EventTypeRepository.findByEstateId(Long)` e `CalComEventTypeGateway`, portanto a lógica de atualização de visibilidade pode ser construída sem alterar as interfaces existentes. A entidade `EventType` já possui `toggleHidden()`, mas precisará de `hide()` e `show()` para operação idempotente.

Os use cases do context `eventtype` são wireados manualmente via `@Bean` em `EventTypeConfig.java` (não via `@Service`), então o novo `HandleEstateChangedService` seguirá o mesmo padrão.

A topologia escolhida é `topic exchange` para permitir expansão futura de eventos de Estate (ex.: `estate.created`, `estate.deleted`) sem reconfiguração. Dead Letter Queue configurada para mensagens com falha de processamento.

## Implementation Plan

### Fase A — Dependência e Configuração
- [ ] A1: `pom.xml` — adicionar `spring-boot-starter-amqp`
- [ ] A2: `application.yml` — bloco `spring.rabbitmq` + `rabbitmq.queues`
- [ ] A3: `application-dev.yml` — defaults dev (localhost:5672, guest/guest)
- [ ] A4: `RabbitMQConfig.java` em `eventtype/infrastructure/config/` — declarar Queue (com DLQ args), TopicExchange, Binding, DirectExchange (DLX), Queue (DLQ), Jackson2JsonMessageConverter, RabbitListenerContainerFactory

### Fase B — Contrato da Mensagem
- [ ] B1: `EstateChangedMessage.java` (record) em `eventtype/infrastructure/messaging/`
- [ ] B2: `EstateStatus.java` (enum) em `eventtype/infrastructure/messaging/`

### Fase C — Domínio (ajustes mínimos)
- [ ] C1: `EventType.java` — adicionar `hide()` e `show()` para operação idempotente

### Fase D — Camada de Aplicação
- [ ] D1: `HandleEstateChangedCommand.java` (record) em `eventtype/application/command/`
- [ ] D2: `HandleEstateChangedUseCase.java` (interface) em `eventtype/application/port/in/`
- [ ] D3: `HandleEstateChangedService.java` em `eventtype/application/service/`
- [ ] D4: `EventTypeConfig.java` — registrar bean `HandleEstateChangedUseCase`

### Fase E — Consumer (Infrastructure)
- [ ] E1: `EstateChangedConsumer.java` em `eventtype/infrastructure/messaging/`

### Fase F — Testes
- [ ] F1: `HandleEstateChangedServiceTest.java` — cenários: INACTIVE com EventType visível, ACTIVE com EventType oculto, no-op (estado já correto), estateId sem EventType
- [ ] F2: `EstateChangedConsumerTest.java` — verifica delegação ao use case
- [ ] F3: `EventTypeTest.java` (existente) — adicionar casos para `hide()` e `show()`

## Topologia RabbitMQ

```
Monolito                    cal-service
   │                            │
   │  estate.exchange           │
   │  (type: topic)             │
   │                            │
   └──► routing key: ──────────►│ Queue: cal-service.estate-changed
        estate.changed          │
                                │     ↓ @RabbitListener
                                │  EstateChangedConsumer
                                │     ↓
                                │  HandleEstateChangedUseCase
                                │     ↓ (hide/show EventType)
                                │  EventTypeRepository + CalComGateway
                                │
                                │  [FALHA] → DLX → cal-service.estate-changed.dlq
```

| Componente | Nome | Tipo |
|---|---|---|
| Exchange principal | `estate.exchange` | `topic` |
| Queue de consumo | `cal-service.estate-changed` | durable, manual ack |
| Routing key | `estate.changed` | — |
| Dead Letter Exchange | `cal-service.dlx` | `direct` |
| Dead Letter Queue | `cal-service.estate-changed.dlq` | durable |

## Contrato da Mensagem

Payload JSON publicado pelo monolito:

```json
{
  "estateId": 42,
  "newStatus": "INACTIVE",
  "occurredAt": "2026-04-05T14:30:00Z"
}
```

| `newStatus` | Ação no `EventType` |
|---|---|
| `INACTIVE` | `hidden = true` (ocultar via Cal.com + persistir) |
| `ACTIVE` | `hidden = false` (revelar via Cal.com + persistir) |
| Estado já correto | no-op (idempotente) |
| Outro valor | Log WARN, descartar sem falhar |
| `estateId` sem EventType | Log WARN, sem exceção |

## Variáveis de Ambiente

| Variável | Dev padrão | Descrição |
|---|---|---|
| `RABBITMQ_HOST` | `localhost` | Host do broker |
| `RABBITMQ_PORT` | `5672` | Porta AMQP |
| `RABBITMQ_USERNAME` | `guest` | Usuário |
| `RABBITMQ_PASSWORD` | `guest` | Senha |
| `RABBITMQ_QUEUE_ESTATE_CHANGED` | `cal-service.estate-changed` | Nome da queue |
| `RABBITMQ_EXCHANGE_ESTATE` | `estate.exchange` | Nome do exchange |

## Mapa de Contexto

**Arquivos criados:**
- `eventtype/infrastructure/config/RabbitMQConfig.java`
- `eventtype/infrastructure/messaging/EstateChangedMessage.java`
- `eventtype/infrastructure/messaging/EstateChangedConsumer.java`
- `eventtype/infrastructure/messaging/EstateStatus.java`
- `eventtype/application/command/HandleEstateChangedCommand.java`
- `eventtype/application/port/in/HandleEstateChangedUseCase.java`
- `eventtype/application/service/HandleEstateChangedService.java`

**Arquivos modificados:**
- `pom.xml`
- `application.yml`
- `application-dev.yml`
- `eventtype/domain/entity/EventType.java`
- `eventtype/infrastructure/config/EventTypeConfig.java`

**Testes criados:**
- `HandleEstateChangedServiceTest.java`
- `EstateChangedConsumerTest.java`
- Adições em `EventTypeTest.java`

## Progress Tracking

**Overall Status:** Completed - 100%

### Subtasks
| ID  | Description | Status | Updated | Notes |
|-----|-------------|--------|---------|-------|
| A1  | pom.xml — spring-boot-starter-amqp | Completed | 2026-04-05 | |
| A2  | application.yml — bloco rabbitmq | Completed | 2026-04-05 | |
| A3  | application-dev.yml — defaults dev | Completed | 2026-04-05 | auto-startup: false em dev |
| A4  | RabbitMQConfig.java | Completed | 2026-04-05 | RabbitMQProperties.java criado também |
| B1  | EstateChangedMessage.java | Completed | 2026-04-05 | |
| B2  | EstateStatus.java | Completed | 2026-04-05 | |
| C1  | EventType.hide() / show() | Completed | 2026-04-05 | |
| D1  | HandleEstateChangedCommand.java | Completed | 2026-04-05 | |
| D2  | HandleEstateChangedUseCase.java | Completed | 2026-04-05 | |
| D3  | HandleEstateChangedService.java | Completed | 2026-04-05 | |
| D4  | EventTypeConfig.java — novo bean | Completed | 2026-04-05 | |
| E1  | EstateChangedConsumer.java | Completed | 2026-04-05 | |
| F1  | HandleEstateChangedServiceTest.java | Completed | 2026-04-05 | 5 testes |
| F2  | EstateChangedConsumerTest.java | Completed | 2026-04-05 | 3 testes |
| F3  | EventTypeTest.java — hide/show | Completed | 2026-04-05 | 4 testes |

## Progress Log

### 2026-04-05 — Fases E e F concluídas — TASK010 completa
- E1: `EstateChangedConsumer.java` criado em `infrastructure/messaging/`. Traduz `EstateStatus → boolean hide` antes de criar o Command. Mensagem com `newStatus` nulo descartada com WARN. Usa `@RabbitListener` com property placeholder `${rabbitmq.queues.estate-changed}` e factory registrada.
- F1: `HandleEstateChangedServiceTest.java` — 5 testes: hide (INACTIVE+visível), show (ACTIVE+oculto), no-op (já oculto), no-op (já visível), estateId sem EventType.
- F2: `EstateChangedConsumerTest.java` — 3 testes: INACTIVE→hide=true, ACTIVE→hide=false, null newStatus→no-op.
- F3: `EventTypeTest.java` — 4 testes adicionados na nested class `HideAndShow`: hide/show com estado oposto + idempotência.
- Suite completa: 134 testes, 0 falhas, BUILD SUCCESS.

### 2026-04-05 — Fases C e D concluídas
- C1: `hide()` e `show()` adicionados à entidade `EventType` (operação idempotente — sem toggle; aplica o estado desejado diretamente).
- D1: `HandleEstateChangedCommand.java` (record) criado com `estateId` + `hide` boolean. O Consumer (infra) faz a tradução de `EstateStatus` → `boolean` antes de criar o Command, mantendo a camada de aplicação isolada da infraestrutura.
- D2: `HandleEstateChangedUseCase.java` (interface) criada em `application/port/in/`.
- D3: `HandleEstateChangedService.java` implementado com lógica de idempotência: verifica se `hidden` já está no estado desejado antes de chamar Cal.com + save.
- D4: Bean `handleEstateChangedUseCase` registrado em `EventTypeConfig.java`.
- Compilação limpa com JDK 21.

### 2026-04-05 — Fase B concluída
- B1: `EstateChangedMessage.java` (record) criado em `eventtype/infrastructure/messaging/`. Campos: `estateId`, `newStatus`, `occurredAt`.
- B2: `EstateStatus.java` (enum) criado com valores `ACTIVE` e `INACTIVE`.
- Correção em `RabbitMQConfig.java`: removido uso de `RabbitListenerContainerFactoryConfigurer` (do autoconfigure, não do AMQP); factory configurada diretamente via `setConnectionFactory`.
- Compilação limpa com JDK 21 (`~/.jdks/corretto-21.0.6`).

### 2026-04-05 — Fase A concluída
- A1: `spring-boot-starter-amqp` adicionado ao `pom.xml`.
- A2: Bloco `spring.rabbitmq` (host/port/username/password via env vars) e `rabbitmq.queues/exchanges` adicionados ao `application.yml`.
- A3: `spring.rabbitmq.listener.simple.auto-startup: false` adicionado ao `application-dev.yml` (consumer não inicia em dev sem RabbitMQ disponível).
- A4: `RabbitMQProperties.java` (record tipado) e `RabbitMQConfig.java` criados em `infrastructure/config/`.
  - Topologia: `TopicExchange` (estate.exchange), `DirectExchange` (DLX), `Queue` (main com DLQ args), `Queue` (DLQ), `Binding` x2.
  - `Jackson2JsonMessageConverter` declarado como bean.
  - `SimpleRabbitListenerContainerFactory` configurado via `RabbitListenerContainerFactoryConfigurer` (respeita auto-config), `AcknowledgeMode.AUTO`, `defaultRequeueRejected: false`.
- Sem erros de compilação.

### 2026-04-05
- Plano aprovado. Checkpoint 0 e 1 registrados.
- Topologia definida: topic exchange `estate.exchange`, queue `cal-service.estate-changed`, DLX/DLQ.
- Contrato de mensagem definido: `{ estateId, newStatus, occurredAt }`.
- Regra de idempotência estabelecida: verificar estado atual antes de agir.
- Decisão: `HandleEstateChangedService` wireado via `@Bean` em `EventTypeConfig.java` (consistente com o padrão existente do context).
- Próximo passo: iniciar Fase A (dependência e configuração).
