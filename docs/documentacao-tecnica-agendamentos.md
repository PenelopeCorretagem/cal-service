# cal-service - Documentacao Tecnica de Agendamentos

## 1. Objetivo

Definir o desenho funcional e tecnico para que toda a integracao com Cal.com seja centralizada nesta API.

Diretriz principal:
- O frontend nao se conecta diretamente ao Cal.com.
- O frontend apenas consome endpoints da API.
- A API orquestra criacao, consulta, reagendamento, cancelamento e sincronizacao de agendamentos.

---

## 2. Escopo

Inclui:
- Fluxo de agendamentos ponta a ponta via backend.
- Contratos HTTP para frontend.
- Contratos internos (ports/use cases/adapters) no padrao Clean Architecture.
- Fluxo de webhook do Cal.com para consistencia eventual.
- Regras de negocio, erros, seguranca, observabilidade e testes.

Nao inclui:
- Implementacao de UI.
- Integracao direta do browser com widget do Cal.com.

---

## 3. Analise do fluxo MVC atual (as-is)

Resumo do comportamento observado no MVC atual:
- Existe separacao entre cliente V1 e V2 para API do Cal.com.
- Existem servicos de Booking e Webhook.
- Existem endpoints de Appointment e Booking com sobreposicao de responsabilidade.
- A criacao de agendamento via endpoint de Appointment foi bloqueada e delegada ao frontend/widget.

Principais gaps para o novo objetivo:
- Criacao de agendamento nao e backend-first.
- Contrato de dominio local e identificador de booking no Cal.com estao acoplados de forma ambigua.
- Parte das operacoes usa id local onde deveria usar bookingUid/id remoto.
- Webhook depende de correlacao fragil por metadata/cliente/data em alguns cenarios.
- Falta padronizacao clara entre API publica (frontend), caso de uso e contratos de integracao.

Conclusao do as-is:
- O MVC atual funciona como base de referencia, mas precisa evoluir para um fluxo unificado backend-first com contratos explicitos e rastreabilidade completa entre entidade local e booking remoto.

---

## 4. Diretrizes arquiteturais (to-be)

Modelo alvo no cal-service:
- Domain: regras de negocio de Appointment e invariantes.
- Application: casos de uso e orquestracao.
- Infrastructure: controllers, adapters HTTP (Cal.com/monolito), persistencia e webhook.

Regra de dependencia:
- infrastructure -> application -> domain

Principios:
- Frontend nunca chama Cal.com.
- Todo comando mutavel passa por caso de uso.
- Persistencia local e fonte de consulta interna.
- Webhook atualiza estado local por consistencia eventual.
- Idempotencia para eventos externos.

---

## 5. Requisitos funcionais

RF-01 Criar agendamento
- A API deve criar agendamento local e booking remoto no Cal.com.
- Deve retornar identificadores local e remoto.

RF-02 Consultar agendamento por id local
- A API deve retornar dados normalizados de agendamento.

RF-03 Listar agendamentos com filtros
- Filtros por cliente, corretor, imovel, status, periodo e paginacao.

RF-04 Reagendar agendamento
- A API deve enviar alteracao para Cal.com e refletir localmente.
- Confirmacao final pode ocorrer por webhook.

RF-05 Cancelar agendamento
- A API deve cancelar no Cal.com e atualizar status local.

RF-06 Confirmar/finalizar
- Se a regra de negocio exigir estados CONFIRMED/CONCLUDED, deve ser conduzida por evento oficial do Cal.com ou fluxo interno aprovado.

RF-07 Processar webhook
- Receber, validar assinatura e processar eventos BOOKING_CREATED, BOOKING_RESCHEDULED, BOOKING_CANCELLED.

RF-08 Idempotencia
- Reprocessamento de mesmo evento nao pode duplicar nem corromper estado.

RF-09 Reconciliacao
- Deve existir rotina de reconciliacao para corrigir divergencias entre base local e Cal.com.

RF-10 Auditoria
- Registrar correlationId, bookingUid, appointmentId e triggerEvent em logs estruturados.

---

## 6. Requisitos nao funcionais

RNF-01 Seguranca
- Validar assinatura HMAC do webhook.
- Proteger endpoints de comando com autenticacao/autorizacao.

RNF-02 Confiabilidade
- Timeouts e retry com backoff para chamadas ao Cal.com.
- Circuit breaker opcional.

RNF-03 Observabilidade
- Logs estruturados, metricas por endpoint e tracing distribuido.

RNF-04 Performance
- P95 de endpoints de consulta menor que 300 ms (sem chamada remota obrigatoria).

RNF-05 Compatibilidade
- Versionar contratos da API quando houver breaking change.

---

## 7. Regras de negocio

RB-01 Um agendamento deve referenciar cliente, corretor e imovel validos.

RB-02 Um agendamento deve possuir janela temporal valida:
- startDateTime < endDateTime
- durationMinutes > 0

RB-03 Status permitidos:
- PENDING
- CONFIRMED
- CANCELLED
- CONCLUDED

RB-04 Transicoes validas (exemplo):
- PENDING -> CONFIRMED
- PENDING -> CANCELLED
- CONFIRMED -> CANCELLED
- CONFIRMED -> CONCLUDED

RB-05 Reagendamento so permitido para status nao terminal.

RB-06 Campo remoto obrigatorio:
- Deve existir bookingUid (ou bookingId remoto) para qualquer operacao no Cal.com apos criacao.

RB-07 Duplicidade:
- Mesmo appointment nao pode gerar multiplos bookings remotos ativos para o mesmo horario.

---

## 8. Modelo de dados recomendado

Entidade Appointment (dominio local):
- id (local)
- bookingUid (identificador remoto no Cal.com)
- eventTypeId
- clientId
- estateAgentId
- estateId
- status
- startDateTime
- endDateTime
- durationMinutes
- createdAt
- updatedAt

Tabela de apoio para idempotencia de webhook:
- processedWebhookEvent
- campos: eventId, triggerEvent, receivedAt, hashBody

Observacao:
- Separar claramente id local e id remoto elimina ambiguidade em update/cancel.

---

## 9. Casos de uso (Application)

UC-01 CreateAppointmentUseCase
- Entrada: CreateAppointmentCommand
- Saida: AppointmentOutput
- Passos:
1. Validar dados.
2. Verificar disponibilidade/regra de conflito.
3. Criar booking no Cal.com.
4. Persistir appointment local com bookingUid.
5. Retornar resposta normalizada.

UC-02 GetAppointmentUseCase
- Entrada: appointmentId
- Saida: AppointmentOutput

UC-03 ListAppointmentsUseCase
- Entrada: filtros + paginacao
- Saida: ListAppointmentsOutput

UC-04 RescheduleAppointmentUseCase
- Entrada: appointmentId + newStart + newEnd + reason
- Saida: AppointmentOutput
- Passos:
1. Buscar appointment local.
2. Validar status e janela temporal.
3. Atualizar no Cal.com.
4. Atualizar local (ou marcar pendente de confirmacao por webhook).

UC-05 CancelAppointmentUseCase
- Entrada: appointmentId + reason
- Saida: AppointmentOutput
- Passos:
1. Buscar appointment local.
2. Cancelar no Cal.com.
3. Atualizar status local para CANCELLED.

UC-06 ProcessWebhookUseCase
- Entrada: rawBody + signature
- Saida: void
- Passos:
1. Validar assinatura.
2. Validar idempotencia.
3. Resolver appointment por bookingUid/eventId.
4. Aplicar transicao de status/data.
5. Persistir e registrar auditoria.

UC-07 ReconcileAppointmentsUseCase
- Entrada: range de datas e filtros tecnicos.
- Saida: Relatorio de divergencias.

---

## 10. Contratos HTTP para frontend (API publica)

### 10.1 POST /appointments

Objetivo:
- Criar agendamento no backend e no Cal.com.

Request:
{
  "eventTypeId": 123,
  "clientId": 10,
  "estateAgentId": 20,
  "estateId": 30,
  "startDateTime": "2026-04-10T14:00:00Z",
  "endDateTime": "2026-04-10T15:00:00Z",
  "notes": "Cliente prefere periodo da tarde"
}

Response 201:
{
  "id": 999,
  "bookingUid": "bk_abc123",
  "status": "PENDING",
  "startDateTime": "2026-04-10T14:00:00Z",
  "endDateTime": "2026-04-10T15:00:00Z"
}

Erros:
- 400 validacao
- 404 referencia nao encontrada
- 409 conflito de agenda
- 502 erro integracao Cal.com

### 10.2 GET /appointments/{id}

Response 200:
{
  "id": 999,
  "bookingUid": "bk_abc123",
  "status": "PENDING",
  "clientId": 10,
  "estateAgentId": 20,
  "estateId": 30,
  "startDateTime": "2026-04-10T14:00:00Z",
  "endDateTime": "2026-04-10T15:00:00Z"
}

### 10.3 GET /appointments

Query params sugeridos:
- clientId
- estateAgentId
- estateId
- status
- startDate
- endDate
- page
- size
- sort

### 10.4 PATCH /appointments/{id}/reschedule

Request:
{
  "startDateTime": "2026-04-11T16:00:00Z",
  "endDateTime": "2026-04-11T17:00:00Z",
  "reason": "Ajuste de disponibilidade"
}

Response 200:
{
  "id": 999,
  "bookingUid": "bk_abc123",
  "status": "PENDING",
  "startDateTime": "2026-04-11T16:00:00Z",
  "endDateTime": "2026-04-11T17:00:00Z"
}

### 10.5 POST /appointments/{id}/cancel

Request:
{
  "reason": "Cliente desistiu"
}

Response 200:
{
  "id": 999,
  "bookingUid": "bk_abc123",
  "status": "CANCELLED"
}

### 10.6 POST /webhooks/calcom

Headers obrigatorios:
- X-Cal-Signature-256

Body:
- raw JSON do evento Cal.com

Response:
- 200 processado
- 401 assinatura invalida
- 409 evento duplicado (opcional)

---

## 11. Contratos internos (Ports)

Porta de saida CalComBookingGateway:
- createBooking(CreateBookingRequest) -> BookingResult
- getBooking(String bookingUid) -> BookingResult
- listBookings(BookingFilter) -> List<BookingResult>
- rescheduleBooking(String bookingUid, RescheduleBookingRequest) -> BookingResult
- cancelBooking(String bookingUid, CancelBookingRequest) -> BookingResult

Porta de saida AppointmentRepository:
- save(Appointment)
- findById(Long)
- findByBookingUid(String)
- search(AppointmentFilter, Pageable)

Porta de entrada WebhookProcessor:
- process(rawBody, signature)

Contrato de erro padrao (application):
- code
- message
- details
- traceId

---

## 12. Fluxos ponta a ponta

### 12.1 Fluxo de criacao

1. Frontend envia POST /appointments.
2. Controller mapeia request para command.
3. Use case valida regras de negocio.
4. Gateway cria booking no Cal.com.
5. Repository persiste appointment com bookingUid.
6. API responde 201.
7. Evento BOOKING_CREATED recebido via webhook confirma consistencia eventual.

### 12.2 Fluxo de reagendamento

1. Frontend envia PATCH /appointments/{id}/reschedule.
2. Use case carrega appointment e valida estado.
3. Gateway envia update ao Cal.com usando bookingUid.
4. Persistencia local atualiza datas/status.
5. Webhook BOOKING_RESCHEDULED reconfirma estado.

### 12.3 Fluxo de cancelamento

1. Frontend envia POST /appointments/{id}/cancel.
2. Use case valida estado.
3. Gateway cancela booking remoto.
4. Persistencia local seta CANCELLED.
5. Webhook BOOKING_CANCELLED garante consistencia.

### 12.4 Fluxo de webhook

1. Cal.com chama POST /webhooks/calcom.
2. API valida assinatura HMAC.
3. API verifica idempotencia por eventId/hash.
4. API resolve appointment por bookingUid.
5. API aplica transicao e persiste.
6. API loga auditoria e retorna 200.

### 12.5 Fluxo de reconciliacao

1. Job tecnico consulta bookings remotos por janela.
2. Cruza com base local.
3. Corrige divergencias conforme politica definida.
4. Emite relatorio tecnico.

---

## 13. Politica de erros e status HTTP

Padrao de resposta de erro:
{
  "status": 409,
  "code": "APPOINTMENT_CONFLICT",
  "message": "Horario indisponivel para o corretor",
  "traceId": "b3f5c6..."
}

Mapeamento sugerido:
- 400 entrada invalida
- 401 nao autenticado
- 403 sem permissao
- 404 recurso nao encontrado
- 409 conflito de estado/agenda
- 422 regra de negocio violada
- 502 falha na integracao externa
- 500 erro interno nao tratado

---

## 14. Seguranca

- Validar JWT para endpoints de comando e consulta privada.
- Validar assinatura de webhook com comparacao segura (constant-time).
- Armazenar segredo do webhook em cofre/variavel protegida.
- Aplicar rate limit em endpoints sensiveis.

---

## 15. Observabilidade e operacao

Logs obrigatorios por operacao:
- traceId
- appointmentId
- bookingUid
- eventTypeId
- triggerEvent (quando webhook)
- latencyMs
- resultado (success/failure)

Metricas recomendadas:
- appointments.create.success/failure
- appointments.reschedule.success/failure
- appointments.cancel.success/failure
- webhook.process.success/failure
- webhook.signature.invalid
- integration.calcom.latency

Alertas recomendados:
- taxa de erro > 5% em 5 min
- webhook.signature.invalid acima do baseline
- backlog de reconciliacao acima do limite

---

## 16. Estrategia de testes

### 16.1 Domain
- Regras de transicao de status.
- Regras de janela temporal.
- Regras de reagendamento e cancelamento.

### 16.2 Application
- Casos de uso com mocks de ports.
- Cenarios de sucesso e falha de integracao.
- Idempotencia de webhook.

### 16.3 Infrastructure
- Testes de adapter HTTP com respostas reais simuladas do Cal.com.
- Testes de controller (contrato HTTP).
- Testes de persistencia.

### 16.4 Contrato
- OpenAPI contract tests para endpoints publicos.
- Contract tests de webhook payload.

### 16.5 E2E
- Criar -> consultar -> reagendar -> cancelar -> reconciliar.

---

## 17. Plano de migracao (incremental)

Fase 1 - Contratos e modelo
1. Introduzir bookingUid no modelo local.
2. Definir novos requests/responses de agendamento.
3. Padronizar erro e traceId.

Fase 2 - Casos de uso
1. Implementar Create/Reschedule/Cancel/Get/List com ports.
2. Implementar gateway de booking no Cal.com.

Fase 3 - Webhook robusto
1. Validacao de assinatura e idempotencia.
2. Correlacao por bookingUid como chave primaria de reconciliacao.

Fase 4 - Cutover
1. Remover dependencia do widget/frontend para criacao.
2. Atualizar frontend para chamar apenas API.
3. Monitorar metricas e reconciliacao.

Fase 5 - Hardening
1. Retry/backoff/circuit breaker.
2. Alertas e dashboards finais.

---

## 18. Criterios de aceite

CA-01 Frontend cria agendamento sem chamar Cal.com diretamente.

CA-02 Toda operacao mutavel de agendamento passa por endpoint da API.

CA-03 Toda operacao mutavel persiste bookingUid localmente.

CA-04 Webhook processa eventos de forma idempotente.

CA-05 Erros retornam contrato padrao com code/message/traceId.

CA-06 Suite de testes cobre fluxos criticos de criacao, reagendamento e cancelamento.

---

## 19. Decisoes tecnicas abertas

DT-01 Confirmacao e conclusao
- Definir se vem exclusivamente de evento Cal.com ou tambem de fluxo interno.

DT-02 Fonte da verdade de disponibilidade
- Definir se consulta local e suficiente ou se precisa consulta online ao Cal.com em tempo real.

DT-03 Politica de consistencia
- Definir quando estado local pode ser atualizado otimisticamente antes do webhook.

DT-04 Chave de correlacao
- Confirmar bookingUid como chave principal para update/cancel/reconciliacao.

---

## 20. Resultado esperado

Com este desenho, a API passa a ser o unico ponto de integracao com Cal.com, reduz o acoplamento do frontend, padroniza contratos e melhora rastreabilidade, seguranca e confiabilidade do fluxo de agendamentos.
